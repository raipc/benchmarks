package com.github.raipc;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
public class DivisionBenchmark {
	@Param({"7", "178", "177678125"})
	int number;

	@Benchmark
	public int testDivisionAndSubtraction() {
		return number - 10 * (number / 10);
	}

	@Benchmark
	public int testDivision() {
		return number / 10;
	}

	@Benchmark
	public int testModulo() {
		return number % 10;
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.include(".*" + DivisionBenchmark.class.getSimpleName() + ".*")
			.warmupIterations(2)
			.warmupTime(TimeValue.seconds(2))
			.measurementIterations(3)
			.measurementTime(TimeValue.seconds(3))
			.mode(Mode.Throughput)
			.timeUnit(TimeUnit.MICROSECONDS)
			.forks(1)
//			.addProfiler(GCProfiler.class)
			.build();
		new Runner(opt).run();
	}
}
