package dna.parallel.io;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.io.Writer;

public class GiraphWriter {

	public static void write(Graph g, String dir, String filename)
			throws IOException {
		Writer w = new Writer(dir, filename);
		w.write("# temp");
		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			StringBuffer buff = new StringBuffer();
			if (n instanceof DirectedNode) {
				DirectedNode d = (DirectedNode) n;
				for (IElement e_ : d.getOutgoingEdges()) {
					DirectedEdge e = (DirectedEdge) e_;
					if (buff.length() != 0) {
						buff.append(",");
					}
					buff.append("[" + e.getDstIndex() + ",0]");
				}
			} else if (n instanceof UndirectedNode) {
				UndirectedNode u = (UndirectedNode) n;
				for (IElement e_ : u.getEdges()) {
					UndirectedEdge e = (UndirectedEdge) e_;
					if (buff.length() != 0) {
						buff.append(",");
					}
					buff.append("[" + e.getDifferingNode(u) + ",0]");
				}
			} else {
				throw new IllegalArgumentException("unknown node type: "
						+ n.getClass());
			}
			w.writeln("[" + n.getIndex() + ",0,[" + buff.toString() + "]]");
		}
		w.close();
	}

}
