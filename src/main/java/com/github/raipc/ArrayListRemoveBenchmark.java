package com.github.raipc;

import com.google.common.collect.Iterables;
import org.openjdk.jmh.annotations.*;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ArrayListRemoveBenchmark {
    private static final int SIZE = 100000;

    public enum PredicateEnum {
        FIRST(integer -> integer == 0, SIZE - 1),
        MIDDLE(integer -> integer == SIZE / 2, SIZE - 1),
        TENTH(integer -> integer % 10 == 9, SIZE - SIZE / 10),
        HALF(integer -> integer % 2 == 0, SIZE / 2),
        THIRTY_PERCENT(integer -> integer % 10 < 3, SIZE * 7 / 10),
        SEVENTY_PERCENT(integer -> integer % 10 < 7, SIZE * 3 / 10),
        ALL(integer -> true, 0),
        NONE(integer -> false, SIZE);
        public final Predicate<Integer> predicate;
        public final int afterFiltering;

        PredicateEnum(Predicate<Integer> predicate, int afterFiltering) {
            this.predicate = predicate;
            this.afterFiltering = afterFiltering;
        }
    }
    private List<Integer> numbers;

    @Param({"FIRST", "MIDDLE", "TENTH", "ALL", "NONE", "HALF", "THIRTY_PERCENT", "SEVENTY_PERCENT"})
    private PredicateEnum predicate;

    @Setup(Level.Invocation)
    public void prepareList() {
        numbers = IntStream.range(0, SIZE).boxed().collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> naiveIterator() {
        assertListPopulated();
        final Predicate<Integer> pred = predicate.predicate;
        final Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            if (pred.test(iterator.next())) {
                iterator.remove();
            }
        }
        assertListFiltered();
        return numbers;
    }

    @Benchmark
    public List<Integer> removeIf() {
        assertListPopulated();
        final Predicate<Integer> pred = predicate.predicate;
        numbers.removeIf(pred);
        assertListFiltered();
        return numbers;
    }

    @Benchmark
    public List<Integer> listIteratorReverse() {
        assertListPopulated();
        final Predicate<Integer> pred = predicate.predicate;
        if (!numbers.isEmpty()) {
            final ListIterator<Integer> iterator = numbers.listIterator(numbers.size());
            while (iterator.hasPrevious()) {
                if (pred.test(iterator.previous())) {
                    iterator.remove();
                }
            }
        }
        assertListFiltered();
        return numbers;
    }

    @Benchmark
    public List<Integer> removeWithGuava() {
        assertListPopulated();
        final Predicate<Integer> pred = predicate.predicate;
        Iterables.removeIf(numbers, pred::test);
        assertListFiltered();
        return numbers;
    }
    
    private void assertListPopulated() {
        if (numbers.size() != SIZE) {
            throw new RuntimeException("List is not populated fully. List size: " + numbers.size());
        }
    }

    private void assertListFiltered() {
        if (numbers.size() != predicate.afterFiltering) {
            throw new RuntimeException("List is not filtered. List size: " + numbers.size());
        }
    }
}
