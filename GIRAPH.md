# Giraph setup for comparison with pDNA

![Giraph](http://giraph.apache.org/images/ApacheGiraph.svg)

## resources
- [main giraph library](http://giraph.apache.org)
- [Grafos.ml - library with additional metrics](http://grafos.ml)
- [notes in shared google doc](https://docs.google.com/document/d/1ExQdbXTfUajLFlUbskjyPkSXLIgQ5-q44Xm1oCcV428/edit?ts=56e7fbbb)

## Giraph wrapper

We provide a single wrapper script for executing the computation of a list of snapshots in Giraph.
This computation is executed in the pseudo-distributed setting of Giraph, i.e., master and workers are running on a single machine.

### `giraph.sh`

The wrapper script takes the following arguments:

	./giraph.sh $inputDir $from $to $outputDir $metric $workers

		$inputDir: directory where the dataset is stored (ending with /)
		$from: first snapshot to process
		$to: last snapshot to process
		$outputDir: directory where to store all output data
		$metric: keyword that specifies the metric to compute (see below)
		$workers: number of workers

As an example, take the following execution:

	./giraph.sh facebook/ 0 12 results/facebook/ WC 4

This launches a master and 4 workers to process the first 13 snapshots of the dataset stored in `facebook/`, i.e., the files `facebook/0.giraph`, `facebook/1.giraph`, ..., `facebook/12.giraph`.
For each snapshot, the weak connectivity (WC) is computed and the work divided among four workers.



## Metrics

The following metrics are considered and listed with their keyword (used as argument for `master.sh`).
In case a metric is only applicable for directed (D) or undirected (U) graphs, this is noted behind the name.
Otherwise, the metric can be computed for both kinds of graphs.

- **APSP** - all-pairs shortest paths
- **CC** - clustering coefficient (U)
- **RW** - random walk
- **WC** - weak connectivity

## Giraph file format
Each line denotes a vertex and its adjacencies, i.e.,

	[source_id,source_value,[[dest_id, edge_value],...]]

For example:

	[0,0,[[1,1],[3,3]]]
	[1,0,[[0,1],[2,2],[3,1]]]
	[2,0,[[1,2],[4,4]]]
	[3,0,[[0,3],[1,1],[4,4]]]
	[4,0,[[3,4],[2,4]]]

For **undirected** graphs, adjacencies must be given for both end-points of an edge.
For **directed** graphs, only the outgoing adjacencies are listed for each vertex.

### Giraph dataset structure
The datset of a dynamic graph consists of a sorted list of snapshots stored in the Giraph file format.
Each snapshot is represented in a separate file and named `${timestamp}.giraph`.
The timestamps always start with `0` and are simply incremented.
Hence, a dataset consisting of 145 snapshots in `testDir` contains the following files:

	testDir/
		0.giraph
		1.giraph
		2.giraph
		...
		144.giraph