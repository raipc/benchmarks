package com.github.raipc;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class DataSerializationBenchmark {
    @Param({"1000000"})
    private int size;

    private double[] data;
    private double sum;

    @Setup
    public void prepareArray() {
        data = IntStream.range(0, size).mapToDouble(i -> RandomUtils.nextDouble()).toArray();
        sum = Arrays.stream(data).sum();
    }

    @Benchmark
    public long firstMethod() throws IOException {
        File seriesFile = File.createTempFile("first", ".data");
        try {
            ByteBuffer seriesBytes = ByteBuffer.allocate(data.length * Double.BYTES);
            Arrays.stream(data).forEach(seriesBytes::putDouble);
            FileUtils.writeByteArrayToFile(seriesFile, seriesBytes.array());
            final long length = seriesFile.length();
            if (length != 8 * size) {
                throw new IllegalStateException("Expected size: " + (8 * size) + ", given: " + length);
            }
            return length;
        } finally {
            FileUtils.forceDelete(seriesFile);
        }
    }

//    @Benchmark
    public long secondMethod() throws IOException {
        File seriesFile = File.createTempFile("second", ".data");
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(seriesFile), data.length * Double.BYTES))) {
            for (double value : data) {
                out.writeDouble(value);
            }
            out.flush();
            final long length = seriesFile.length();
            if (length != 8 * size) {
                throw new IllegalStateException("Expected size: " + (8 * size) + ", given: " + length);
            }
            return length;
        } finally {
            FileUtils.forceDelete(seriesFile);
        }
    }

//    @Benchmark
    public long thirdMethod() throws IOException {
        File seriesFile = File.createTempFile("third", ".data");
        try (final RandomAccessFile raf = new RandomAccessFile(seriesFile, "rw");
//                FileOutputStream out = new FileOutputStream(raf.getFD());
             FileChannel fileChannel = raf.getChannel()) {
            MappedByteBuffer seriesBytes = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, data.length * Double.BYTES);
            Arrays.stream(data).forEach(seriesBytes::putDouble);
            final long length = seriesFile.length();
            if (length != 8 * size) {
                throw new IllegalStateException("Expected size: " + (8 * size) + ", given: " + length);
            }
            return length;
        } finally {
            FileUtils.forceDelete(seriesFile);
        }
    }

    @Benchmark
    public long forthMethod() throws IOException {
        File seriesFile = File.createTempFile("first", ".data");
        try (FileOutputStream fos = new FileOutputStream(seriesFile);) {
            byte[] bytes = new byte[8];
            for (double datum : data) {
                long rawLongBits = Double.doubleToRawLongBits(datum);
                for (int i = 7; i > 0; i--) {
                    bytes[i] = (byte) rawLongBits;
                    rawLongBits >>>= 8;
                }
                bytes[0] = (byte) rawLongBits;
                fos.write(bytes);
            }
            fos.flush();
            final long length = seriesFile.length();
            if (length != 8 * size) {
                throw new IllegalStateException("Expected size: " + (8 * size) + ", given: " + length);
            }
            return length;
        } finally {
            FileUtils.forceDelete(seriesFile);
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        final DataSerializationBenchmark bench = new DataSerializationBenchmark();
        bench.size = 1000;
        bench.prepareArray();
        System.out.println(bench.firstMethod());
        System.out.println(bench.secondMethod());
        System.out.println(bench.thirdMethod());
        System.out.println(bench.forthMethod());

        Options opt = new OptionsBuilder()
                .include(".*" + DataSerializationBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(3))
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .addProfiler(GCProfiler.class)
                .build();
        new Runner(opt).run();
    }
}
