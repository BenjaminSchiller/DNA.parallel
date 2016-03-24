# Format

## file structure for an analysis


### output of partitioning / splitting

Here, P indicates the partition type, i.e., Complete, Separated, or Overlapping.

	1-partitioning/
		aux/
			0.aux.P.Init			1.aux.P.Add			1.aux.P.Remove			2.aux.P.Add			2.aux.P.Remove
			...
		worker0/
			0.dnag			1.dnab			2.dnab
			...
		worker1/
			...
		worker2/
			...
		worker3/
			...
		...

### output of workers / computation

	2-computation/
		worker0/
			run.0/
				batch.0/
				batch.1/
				batch.2/
				...
		worker2/
			...
		worker3/
			...
		...


### output of collation

	3-collation/
		run.0/
			batch.0/
			batch.1/
			batch.2/
			...


## aux file formats

In general, aux files contain one line for each partition followed by a line specific to the partion type.

	$partition0
	$partition1
	$partition2
	...
	$generalData

### partitions

Each partition (represented by a single line) contains 2 or 3 entries, separated by ";".
The first entry is always the index of the partition and the second a list (separated by " ") of the assigned vertex indexes (the only entries for `NonOverlapping`).

	$id; $listOfAssignedVertices

For `Overlapping` partitions, the third entry is a list all vertices that are duplicated for this partition in addition to the assigned ones.

	$id; $listOfAssignedVertices; $listOfDuplicatedVertices

### general (partition-specific) data

For a `NonOverlapping` partitioning, the general data contains a list of all edges between partitions that are not covered by them:

	$edgesBetweenPartitions

Here, the list of edges is separated by ";" and each edge represented by a pair of verties (separated by " ".
In case of undirected edges, the order is irrelevant and identified source and destination in the case of directed edges.

For an `Overlapping` partitioning, no additional data is required since the duplicated vertices are already listed for each partition in the third column.

### examples (.*_aux)

###### .NonOverlapping_aux

	0; 4 7 2 91 3 1
	1; 3 6 78
	2; 100 21 8
	...
	10; 9 15 18
	4,3 78,21 78,8 ...

###### .Overlapping_aux

	0; 4 7 2 91 3 1; 3 8
	1; 3 6 78; 1 91
	2; 100 21 8; 	3 6 4 7
	...
	10; 9 15 18; 100 2