package dna.parallel.components;

import java.io.File;
import java.io.IOException;

import argList.ArgList;
import argList.types.array.StringArrayArg;
import argList.types.atomic.EnumArg;
import argList.types.atomic.IntArg;
import argList.types.atomic.LongArg;
import argList.types.atomic.StringArg;
import dna.graph.Graph;
import dna.graph.datastructures.GDS;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.util.ReadableFileGraph;
import dna.graph.weights.TypedWeight;
import dna.graph.weights.Weight.WeightSelection;
import dna.io.BatchWriter;
import dna.io.GraphWriter;
import dna.parallel.auxData.AuxData;
import dna.parallel.auxData.AuxData.AuxWriteType;
import dna.parallel.nodeAssignment.NodeAssignment;
import dna.parallel.nodeAssignment.NodeAssignmentFromArgs;
import dna.parallel.nodeAssignment.NodeAssignmentFromArgs.NodeAssignmentType;
import dna.parallel.partition.AllChanges;
import dna.parallel.partition.AllPartitions;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.partitioning.PartitioningFromArgs;
import dna.parallel.partitioning.PartitioningFromArgs.PartitioningType;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.util.ReadableDirConsecutiveBatchGenerator;
import dna.util.fromArgs.GraphDataStructuresFromArgs;
import dna.util.fromArgs.GraphDataStructuresFromArgs.GdsType;

public class Partitioning {

	public String auxDir;
	public String outputDir;
	public String graphFilename;
	public String batchSuffix;

	public GraphGenerator gg;
	public BatchGenerator bg;
	public int batches;

	public PartitionType partitionType;
	public dna.parallel.partitioning.Partitioning partitioning;
	public int partitionCount;
	public NodeAssignment nodeAssignment;

	public long sleep;

	public Partitioning(String gdsType, String[] gdsArgs, String inputDir,
			String graphFilename, String batchSuffix, String auxDir,
			String outputDir, Integer batches, String partitionType,
			String partitioningType, Integer partitionCount,
			String nodeAssignmentType, Long sleep) throws IOException {
		GraphDataStructure gds = GraphDataStructuresFromArgs.parse(
				GdsType.valueOf(gdsType), gdsArgs);
		gds = GDS.undirectedV(TypedWeight.class, WeightSelection.None);
		this.gg = new ReadableFileGraph(inputDir, graphFilename, gds);
		this.bg = new ReadableDirConsecutiveBatchGenerator("name", inputDir,
				batchSuffix);
		this.auxDir = auxDir;
		this.outputDir = outputDir;
		this.graphFilename = graphFilename;
		this.batchSuffix = batchSuffix;
		this.batches = batches;

		this.partitionType = PartitionType.valueOf(partitionType);
		this.partitioning = PartitioningFromArgs.parse(PartitioningType
				.valueOf(partitioningType));
		this.partitionCount = partitionCount;
		this.nodeAssignment = NodeAssignmentFromArgs.parse(
				NodeAssignmentType.valueOf(nodeAssignmentType),
				this.partitionCount);
		this.sleep = sleep;

	}

	public static void main(String[] args) throws Exception {
		ArgList<Partitioning> argList = new ArgList<Partitioning>(
				Partitioning.class,
				new EnumArg("gdsType", "type of the gds to use", GdsType
						.values()),
				new StringArrayArg("gdsArgs", "arguments for the gds type", ","),
				new StringArg("inputDir",
						"directory where the dataset to partition is stored"),
				new StringArg("graphFilename", "e.g., 0.dnag"),
				new StringArg("batchSuffix", "e.g.., .dnab"),
				new StringArg("auxDir", "dir where to store the aux data"),
				new StringArg(
						"outputDir",
						"where to store the output "
								+ "(in this name PARTITION is replaced with the index "
								+ "of the worker, e.g., data/partitions/workerPARTITION/)"),
				new IntArg("batches", "# of batches to generate"),
				new EnumArg("partitionType",
						"what type of partitions to create", PartitionType
								.values()),
				new EnumArg("partitioningType", "how to partition the graph",
						PartitioningType.values()),
				new IntArg("partitionCount", "how many partitions to create"),
				new EnumArg("nodeAssignmentType",
						"how to assign new nodes to partitions",
						NodeAssignmentType.values()),
				new LongArg("sleep",
						"time (in milliseconds) to sleep after writing each graph / batch"));

		if (args.length == 0) {
			int workers = 10;
			args = new String[] {
					"Undirected",
					"-",
					"/Users/benni/TUD/Projects/DNA/DNA.parallel/build/datasets/Random-100,500-RandomGrowth-10,2-100/",
					"0.dnag", ".dnab", "data/1-partitioning/aux/",
					"data/1-partitioning/workerPARTITION/", "10",
					"Overlapping", "EqualSize", "" + workers, "Random", "0" };
		}

		Partitioning p = argList.getInstance(args);
		p.partition();
	}

	@SuppressWarnings("rawtypes")
	public void partition() throws Exception {
		Graph g = gg.generate();
		AllPartitions all = partitioning.getPartition(partitionType, g,
				partitionCount);

		for (int i = 0; i < all.partitions.length; i++) {
			this.write(all.partitions[i].g, i);
		}
		this.write(all.auxData, AuxWriteType.Init, g.getTimestamp());

		for (int j = 0; j < batches; j++) {
			if (!bg.isFurtherBatchPossible(g)) {
				break;
			}
			Batch b = bg.generate(g);
			AllChanges changes = AllChanges.split(all, b, all.auxData,
					this.nodeAssignment);
			b.apply(g);
			all.auxData.add(changes.auxAdd);
			all.auxData.remove(changes.auxRemove);
			for (int i = 0; i < changes.batches.length; i++) {
				this.write(changes.batches[i], i);
			}
			this.write(changes.auxAdd, AuxWriteType.Add, g.getTimestamp());
			this.write(changes.auxRemove, AuxWriteType.Remove, g.getTimestamp());
		}
	}

	public static String getPartitionDir(String outputDir, int partition) {
		String kw = dna.parallel.collation.Collation.partitionKeyword;
		if (outputDir.contains(kw)) {
			return outputDir.replace(kw, "" + partition);
		} else {
			return outputDir + partition + "/";
		}
	}

	public static final String tmp = ".tmp";

	protected void write(Graph g, int partition) {
		System.out.println(g + " => " + getPartitionDir(outputDir, partition));
		String dir = getPartitionDir(outputDir, partition);
		GraphWriter.write(g, dir, graphFilename + tmp);
		(new File(dir + graphFilename + tmp)).renameTo(new File(dir
				+ graphFilename));
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void write(Batch b, int partition) {
		System.out.println(b + " => " + getPartitionDir(outputDir, partition));
		String dir = getPartitionDir(outputDir, partition);
		BatchWriter.write(b, dir, b.getTo() + batchSuffix + tmp);
		(new File(dir + b.getTo() + batchSuffix + tmp)).renameTo(new File(dir
				+ b.getTo() + batchSuffix));
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	protected void write(AuxData d, AuxWriteType auxWriteType, long timestamp) {
		String filename = timestamp + ".aux." + partitionType + "."
				+ auxWriteType;
		System.out.println(d + " => " + auxDir + filename);
		d.write(auxDir, filename + tmp);
		(new File(auxDir + filename + tmp))
				.renameTo(new File(auxDir + filename));
	}

}
