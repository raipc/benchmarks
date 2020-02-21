package com.github.raipc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import static com.github.raipc.impl.TimeUtils.sizeInDigits;

@State(Scope.Benchmark)
public class PrintNumberBenchmark {

	private static final StringBuilder stringBuilder = new StringBuilder();
	private NumberFormat prepareFormat() {
		final DecimalFormat decimalFormat = new DecimalFormat("0");
		decimalFormat.setMinimumIntegerDigits(size);
		return decimalFormat;
	}

	private ThreadLocal<NumberFormat> numberFormat = ThreadLocal.withInitial(this::prepareFormat);
	private ThreadLocal<char[]> buf;

	@Param({"3", "9", "100"})
	int size;
	@Param({"7", "123"})
	int number;

	@Setup(Level.Invocation)
	public void setup() {
		stringBuilder.setLength(0);
	}

	@Setup(Level.Trial)
	public void initTl() {
		buf = ThreadLocal.withInitial(() -> new char[size]);
	}

//	@Benchmark
	public String withStringFormat() {
		return String.format("%03d", number);
	}

//	@Benchmark
	public String withFormatter() {
		return numberFormat.get().format(number);
	}

//	@Benchmark
	public StringBuilder appendWithFormatter() {
		return stringBuilder.append(numberFormat.get().format(number));
	}

	@Benchmark
	public String manualWithoutDivision() {
		final int num = this.number;
		final int sz = this.size;
		final StringBuilder sb = new StringBuilder(sz);
		sb.ensureCapacity(sz);
		for (int i = sz - sizeInDigits(num); i > 0; --i) {
			sb.append('0');
		}
		return sb.append(num).toString();
	}

	@Benchmark
	public StringBuilder appendManualWithoutDivision() {
		final int num = this.number;
		final int sz = this.size;
		final StringBuilder sb = stringBuilder;
		sb.ensureCapacity(sz);
		for (int i = sz - sizeInDigits(num); i > 0; --i) {
			sb.append('0');
		}
		return sb.append(num);
	}

	@Benchmark
	public String manualWithDivision() {
		int num = this.number;
		final int sz = this.size;
		char[] buffer = buf.get();
		for (int j = sz - 1; j >= 0; j--) {
			if (num == 0) {
				buffer[j] = '0';
			} else {
				buffer[j] = (char)('0' + num % 10);
				num /= 10;
			}
		}
		return new String(buffer);
	}

	@Benchmark
	public StringBuilder appendManualWithDivision() {
		int num = this.number;
		final int sz = this.size;
		char[] buffer = buf.get();
		for (int j = sz - 1; j >= 0; j--) {
			if (num == 0) {
				buffer[j] = '0';
			} else {
				buffer[j] = (char)('0' + num % 10);
				num /= 10;
			}
		}
		return stringBuilder.append(buffer);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.include(".*PrintNumberBenchmark.*.*")
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
}
