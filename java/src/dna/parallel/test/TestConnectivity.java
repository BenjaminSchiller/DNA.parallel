package dna.parallel.test;

import java.io.IOException;

import dna.graph.datastructures.GDS;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.random.RandomGraph;
import dna.metrics.Metric;
import dna.metrics.MetricNotApplicableException;
import dna.metrics.connectivity.WCSimpleR;
import dna.plot.Plotting;
import dna.series.Series;
import dna.series.data.SeriesData;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.random.RandomBatch;
import dna.util.Config;
import dna.util.Execute;

public class TestConnectivity {
	public static void main(String[] args) throws IOException,
			MetricNotApplicableException, InterruptedException {
		Execute.exec("rm -r data/test/");
		Config.zipRuns();
		GraphGenerator gg = new RandomGraph(GDS.undirected(), 100, 0);
		BatchGenerator bg = new RandomBatch(0, 0, 5, 0);
		Metric[] metrics = new Metric[] { new WCSimpleR(), new WCSimpleR() };

		Series s = new Series(gg, bg, metrics, "data/test/connectivity/",
				"name");
		SeriesData sd = s.generateRuns(0, 0, 20);
		Plotting.plot(sd, "data/test/connectivity-plots/");
		Execute.exec("open data/test/connectivity-plots/");
	}
}
