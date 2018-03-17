package com.github.raipc;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.helpers.NOPAppender;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class LoggerStringConcatBenchmark {

	@State(Scope.Benchmark)
	public static class LoggerState {
		private final Logger LOG = prepareLogger();

		private static ch.qos.logback.classic.Logger prepareLogger() {
			final LoggerContext loggerContext = new LoggerContext();
			final ch.qos.logback.classic.Logger logger = loggerContext.getLogger("benchmarked_logger");
			logger.setAdditive(false);
			final NOPAppender<ILoggingEvent> noopAppender = new NOPAppender<>();
			noopAppender.setContext(loggerContext);
			noopAppender.setName("noop_appender");
			noopAppender.start();
			logger.addAppender(noopAppender);
			return logger;
		}
	}


	@State(Scope.Benchmark)
	public static class MyState {
		@Setup(Level.Trial)
		public void doSetup() {
			now = System.currentTimeMillis();
			str = "" + (now / 1000);
		}

		private long now;
		private String str;
		private int times = 10000;
	}


	@Benchmark
	public void concatWithFormat(MyState state, LoggerState loggerState) {
		for (int i = 0; i < state.times; ++i) {
			loggerState.LOG.info(String.format("Event happened at %s ms. Info: %s", state.now, state.str));
		}
	}

	@Benchmark
	public void concatWithLogger(MyState state, LoggerState loggerState) {
		for (int i = 0; i < state.times; ++i) {
			loggerState.LOG.info("Event happened at {} ms. Info: {}", state.now, state.str);
		}
	}

	@Benchmark
	public void concatWithLambda(MyState state, LoggerState loggerState) {
		for (int i = 0; i < state.times; ++i) {
			logHelper(loggerState.LOG, () -> "Event happened at " + state.now + " ms. Info: " + state.str);
		}
	}

	private static void logHelper(Logger log, Supplier<String> supplier) {
		log.info(supplier.get());
	}

	public static void main(String[] args) {
		final LoggerState loggerState = new LoggerState();
		loggerState.LOG.info("123");
	}
}
