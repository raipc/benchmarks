package xyz.raipc;


import org.openjdk.jmh.annotations.*;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class EnumValueOfBenchmark {
    enum DataType {
        SHORT,
        INTEGER,
        FLOAT,
        LONG,
        DOUBLE,
        DECIMAL
    }

    @Param(value = "datatype")
    private String datatype;

    @Benchmark
    public DataType parseUsingValueOf() {
        if (datatype != null) {
            try {
                return DataType.valueOf(datatype.toUpperCase(Locale.US));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
        return null;
    }

    @Benchmark
    public DataType parseUsingIteration() {
        if (datatype != null) {
            for (DataType dataType : DataType.values()) {
                if (dataType.name().equalsIgnoreCase(datatype)) {
                    return dataType;
                }
            }
        }

        return null;
    }




}
