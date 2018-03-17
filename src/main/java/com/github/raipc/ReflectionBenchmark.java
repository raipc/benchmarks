package com.github.raipc;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ReflectionBenchmark {
	private static final String STR1_GETTER = "getStr1";

	static class A {
		private String str1;
		private String str2;

		public String getStr1() {
			return str1;
		}

		public String getStr2() {
			return str2;
		}

	}

	static class B extends A {
		@Override
		public String getStr2() {
			return super.getStr2();
		}
	}

	@Benchmark
	public Method getMethodForStr1FromB() throws NoSuchMethodException {
		return B.class.getMethod(STR1_GETTER);
	}

	@Benchmark
	public Method getMethodForStr1FromA() throws NoSuchMethodException {
		return A.class.getMethod(STR1_GETTER);
	}

	@Benchmark
	public Method getDeclaredMethodForStr1FromA() throws NoSuchMethodException {
		return A.class.getDeclaredMethod(STR1_GETTER);
	}

	@Benchmark
	public Method getDeclaredMethodForStr1FromB() throws NoSuchMethodException {
		try {
			return B.class.getDeclaredMethod(STR1_GETTER);
		} catch (NoSuchMethodException e) {
			return B.class.getSuperclass().getDeclaredMethod(STR1_GETTER);
		}
	}
}
