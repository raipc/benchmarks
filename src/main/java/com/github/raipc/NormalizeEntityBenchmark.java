package com.github.raipc;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import net.openhft.chronicle.core.util.StringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class NormalizeEntityBenchmark {
	@Param({"atsd", "01115b253436df2e42209aab73c7e1fad9a2afb94d2ffd26e71157525e66bbee", "GAZP [TQBR]", " entity  "})
	String entity;

	@Benchmark
	public String currentImpl() {
		final String prepared = entity.toLowerCase().trim();

		int len = 0;
		char[] res = new char[prepared.length()];
		for (int i = 0; i < prepared.length(); i++) {
			char c = prepared.charAt(i);
			if (Character.isWhitespace(c)) {
				c = '_';
			}
			res[len++] = c;
		}

		if (len == 0) {
			throw new IllegalArgumentException("Illegal name: '" + entity + "'");
		}

		if (len == res.length) {
			return new String(res);
		}
		return new String(res, 0, len);
	}

	@Benchmark
	public String toCharArray() {
		final String prepared = entity.trim().toLowerCase(Locale.US);
		final int length = prepared.length();
		if (length == 0) {
			throw new IllegalArgumentException("Illegal name: '" + entity + "'");
		}
		final char[] res = prepared.toCharArray();
		boolean modified = false;
		for (int i = 0; i < length; i++) {
			char c = res[i];
			if (Character.isWhitespace(c)) {
				res[i] = '_';
				modified = true;
			}
		}
		return modified ? new String(res) : entity;
	}

	@Benchmark
	public String manualReplace() {
		final String entityLc = entity.trim().toLowerCase(Locale.US);
		final int length = entityLc.length();
		if (length == 0) {
			throw new IllegalArgumentException("Illegal name: '" + entity + "'");
		}
		char[] arr = null;
		for (int i = 0; i < length; i++) {
			char c = entityLc.charAt(i);
			if (Character.isWhitespace(c)) {
				if (arr == null) {
					arr = entityLc.toCharArray();
				}
				arr[i] = '_';
			}
		}
		return arr == null ? entityLc : new String(arr);
	}

	@Benchmark
	public String manualTrim() {
		String entityLc = entity.toLowerCase(Locale.US);
		final int length = entityLc.length();
		int startIncl = 0;
		int endIncl = length - 1;
		while (startIncl <= endIncl && entityLc.charAt(startIncl) <= ' ') {
			++startIncl;
		}
		while (endIncl >= startIncl && entityLc.charAt(endIncl) <= ' ') {
			--endIncl;
		}
		if (endIncl <= startIncl) {
			throw new IllegalArgumentException("Illegal name: '" + entity + "'");
		}
		if (startIncl > 0 || endIncl + 1 < length) {
			char[] arr = entityLc.toCharArray();
			for (int i = startIncl; i <= endIncl; i++) {
				char c = arr[i];
				if (Character.isWhitespace(c)) {
					arr[i] = '_';
				}
			}
			return new String(arr, startIncl, endIncl - startIncl + 1);
		} else {
			char[] arr = null;
			for (int i = 0; i <= endIncl; i++) {
				char c = entityLc.charAt(i);
				if (Character.isWhitespace(c)) {
					if (arr == null) {
						arr = entityLc.toCharArray();
					}
					arr[i] = '_';
				}
			}
			return arr == null ? entityLc : new String(arr);
		}
	}

	@Benchmark
	public String unsafe() {
		final String entityLc = entity.toLowerCase(Locale.US);
		final char[] internalArray = StringUtils.extractChars(entityLc);
		final int lengthBeforeTrim = internalArray.length;
		char[] arr = internalArray;
		int start = 0;
		int end = lengthBeforeTrim;
		while (start < end && entityLc.charAt(start) <= ' ') {
			++start;
		}
		while (end > start && entityLc.charAt(end - 1) <= ' ') {
			--end;
		}
		final int len = end - start;
		if (len <= 0) {
			throw new IllegalArgumentException("Illegal name: '" + entity + "'");
		}
		if (len != lengthBeforeTrim) {
			arr = new char[end - start];
			System.arraycopy(internalArray, start, arr, 0, end - start);
		}
		for (int i = 0; i < len; i++) {
			char c = arr[i];
			if (Character.isWhitespace(c)) {
				if (arr == internalArray) {
					arr = new char[len];
					System.arraycopy(internalArray, start, arr, 0, end - start);
				}
				arr[i] = '_';
			}
		}
		return arr == internalArray ? entityLc : StringUtils.newString(arr);
	}

	public static void main(String[] args) throws RunnerException {
		String benchmark = ".*" + NormalizeEntityBenchmark.class.getSimpleName() + ".*";
		Options opt = new OptionsBuilder()
			.include(benchmark)
			.warmupIterations(1)
			.warmupTime(TimeValue.seconds(3))
			.measurementIterations(2)
			.addProfiler( GCProfiler.class )
			.forks(1)
			.build();
		new Runner(opt).run();
	}
}
