package com.github.raipc.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import lombok.experimental.Accessors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ComputeIfAbsentBenchmark {
    @Benchmark
    public ColumnSizeContext withLambda() {
        final Map<Integer, ColumnSizeContext> map = new HashMap<>();
        consumeRowLambda(4, 1, map);
        consumeRowLambda(4, 2, map);
        consumeRowLambda(1, 3, map);
        consumeRowLambda(8, 4, map);
        consumeRowLambda(8, 5, map);
        consumeRowLambda(8, 6, map);
        return consumeRowLambda(8, 7, map);
    }

    @Benchmark
    public ColumnSizeContext withLambdaNonCapturing() {
        final Map<Integer, ColumnSizeContext> map = new HashMap<>();
        consumeRowLambdaNC(4, 1, map);
        consumeRowLambdaNC(4, 2, map);
        consumeRowLambdaNC(1, 3, map);
        consumeRowLambdaNC(8, 4, map);
        consumeRowLambdaNC(8, 5, map);
        consumeRowLambdaNC(8, 6, map);
        return consumeRowLambdaNC(8, 7, map);
    }

    @Benchmark
    public ColumnSizeContext withoutLambda() {
        final Map<Integer, ColumnSizeContext> map = new HashMap<>();
        consumeRow(4, 1, map);
        consumeRow(4, 2, map);
        consumeRow(1, 3, map);
        consumeRow(8, 4, map);
        consumeRow(8, 5, map);
        consumeRow(8, 6, map);
        return consumeRow(8, 7, map);
    }

    private static ColumnSizeContext consumeRowLambda(int columnCount, int rowNumber, Map<Integer, ColumnSizeContext> map) {
        return map.computeIfAbsent(columnCount, k -> new ColumnSizeContext(rowNumber));
    }

    private static ColumnSizeContext consumeRowLambdaNC(int columnCount, int rowNumber, Map<Integer, ColumnSizeContext> map) {
        return map.computeIfAbsent(columnCount, k -> new ColumnSizeContext());
    }

    private static ColumnSizeContext consumeRow(int columnCount, int rowNumber, Map<Integer, ColumnSizeContext> map) {
        ColumnSizeContext columnSizeContext = map.get(columnCount);
        if (columnSizeContext == null) {
            columnSizeContext = new ColumnSizeContext(rowNumber);
            map.put(columnCount, columnSizeContext);
        }
        return columnSizeContext;
    }

    private static final class ColumnSizeContext {
        private final List<Range> ranges = new ArrayList<>();
        private long totalRows = 0L;

        private ColumnSizeContext(int rowNumber) {
            this.ranges.add(new Range().setStart(rowNumber));
        }

        private ColumnSizeContext() {
            this.ranges.add(new Range());
        }
    }

    @Data
    @Accessors(chain = true)
    private static final class Range {
        private long start;
        private long end = -1L;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + ComputeIfAbsentBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(3))
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .addProfiler(GCProfiler.class)
                .build();
        new Runner(opt).run();
    }
}
