package com.github.raipc.moex;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class InstrumentCache {
	private static final byte[] INDX = "IMOEX".getBytes(StandardCharsets.UTF_8);
	private static final byte[] NO_INDX = "GAZP".getBytes(StandardCharsets.UTF_8);
	private static final HashMap<String, String> hashMap = new HashMap<>();
	private static final TreeMap<String, String> treeMap = new TreeMap<>();
	private static final SimpleTrie trie = new SimpleTrie();

	static {
		fillIndexes(hashMap::put);
		fillIndexes(treeMap::put);
		fillIndexes(trie::insert);
	}

	private static void fillIndexes(BiConsumer<String, String> consumer) {
		consumer.accept("IMOEX", "INDX");
		consumer.accept("IMOEX2", "INDX");
		consumer.accept("MOEX10", "INDX");
		consumer.accept("MOEXBC", "INDX");
		consumer.accept("MOEXBMI", "INDX");
		consumer.accept("MOEXCH", "INDX");
		consumer.accept("MOEXCN", "INDX");
		consumer.accept("MOEXEU", "INDX");
		consumer.accept("MOEXFN", "INDX");
		consumer.accept("MOEXINN", "INDX");
		consumer.accept("MOEXMM", "INDX");
		consumer.accept("MOEXOG", "INDX");
		consumer.accept("MOEXREPO", "INDX");
		consumer.accept("MOEXREPO1W", "INDX");
		consumer.accept("MOEXREPO1WE", "INDX");
		consumer.accept("MOEXREPOE", "INDX");
		consumer.accept("MOEXREPOEQ", "INDX");
		consumer.accept("MOEXREPOEQE", "INDX");
		consumer.accept("MOEXREPOUSD", "INDX");
		consumer.accept("MOEXREPOUSDE", "INDX");
		consumer.accept("MOEXTL", "INDX");
		consumer.accept("MOEXTN", "INDX");
		consumer.accept("MRBC", "INDX");
		consumer.accept("RGBI", "INDX");
		consumer.accept("RGBITR", "INDX");
		consumer.accept("RPGCC", "INDX");
		consumer.accept("RPGCC1W", "INDX");
		consumer.accept("RPGCC1WE", "INDX");
		consumer.accept("RPGCCE", "INDX");
		consumer.accept("RTSCH", "INDX");
		consumer.accept("RTSCR", "INDX");
		consumer.accept("RTSEU", "INDX");
		consumer.accept("RTSFN", "INDX");
		consumer.accept("RTSI", "INDX");
		consumer.accept("RTSMM", "INDX");
		consumer.accept("RTSOG", "INDX");
		consumer.accept("RTSTL", "INDX");
		consumer.accept("RTSTN", "INDX");
		consumer.accept("RUBMI", "INDX");
		consumer.accept("RUCBCP3Y", "INDX");
		consumer.accept("RUCBCP5Y", "INDX");
		consumer.accept("RUCBICP", "INDX");
		consumer.accept("RUCBITR", "INDX");
		consumer.accept("RUCBTR3Y", "INDX");
		consumer.accept("RUCBTR5Y", "INDX");
		consumer.accept("RUMBICP", "INDX");
		consumer.accept("RUMBITR", "INDX");
		consumer.accept("RUSFAR", "INDX");
		consumer.accept("RUSFAR1M", "INDX");
		consumer.accept("RUSFAR1MRT", "INDX");
		consumer.accept("RUSFAR1W", "INDX");
		consumer.accept("RUSFAR1WRT", "INDX");
		consumer.accept("RUSFAR2W", "INDX");
		consumer.accept("RUSFAR2WRT", "INDX");
		consumer.accept("RUSFAR3M", "INDX");
		consumer.accept("RUSFAR3MRT", "INDX");
		consumer.accept("RUSFARRT", "INDX");
		consumer.accept("RUSFARUSD", "INDX");
		consumer.accept("CNYFIX", "INDX");
		consumer.accept("CNYFIXME", "INDX");
		consumer.accept("EURFIX", "INDX");
		consumer.accept("EURFIXME", "INDX");
		consumer.accept("EURUSDFIX", "INDX");
		consumer.accept("EURUSDFIXME", "INDX");
		consumer.accept("USDEURBASKET", "INDX");
		consumer.accept("USDFIX", "INDX");
		consumer.accept("USDFIXME", "INDX");

	}

	@Benchmark
	public String baseline() {
		return new String(INDX);
	}

	@Benchmark
	public boolean usingHashMapIndx() {
		return hashMap.containsKey(new String(INDX));
	}

	@Benchmark
	public boolean usingHashMapNoIndx() {
		return hashMap.containsKey(new String(NO_INDX));
	}

	@Benchmark
	public boolean usingTreeMapIndx() {
		return treeMap.containsKey(new String(INDX));
	}

	@Benchmark
	public boolean usingTreeMapNoIndx() {
		return treeMap.containsKey(new String(NO_INDX));
	}

	@Benchmark
	public boolean usingTrieIndx() {
		return trie.find(new String(INDX)) != null;
	}

	@Benchmark
	public boolean usingTrieNoIndx() {
		return trie.find(new String(NO_INDX)) != null;
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*InstrumentCache.*.*")
				.warmupIterations(2)
				.warmupTime(TimeValue.seconds(3))
				.measurementIterations(3)
				.measurementTime(TimeValue.seconds(5))
				.mode(Mode.SampleTime)
				.timeUnit(TimeUnit.NANOSECONDS)
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}
