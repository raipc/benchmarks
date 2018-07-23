package com.github.raipc;

import org.apache.commons.lang3.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@State(Scope.Thread)
public class ToPrimitiveArray {
    private List<Long> list;

    @Setup
    public void setup() {
        list = LongStream.range(0, 100000).boxed().collect(Collectors.toList());
    }

    @Benchmark
    public long[] toArrayApache() {
        Long[] values = list.toArray(new Long[list.size()]);
        return ArrayUtils.toPrimitive(values);
    }

    @Benchmark
    public long[] toArrayStream() {
        return list.stream().mapToLong(a -> a).toArray();
    }

    public static void main(String[] args) throws RunnerException {
        final Options build = new OptionsBuilder()
                .include("ToPrimitiveArray.*")
                .forks(1)
                .mode(Mode.SampleTime)
                .warmupTime(new TimeValue(2, TimeUnit.SECONDS))
                .measurementTime(new TimeValue(3, TimeUnit.SECONDS))
                .timeUnit(TimeUnit.MICROSECONDS)
                .build();
        new Runner(build).run();
    }
}
