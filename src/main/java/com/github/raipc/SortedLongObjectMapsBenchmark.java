package com.github.raipc;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMaps;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import org.apache.commons.lang3.mutable.MutableLong;
import org.eclipse.collections.api.block.procedure.primitive.LongObjectProcedure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class SortedLongObjectMapsBenchmark {
	@State(Scope.Benchmark)
	public static class BenchmarkState {
		@Param({"jdk", "avl", "rbtree"})
		private String impl;
		@Param({"1", "10", "1000", "1000000"})
		private int size;
		private SimpleMap<Integer> map;

		@Setup
		public void setup() {
			switch (impl) {
				case "jdk": map = new JDKTreeMap<>(); break;
				case "avl": map = new FastUtilAVLTreeMap<>(); break;
				case "rbtree": map = new FastUtilRBTreeMap<>(); break;
				default: throw new IllegalStateException("unknown impl: " + impl);
			}
		}
	}

	@State(Scope.Benchmark)
	public static class NonEmptyState {
		@Param({"jdk", "avl", "rbtree"})
		private String impl;
		@Param({"1", "10", "1000", "1000000"})
		private int size;
		private SimpleMap<Integer> map;

		@Setup
		public void setup() {
			switch (impl) {
				case "jdk": map = new JDKTreeMap<>(); break;
				case "avl": map = new FastUtilAVLTreeMap<>(); break;
				case "rbtree": map = new FastUtilRBTreeMap<>(); break;
				default: throw new IllegalStateException("unknown impl: " + impl);
			}
			for (int i = 0; i < size; i++) {
				map.put(i, i);
			}
		}
	}

	@Benchmark
	public void putSequential(BenchmarkState state) {
		for (int i = 0; i < state.size; i++) {
			state.map.put(i, i);
		}
	}

	@Benchmark
	public Integer getExisting(NonEmptyState state) {
		return state.map.get(state.size / 2);
	}

	@Benchmark
	public Integer getNonExisting(NonEmptyState state) {
		return state.map.get(state.size + 100);
	}

	@Benchmark
	public long iterating(NonEmptyState state) {
		MutableLong sum = new MutableLong();
		state.map.forEach((k, v) -> sum.add(k));
		return sum.longValue();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*SortedLongObjectMapsBenchmark.*.*")
				.warmupIterations(2)
				.warmupTime(TimeValue.seconds(3))
				.measurementIterations(3)
				.measurementTime(TimeValue.seconds(4))
				.mode(Mode.Throughput)
				.timeUnit(TimeUnit.MILLISECONDS)
				.forks(1)
				.build();

		new Runner(opt).run();
	}

	private interface SimpleMap<T> {
		void put(long key, T value);
		T get(long key);
		void forEach(LongObjectProcedure<T> procedure);
	}

	private static class JDKTreeMap<T> implements SimpleMap<T> {
		private final TreeMap<Long, T> delegate = new TreeMap<>();

		@Override
		public void put(long key, T value) {
			delegate.put(key, value);
		}

		@Override
		public T get(long key) {
			return delegate.get(key);
		}

		@Override
		public void forEach(LongObjectProcedure<T> procedure) {
			delegate.forEach(procedure::value);
		}
	}


	private static class FastUtilAVLTreeMap<T> implements SimpleMap<T> {
		private final Long2ObjectAVLTreeMap<T> delegate = new Long2ObjectAVLTreeMap<>();

		@Override
		public void put(long key, T value) {
			delegate.put(key, value);
		}

		@Override
		public T get(long key) {
			return delegate.get(key);
		}

		@Override
		public void forEach(LongObjectProcedure<T> procedure) {
			final ObjectBidirectionalIterator<Long2ObjectMap.Entry<T>> iterator = Long2ObjectSortedMaps.fastIterator(delegate);
			while (iterator.hasNext()) {
				final Long2ObjectMap.Entry<T> next = iterator.next();
				procedure.value(next.getLongKey(), next.getValue());
			}
		}
	}

	private static class FastUtilRBTreeMap<T> implements SimpleMap<T> {
		private final Long2ObjectRBTreeMap<T> delegate = new Long2ObjectRBTreeMap<>();

		@Override
		public void put(long key, T value) {
			delegate.put(key, value);
		}

		@Override
		public T get(long key) {
			return delegate.get(key);
		}

		@Override
		public void forEach(LongObjectProcedure<T> procedure) {
			final ObjectBidirectionalIterator<Long2ObjectMap.Entry<T>> iterator = Long2ObjectSortedMaps.fastIterator(delegate);
			while (iterator.hasNext()) {
				final Long2ObjectMap.Entry<T> next = iterator.next();
				procedure.value(next.getLongKey(), next.getValue());
			}
		}
	}
}
