# Giraph setup for comparison with pDNA

![Giraph](http://giraph.apache.org/images/ApacheGiraph.svg)

## resources
- [main giraph library](http://giraph.apache.org)
- [Grafos.ml - library with additional metrics](http://grafos.ml)
- [notes in shared google doc](https://docs.google.com/document/d/1ExQdbXTfUajLFlUbskjyPkSXLIgQ5-q44Xm1oCcV428/edit?ts=56e7fbbb)

## Giraph wrapper

Two scripts are provided as wrappers for Giraph to start workers (`worker.sh`) and masters (`master.sh`).

In the initial version, a master is executed on the same machine as all workers it is working with.
Thereby, the ip address of all instances is assumed to be *127.0.0.1*.

### `worker.sh`

This wrapper script is used to start an instance of a worker which can then be used by a master instance to process a given graph.

`worker.sh` takes the following arguments:

	./worker.sh $port $outputDir
	
The worker is then launched ans listens on the specified port for commands of a master.
Possible output data is written to the specified directory.

### `master.sh`

This wrapper script is used to start an instance of a master which processes a list of graphs and computes a specified metric on each of them.

`master.sh` takes the following arguments:

	./master.sh $direction $inputDir $from $to $outputDir $metric $workers

		$direction: 'U' or 'D' to specify that the graph is undirected or directed
		$inputDir: directory where the dataset is stored (ending with /)
		$from: first snapshot to process
		$to: last snapshot to process
		$outputDir: directory where to store all output data
		$metric: keyword that specifies the metric to compute (see below)
		$workers: list of workers identified by their ports (separated by ,)

As an example, take the following execution:

	./master.sh D facebook/ 0 12 results/facebook/ WC 521,525,642

This launches a master to process the first 13 snapshots of the dataset stored in `facebook/`.
For each snapshot, the weak connectivity is computed and the work divided among the three workers with ports 521, 525, and 642.

## Metrics

The following metrics are considered and listed with their keyword (used as argument for `master.sh`).
In case a metric is only applicable for directed (D) or undirected (U) graphs, this is noted behind the name.
Otherwise, the metric can be computed for both kinds of graphs.

- **APSP** - all-pairs shortest paths
- **CC** - clustering coefficient (U)
- **PR** - page rank
- **RW** - random walk
- **SC** - strong connectivity (D)
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