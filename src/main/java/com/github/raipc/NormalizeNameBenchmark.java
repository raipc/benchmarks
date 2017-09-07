package com.github.raipc;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class NormalizeNameBenchmark {
	@Param({" Metric Value with Spaces   ", "metric_with_underscores", "test_m"})
	private String metricName;

	@Benchmark
	public String normalizeName() {
		return doNormalizeName(metricName);
	}

	@Benchmark
	public String normalizePostLowercaseName() {
		return doNormalizePostLowercaseName(metricName);
	}

	@Benchmark
	public String normalizePostLowercaseNameKeep() {
		return doNormalizePostLowercaseNameKeepLength(metricName);
	}

	@Benchmark
	public String normalizePostLowercaseNameNoTrim() {
		return doNormalizePostLowercaseNameNoTrim(metricName);
	}

	@Benchmark
	public String normalizeNameSb() {
		return doNormalizeNameSb(metricName);
	}

	private static String doNormalizeName(String rawName) {
		final String prepared = rawName.toLowerCase().trim();

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
			throw new IllegalArgumentException("Illegal name: '" + rawName + "'");
		}

		if (len == res.length) {
			return new String(res);
		}
		return new String(res, 0, len);
	}

	private static String doNormalizePostLowercaseName(String rawName) {
		final String prepared = rawName.trim();

		int len = 0;
		char[] res = new char[prepared.length()];
		for (int i = 0; i < prepared.length(); i++) {
			char c = prepared.charAt(i);
			if (Character.isWhitespace(c)) {
				c = '_';
			} else {
				c = Character.toLowerCase(c);
			}
			res[len++] = c;
		}

		if (len == 0) {
			throw new IllegalArgumentException("Illegal name: '" + rawName + "'");
		}

		if (len == res.length) {
			return new String(res);
		}
		return new String(res, 0, len);
	}

	private static String doNormalizePostLowercaseNameKeepLength(String rawName) {
		final String prepared = rawName.trim();
		final int length = prepared.length();
		if (length == 0) {
			throw new IllegalArgumentException("Illegal name: '" + rawName + "'");
		}
		char[] res = new char[length];
		for (int i = 0; i < length; ++i) {
			char c = prepared.charAt(i);
			if (Character.isWhitespace(c)) {
				res[i] = '_';
			} else {
				res[i] = Character.toLowerCase(c);
			}
		}

		return new String(res);
	}

	private static String doNormalizePostLowercaseNameNoTrim(String rawName) {
		final int length = rawName.length();
		final int startIndex = findStartIndex(rawName, length);
		final int endIndex = findEndIndex(rawName, length);

		final int newLen = endIndex - startIndex + 1;
		if (newLen <= 1) {
			throw new IllegalArgumentException("Illegal name: '" + rawName + "'");
		}
		char[] res = new char[newLen];
		int resI = 0;
		for (int i = startIndex; i <= endIndex; ++i) {
			char c = rawName.charAt(i);
			if (Character.isWhitespace(c)) {
				res[resI] = '_';
			} else {
				res[resI] = Character.toLowerCase(c);
			}
			++resI;
		}

		return new String(res);
	}

	public static void main(String[] args) {
		System.out.println(doNormalizePostLowercaseNameNoTrim("metric_with_underscores"));
	}

	private static String doNormalizeNameSb(String rawName) {
		final int len = rawName.length();
		final int startIndex = findStartIndex(rawName, len);
		final int endIndex = findEndIndex(rawName, len);
		final StringBuilder buffer = new StringBuilder(rawName.length());
		for (int i = startIndex; i <= endIndex; ++i) {
			final char current = rawName.charAt(i);
			if (Character.isWhitespace(i)) {
				buffer.append('_');
			} else {
				buffer.append(Character.toLowerCase(current));
			}
		}
		if (buffer.length() == 0) {
			throw new IllegalArgumentException("Illegal name: '" + rawName + "'");
		}
		return buffer.toString();
	}

	private static int findStartIndex(String rawName, int length) {
		int i = 0;
		for (; i < length; ++i) {
			if (!Character.isSpaceChar(rawName.charAt(i))) {
				break;
			}
		}
		return i;
	}

	private static int findEndIndex(String rawName, int length) {
		int i = length - 1;
		for (; i >= 0; --i) {
			if (!Character.isSpaceChar(rawName.charAt(i))) {
				break;
			}
		}
		return i;
	}
}
