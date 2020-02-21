package com.github.raipc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 20)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class CircularDequeBenchmark {
	@Param({"10", "100000",  "1000000"})
	int size;
	@Param({"arrayList", "arrayDeque", "linkedList"})
	String type;
	private Window collection;

	private static Window initCollection(String type, int size) {
		switch (type) {
			case "arrayList": return new ArrayListWindowImpl(new ArrayList<>(size));
			case "arrayDeque": return new ArrayDequeWindowImpl(new ArrayDeque<>(size));
			case "linkedList": return new LinkedListWindowImpl(new LinkedList<>());
			default: throw new IllegalArgumentException("type " + type + " unsupported");
		}
	}

	@Setup(value = Level.Trial)
	public void init() {
		collection = initCollection(type, size);
		for (int i = 0; i < size; i++) {
			collection.addLast(1000);
		}
	}

	@Benchmark
	public void removeAndAdd(Blackhole bh) {
		collection.removeFirst();
		collection.addLast(Integer.MAX_VALUE);
		bh.consume(collection);
	}

	public static void main(String[] args) throws RunnerException {
		String benchmark = ".*" + CircularDequeBenchmark.class.getSimpleName() + ".*";
		Options opt = new OptionsBuilder()
			.include(benchmark)
			.warmupIterations(1)
			.warmupTime(TimeValue.seconds(3))
			.measurementIterations(2)
			.forks(1)
			.build();
		new Runner(opt).run();
	}

	private interface Window {
		void removeFirst();
		void addLast(int element);
	}

	@RequiredArgsConstructor
	public static class LinkedListWindowImpl implements Window {
		private final LinkedList<Integer> linkedList;
		@Override
		public void removeFirst() {
			linkedList.removeFirst();
		}

		@Override
		public void addLast(int element) {
			linkedList.addLast(element);
		}
	}

	@RequiredArgsConstructor
	public static class ArrayDequeWindowImpl implements Window {
		private final ArrayDeque<Integer> deque;
		@Override
		public void removeFirst() {
			deque.removeFirst();
		}

		@Override
		public void addLast(int element) {
			deque.addLast(element);
		}
	}

	@RequiredArgsConstructor
	public static class ArrayListWindowImpl implements Window {
		private final ArrayList<Integer> arrayList;
		@Override
		public void removeFirst() {
			arrayList.remove(0);
		}

		@Override
		public void addLast(int element) {
			arrayList.add(element);
		}
	}


}
