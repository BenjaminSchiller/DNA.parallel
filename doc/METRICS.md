# Metrics


## DNA

<https://github.com/BenjaminSchiller/DNA/tree/master/src/dna/metrics>

**available:**

- assortativity
	- assortativity (R/U)
- centrality
	- betweenness centrality (R/U)
- triangles
	- directed clustering coefficient (R/U)
	- undirected clustering coefficient (R/U)
- connectivity
	- strong connectivity (R/U)
	- weak connectivity (R/U)
- degree
	- degree distribution (R/U)
- motifs
	- directed 3-vertex motifs (R/U)
	- undirected 4-vertex motifs (R/U)
- paths
	- double weighted APSP (R/U)
	- int weighted APSP (R/U)
	- unweighted APSP ((R/U)
- rich-clubs
	- rich-club connectivity by dergree (R/U)
- similarity measures
	- dice (R/U)
	- jaccard (R/U)
	- matching (R/U)
	- overlap (R/U)
- weights
	- edge weight distribution (R/U)
	- node weight distribution (R/U)
	- root mean square deviation (R/B)
	- root mean square fluctuation (R/U)

**missing:**

- page rank

## pDNA

<https://github.com/BenjaminSchiller/DNA/tree/master/src/dna/parallel/collation>

**available:**

- undirected cc (overlapping)
- directed cc (overlapping)
- weak connectivity (separated)
- all-pairs shortest paths (complete)
- betweenness centrality (complete)
- degree dsitribution (overlapping)
- similarity measures (complete)
- assortativity (???)

**hard collation:**

- strong connectivity
- motifs

**not available / applicable:**

- page rank
- rich-club connectivity


## Giraph

Here, metrics used from giraph examples are listed.
It is unclear if they are provided for directed and/or undirected graph, we assume that only undirected graphs are implemented.

Each algorithm is specified as:

- **KEY**: name/link

### sources
- <https://www.quora.com/What-are-the-algorithms-built-of-top-of-Giraph-by-far>
- <http://giraph.apache.org>
- <https://github.com/apache/giraph/tree/release-1.0/giraph-examples/src/main/java/org/apache/giraph/examples>

### we use:
- **GIRAPH\_WEAK\_CONNECTIVITY**: [Connected Components](https://github.com/apache/giraph/blob/release-1.0/giraph-examples/src/main/java/org/apache/giraph/examples/ConnectedComponentsVertex.java) (?)
- **GIRAPH\_IN\_DEGREE**: [In Degree](https://github.com/apache/giraph/blob/release-1.0/giraph-examples/src/main/java/org/apache/giraph/examples/SimpleInDegreeCountVertex.java) (D & U)
- **GIRAPH\_OUT\_DEGREE**: [Out Degree](https://github.com/apache/giraph/blob/release-1.0/giraph-examples/src/main/java/org/apache/giraph/examples/SimpleOutDegreeCountVertex.java) (D & U)
- **GIRAPH\_APSP\_SINGLE**: [Single-source shortest paths](https://github.com/apache/giraph/blob/release-1.0/giraph-examples/src/main/java/org/apache/giraph/examples/SimpleShortestPathsVertex.java) (?)
- **GIRAPH\_TRIANGLE\_CLOSING**: [Triangle Closing](https://github.com/apache/giraph/blob/release-1.0/giraph-examples/src/main/java/org/apache/giraph/examples/SimpleTriangleClosingVertex.java) (?)

we assume ethat all metrcs are only implemented for undirected graphs.
this still needs to be checked!

### others:
- random walk
- page rank


## Okapi

Here, metrics used from ocapi are listed.
*W* identifies algorithms working for weighted graphs, *D* are directed graphs, and *U* are undirected graphs.
*U & D* means that the algorithm can be applied to directed or undirected graphs, if only one is used the other is assumed to not be supported.
*W* menas that weighted graphs are supported but no statement about directed or undirected is mde.

Each algorithm is specified as:

- **KEY**: [name/link]() (*graph type*) (optional description)

### sources
- <http://grafos.ml/okapi.html#analytics>
- <https://github.com/grafos-ml/okapi/tree/master/src/main/java/ml/grafos/okapi/graphs>

### we use:
- **OKAPI\_CLUSTERING\_COEFFICIENT**: [Clustering Coefficient](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/ClusteringCoefficient.java) (D & U)
- **OKAPI\_WEAK\_CONNECTIVITY**: [Connected Components](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/ConnectedComponents.java) (i.e., Weak Connectivity)
- **OKAPI\_APSP\_MULTI**: [Multi-source shortest paths](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/MultipleSourceShortestPaths.java) (*W*) (to compute APSP, a list of all vertices (contained in the current graph!) must be provided as landmarks)
- **OKAPI\_APSP\_SINGLE**: [Single-source shortest paths](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/SingleSourceShortestPaths.java) (*D & U*) (to compute APSP, this computation must be executed for each vertex (contained in the current graph!) must be executed separately)
- **OKAPI\_TRIANGLE\_COUNT**: [TriangleCount](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/Triangles.java) (*D*) (use this class to count the number of triangles in the graph)
- **OKAPI\_TRIANGLE\_FIND**: [TriangleFind](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/Triangles.java) (*D*) (use this class to find the occurrences of all triangles in the graph)
- **OKAPI\_JACCARD**: [Jaccard similarity](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/similarity/Jaccard.java) (*U*)

### others:
- [K-core](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/KCore.java)
- [Semi-clustering](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/SemiClustering.java) (W)
- [Semi-metric](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/ScalableSemimetric.java)
- [Semi-metric triangles](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/SemimetricTriangles.java)
- [Page rang](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/SimplePageRank.java)
- [Sybil rank](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/SybilRank.java) (W)
- [Adamic-Adar similarity](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/similarity/AdamicAdar.java) (U)
- [Maximum B-matching](https://github.com/grafos-ml/okapi/blob/master/src/main/java/ml/grafos/okapi/graphs/maxbmatching/MaxBMatching.java)

