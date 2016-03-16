package dna.parallel.test;

import java.util.Scanner;

import dna.graph.Graph;
import dna.graph.datastructures.GDS;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.canonical.RingGraph;
import dna.graph.generators.combined.CombinedGraph;
import dna.graph.generators.combined.CombinedGraph.InterconnectionType;
import dna.graph.generators.random.RandomGraph;
import dna.graph.weights.TypedWeight;
import dna.graph.weights.Weight.WeightSelection;
import dna.io.BatchReader;
import dna.io.BatchWriter;
import dna.parallel.nodeAssignment.NodeAssignment;
import dna.parallel.nodeAssignment.RandomNodeAssignment;
import dna.parallel.partition.AllChanges;
import dna.parallel.partition.AllPartitions;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.partitioning.EqualSizePartitioning;
import dna.parallel.partitioning.Partitioning;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitization;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.random.RandomBatch;
import dna.util.Rand;

public class TestPartitioning {
	public static void main(String[] args) throws Exception {
		// GraphVisualization.enable();
		Rand.init(0);
		GraphDataStructure gds = GDS.undirected();
		gds = GDS.undirectedV(TypedWeight.class, WeightSelection.None);
		// gds = GDS.undirectedV(TypedWeight.class, WeightSelection.None);
		GraphGenerator gg = new RandomGraph(gds, 500, 2500);
		gg = new CombinedGraph("name", InterconnectionType.RANDOM, 5,
				new RingGraph(gds, 20), new RingGraph(gds, 20), new RingGraph(
						gds, 20), new RingGraph(gds, 20),
				new RingGraph(gds, 20));
		BatchGenerator bg = new RandomBatch(0, 4, 0, 20);

		// String dir =
		// "/Users/benni/TUD/Projects/DNA/DNA.datasets/build/datasets/random-100-500-0-0-10-5/";
		// gg = new ReadableFileGraph(dir, "0.dnag");
		// bg = new ReadableDirBatchGenerator("name", dir,
		// new SuffixFilenameFilter(".dnab"));

		int partitionCount = 2;

		Partitioning partitioning = new EqualSizePartitioning();
		NodeAssignment nodeAssignment = new RandomNodeAssignment(partitionCount);

		// test(gg, bg, partitioning, PartitionType.NonOverlapping,
		// partitionCount, 100, nodeAssignment);
		test(gg, bg, partitioning, PartitionType.Overlapping, partitionCount,
				5, nodeAssignment);
	}

	protected static void test(GraphGenerator gg, BatchGenerator bg,
			Partitioning partitioning, PartitionType type, int partitionCount,
			int batches, NodeAssignment nodeAssignment) throws Exception {
		Scanner scanner = new Scanner(System.in);

		Graph g = gg.generate();

		AllPartitions all = partitioning.getPartition(type, g, partitionCount);
		System.out.println(g);
		System.out.println(all);
		System.out.println(all.auxData);
		for (int i = 0; i < partitionCount; i++) {
			System.out.println(all.auxData.nodes[i]);
		}
		for (int i = 0; i < batches; i++) {
			// scanner.nextLine();
			System.out.println();
			Batch b = bg.generate(g);
			BatchSanitization.sanitize(b);
			System.out.println(g);
			System.out.println(b);
			// b.print();
			AllChanges changes = AllChanges.split(all, b, all.auxData,
					nodeAssignment);
			b.apply(g);
			for (int j = 0; j < partitionCount; j++) {
				System.out.println("before: " + all.partitions[j].g);
				all.partitions[j].g.printV();
				BatchWriter.write(changes.batches[j], "/tmp/", "temp.dnab");
				Batch b_ = BatchReader.read("/tmp/", "temp.dnab",
						all.partitions[j].g);
				BatchSanitization.sanitize(b_);
				System.out.println(j + ": " + b_);
				b_.print();
				b_.apply(all.partitions[j].g);
				System.out.println("after: " + all.partitions[j].g);
				// all.partitions[j].g.printAll();
				// System.out.println();
			}
			all.auxData.add(changes.auxAdd);
			all.auxData.remove(changes.auxRemove);
			System.out.println(changes);
			System.out.println(all.auxData);
		}
	}
}
