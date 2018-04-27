package com.github.raipc;

import com.carrotsearch.hppc.DoubleIntOpenHashMap;
import com.carrotsearch.hppc.IntArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.DoubleIntHashMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class PrimitiveCollectionsBenchmark {
    private static final int COUNT_BIG = 1_000_000;
    private static final int COUNT_SMALL = 10;
    private static final int[] SMALL_ARRAY = new Random().ints(10, 0, 10).toArray();
    private static final int[] BIG_ARRAY = new Random().ints(1_000_000, 0, 1000).toArray();

    @Benchmark
    public IntArrayList hppcIntArrayList() {
        final IntArrayList intArrayList = IntArrayList.newInstance();
        for (int i = 0; i < COUNT_BIG; i++) {
            intArrayList.add(i);
        }
        return intArrayList;
    }

    @Benchmark
    public org.eclipse.collections.impl.list.mutable.primitive.IntArrayList eclipseIntArrayList() {
        final org.eclipse.collections.impl.list.mutable.primitive.IntArrayList intArrayList = new org.eclipse.collections.impl.list.mutable.primitive.IntArrayList();
        for (int i = 0; i < COUNT_BIG; i++) {
            intArrayList.add(i);
        }
        return intArrayList;
    }

    @Benchmark
    public IntArrayList hppcIntArrayListSmall() {
        final IntArrayList intArrayList = IntArrayList.newInstance();
        for (int i = 0; i < COUNT_SMALL; i++) {
            intArrayList.add(i);
        }
        return intArrayList;
    }

    @Benchmark
    public org.eclipse.collections.impl.list.mutable.primitive.IntArrayList eclipseIntArrayListSmall() {
        final org.eclipse.collections.impl.list.mutable.primitive.IntArrayList intArrayList = new org.eclipse.collections.impl.list.mutable.primitive.IntArrayList();
        for (int i = 0; i < COUNT_SMALL; i++) {
            intArrayList.add(i);
        }
        return intArrayList;
    }

    @Benchmark
    public void hppcDoubleIntMapSmall() {
        final DoubleIntOpenHashMap doubleIntOpenHashMap = new DoubleIntOpenHashMap();
        for (int item : SMALL_ARRAY) {
            doubleIntOpenHashMap.putOrAdd(item, 1, 1);
        }
    }

    @Benchmark
    public void hppcDoubleIntMapBig() {
        final DoubleIntOpenHashMap doubleIntOpenHashMap = new DoubleIntOpenHashMap();
        for (int item : BIG_ARRAY) {
            doubleIntOpenHashMap.putOrAdd(item, 1, 1);
        }
    }

    @Benchmark
    public void eclipseDoubleIntMapSmall() {
        final DoubleIntHashMap doubleIntHashMap = new DoubleIntHashMap();
        for (int item : SMALL_ARRAY) {
            doubleIntHashMap.addToValue(item, 1);
        }
    }

    @Benchmark
    public void eclipseDoubleIntMapBig() {
        final DoubleIntHashMap doubleIntHashMap = new DoubleIntHashMap();
        for (int item : BIG_ARRAY) {
            doubleIntHashMap.addToValue(item, 1);
        }
    }
}
