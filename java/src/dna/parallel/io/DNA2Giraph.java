package dna.parallel.io;

import argList.ArgList;
import argList.types.atomic.IntArg;
import argList.types.atomic.StringArg;
import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.util.ReadableFileGraph;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitization;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.util.ReadableDirConsecutiveBatchGenerator;

public class DNA2Giraph {

	public String datasetDir;
	public String graphFilename;
	public String batchSuffix;

	public int batches;

	public String dstDir;
	public String dstSuffix;

	public DNA2Giraph(String datasetDir, String graphFilename,
			String batchSuffix, Integer batches, String dstDir, String dstSuffix) {
		this.datasetDir = datasetDir;
		this.graphFilename = graphFilename;
		this.batchSuffix = batchSuffix;
		this.batches = batches;
		this.dstDir = dstDir;
		this.dstSuffix = dstSuffix;
	}

	public static void main(String[] args) throws Exception {
		ArgList<DNA2Giraph> argList = new ArgList<DNA2Giraph>(DNA2Giraph.class,
				new StringArg("datasetDir", ""), new StringArg("graphFilename",
						"e.g., 0.dnag"), new StringArg("batchSuffix",
						"e.g., .dnab"), new IntArg("batches", "# of batches"),
				new StringArg("dstDir", "where to write (ending with /"),
				new StringArg("dstSuffix", "e.g., .giraph"));

		DNA2Giraph dg = argList.getInstance(args);
		dg.write();
	}

	public void write() throws Exception {
		int counter = 0;
		GraphGenerator gg = new ReadableFileGraph(datasetDir, graphFilename);
		BatchGenerator bg = new ReadableDirConsecutiveBatchGenerator("bg",
				datasetDir, batchSuffix);
		Graph g = gg.generate();
		String filename = (counter++) + dstSuffix;
		GiraphWriter.write(g, dstDir, filename);
		System.out.println("=> " + dstDir + filename);
		for (int i = 0; i < batches; i++) {
			if (!bg.isFurtherBatchPossible(g)) {
				break;
			}
			Batch b = bg.generate(g);
			BatchSanitization.sanitize(b);
			b.apply(g);
			filename = (counter++) + dstSuffix;
			GiraphWriter.write(g, dstDir, filename);
			System.out.println("=> " + dstDir + filename);
		}
	}
}
