package dna.parallel.components;

import argList.ArgList;
import argList.types.array.StringArrayArg;
import argList.types.atomic.BooleanArg;
import argList.types.atomic.EnumArg;
import argList.types.atomic.IntArg;
import argList.types.atomic.LongArg;
import argList.types.atomic.StringArg;
import dna.graph.generators.GraphGenerator;
import dna.metrics.Metric;
import dna.parallel.partition.Partition;
import dna.parallel.util.ReadableDirConsecutiveWaitingBatchGenerator;
import dna.parallel.util.ReadableFileWaitingGraph;
import dna.parallel.util.Sleeper;
import dna.plot.Plotting;
import dna.plot.PlottingConfig.PlotFlag;
import dna.series.Series;
import dna.series.data.SeriesData;
import dna.updates.generators.BatchGenerator;
import dna.util.Config;
import dna.util.fromArgs.GraphDataStructuresFromArgs.GdsType;
import dna.util.fromArgs.MetricFromArgs;
import dna.util.fromArgs.MetricFromArgs.MetricType;

public class Computation {

	public String inputDir;
	public String graphFilename;
	public String batchSuffix;

	public String outputDir;

	public int batches;
	public int run;
	public Metric metric;
	public ZipType zipType;

	public Sleeper sleeper;

	public boolean plot;

	public Computation(String gdsType, String[] gdsArgs, String inputDir,
			String graphFilename, String batchSuffix, String outputDir,
			Integer batches, Integer run, String metricType,
			String[] metricArgs, String zipType, Long sleep, Long timeoutAfter,
			Boolean plot) {
		this.inputDir = inputDir;
		this.graphFilename = graphFilename;
		this.batchSuffix = batchSuffix;
		this.outputDir = outputDir;
		this.batches = batches;
		this.run = run;
		this.metric = MetricFromArgs.parse(
				new String[] { Partition.mainNodeType },
				MetricType.valueOf(metricType), metricArgs);
		this.zipType = ZipType.valueOf(zipType);
		this.sleeper = new Sleeper(sleep, 0, timeoutAfter);
		this.plot = plot;
	}

	public static enum ZipType {
		none, batches, runs
	}

	public static void main(String[] args) throws Exception {
		ArgList<Computation> argList = new ArgList<Computation>(
				Computation.class,
				new EnumArg("gdsType", "type of the gds to use", GdsType
						.values()),
				new StringArrayArg("gdsArgs", "arguments for the gds type", ","),
				new StringArg("inputDir", "where to read data from"),
				new StringArg("graphFilename", "0.dnag"), new StringArg(
						"batchSuffix", "e.g., .dnab"), new StringArg(
						"outputDir", "where to store the results"), new IntArg(
						"batches", "# of batches to execute"), new IntArg(
						"run", "run index (starting at 0)"),
				new EnumArg("metricType", "metric to compute", MetricType
						.values()), new StringArrayArg("metricArgs",
						"arguments for the metric", ","), new EnumArg(
						"zipType", "what data to zip", ZipType.values()),
				new LongArg("sleep", "time to sleep in milliseconds"),
				new LongArg("timeoutAfter", "timeout in milliseconds"),
				new BooleanArg("plot", "flag to enable/disable plotting"));

		if (args.length == 0) {
			int worker = 6;
			args = new String[] { "Undirected", "-",
					"data/1-partitioning/worker" + worker + "/", "0.dnag",
					".dnab", "data/2-computation/worker" + worker + "/", "10",
					"0", "UndirectedClusteringCoefficientR", "-", "none", "50",
					"10000" };
		}

		Computation c = argList.getInstance(args);
		c.compute();
	}

	public void compute() throws Exception {
		Config.overwrite("GENERATION_AS_ZIP", this.zipType.toString());

		GraphGenerator gg = new ReadableFileWaitingGraph(inputDir,
				graphFilename, sleeper);
		BatchGenerator bg = new ReadableDirConsecutiveWaitingBatchGenerator(
				"name", inputDir, batchSuffix, sleeper);
		Metric[] metrics = new Metric[] { metric };

		Series s = new Series(gg, bg, metrics, outputDir, "name");
		// s.generateRuns(run, run, batches);
		SeriesData sd = s.generateRuns(run, run, batches, false, true, 1);
		if (this.plot) {
			Plotting.plot(sd, outputDir + "_plots/",
					PlotFlag.plotSingleScalarValues);
		}
	}

}
