package com.clickhouse.benchmark.clients;

import com.clickhouse.benchmark.data.DataSet;
import com.clickhouse.client.api.internal.ClickHouseLZ4OutputStream;
import com.clickhouse.data.ClickHouseOutputStream;
import com.clickhouse.data.stream.Lz4OutputStream;
import net.jpountz.lz4.LZ4Factory;
import org.openjdk.jmh.annotations.Benchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

public class Compression extends BenchmarkBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(Compression.class);

    static final int COMPRESS_BUFFER_SIZE = 64 * 1024; // 64K

    @Benchmark
    public void CompressingOutputStreamV1(DataState dataState) {
        DataSet dataSet = dataState.dataSet;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ClickHouseOutputStream out =
                new Lz4OutputStream(baos, COMPRESS_BUFFER_SIZE, null)) {
            for (byte[] bytes : dataSet.getBytesList(dataSet.getFormat())) {
                out.write(bytes);
            }
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
        }
    }

    private static final LZ4Factory factory = LZ4Factory.fastestInstance();

    @Benchmark
    public void CompressingOutputStreamV2(DataState dataState) {
        DataSet dataSet = dataState.dataSet;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ClickHouseLZ4OutputStream out = new ClickHouseLZ4OutputStream(baos,
                     factory.fastCompressor(), COMPRESS_BUFFER_SIZE)) {
            for (byte[] bytes : dataSet.getBytesList(dataSet.getFormat())) {
                out.write(bytes);
            }
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
        }
    }
}
