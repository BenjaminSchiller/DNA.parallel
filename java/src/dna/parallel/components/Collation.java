package dna.parallel.components;

import argList.ArgList;
import argList.types.array.StringArrayArg;
import argList.types.atomic.BooleanArg;
import argList.types.atomic.EnumArg;
import argList.types.atomic.IntArg;
import argList.types.atomic.LongArg;
import argList.types.atomic.StringArg;
import dna.graph.datastructures.GDS;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.util.EmptyGraph;
import dna.graph.generators.util.ReadableFileGraph;
import dna.io.filter.SuffixFilenameFilter;
import dna.metrics.Metric;
import dna.parallel.collation.CollationFromArgs;
import dna.parallel.collation.CollationFromArgs.CollationType;
import dna.parallel.components.Computation.ZipType;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.plot.Plotting;
import dna.plot.PlottingConfig.PlotFlag;
import dna.series.Series;
import dna.series.data.SeriesData;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.util.EmptyBatch;
import dna.updates.generators.util.ReadableDirBatchGenerator;
import dna.util.Config;
import dna.util.fromArgs.MetricFromArgs;
import dna.util.fromArgs.MetricFromArgs.MetricType;

public class Collation {

	public String auxDir;
	public String inputDir;
	public String outputDir;
	public ZipType zipType;
	public int partitionCount;
	public int batches;
	public int run;

	public PartitionType partitionType;
	@SuppressWarnings("rawtypes")
	public dna.parallel.collation.Collation collation;

	public Metric m;
	public String datasetDir;

	public boolean plot;

	public Collation(String auxDir, String inputDir, String outputDir,
			String zipType, Integer partitionCount, Integer batches,
			Integer run, String partitionType, String collationType,
			String[] collationArgs, Long sleep, Long timeoutAfter,
			String metricType, String datasetDir, Boolean plot) {
		this.auxDir = auxDir;
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.zipType = ZipType.valueOf(zipType);
		this.partitionCount = partitionCount;
		this.batches = batches;
		this.run = run;
		this.partitionType = PartitionType.valueOf(partitionType);
		this.collation = CollationFromArgs.parse(CollationType
				.valueOf(collationType), auxDir, inputDir, this.partitionCount,
				this.run, new Sleeper(sleep, 0, timeoutAfter), collationArgs);
		if (metricType.equals("-")) {
			this.m = null;
			this.datasetDir = null;
		} else {
			this.m = MetricFromArgs.parse(MetricType.valueOf(metricType));
			this.datasetDir = datasetDir;
		}
		this.plot = plot;
	}

	public static void main(String[] args) throws Exception {
		ArgList<Collation> argList = new ArgList<Collation>(
				Collation.class,
				new StringArg("auxDir", "where to read the aux data from, "
						+ "e.g., data/partitions/aux/)"),
				new StringArg(
						"inputDir",
						"where to read the computations from "
								+ "(in this name PARTITION is replaced with the index "
								+ "of the worker, e.g., data/partitions/workerPARTITION/)"),
				new StringArg("outputDir",
						"where to write collation results to"),
				new EnumArg("zipType", "what data to zip", ZipType.values()),
				new IntArg("partitionCount", "# of partitions / workers"),
				new IntArg("batches", "# of batches"),
				new IntArg("run", "run index"),
				new EnumArg("partitionType", "type of partitions used",
						PartitionType.values()),
				new EnumArg("collationType", "what collation should be used",
						CollationType.values()),
				new StringArrayArg("collationArgs",
						"arguments for the collation", ","),
				new LongArg("sleep", "time to sleep in milliseconds"),
				new LongArg("timeoutAfter", "timeout in milliseconds"),
				new EnumArg("metricType", "metric to compute for comparison",
						MetricType.values()),
				new StringArg("datasetDir",
						"dir where the dataset is stored (only used in case the metrics is set (!= '-'"),
				new BooleanArg("plot",
						"flag to enable/disable plotting of results"));

		if (args.length == 0) {
			args = new String[] {
					"../build/data/Random-100,500-RandomGrowth-10,5-100/1-partitioning/aux/",
					"../build/data/Random-100,500-RandomGrowth-10,5-100/2-computation/workerPARTITION/",
					"../build/data/Random-100,500-RandomGrowth-10,5-100/3-collation/",
					"batches", "10", "5", "0", "Separated",
					"WCSimpleSeparated", "-", "100", "10000", "WCSimpleR",
					"../build/datasets/Random-100,500-RandomGrowth-10,5-100/" };
		}

		Collation c = argList.getInstance(args);
		c.collate();
	}

	public void collate() throws Exception {
		Config.overwrite("GENERATION_AS_ZIP", this.zipType.toString());

		GraphGenerator gg = new EmptyGraph(GDS.undirected());
		BatchGenerator bg = new EmptyBatch();
		Metric[] metrics = new Metric[] { this.collation };

		if (this.m != null) {
			gg = new ReadableFileGraph(this.datasetDir, "0.dnag");
			bg = new ReadableDirBatchGenerator("batch", this.datasetDir,
					new SuffixFilenameFilter(".dnab"));
			metrics = new Metric[] { this.collation, this.m };
		}

		Series s = new Series(gg, bg, metrics, outputDir, "collation");
		Config.overwrite("GENERATION_AS_ZIP", this.zipType.toString());
		SeriesData sd = s.generateRuns(run, run, batches);
		if (plot) {
			Plotting.plot(sd, outputDir + "_plots/",
					PlotFlag.plotSingleScalarValues);
		}
	}

}
