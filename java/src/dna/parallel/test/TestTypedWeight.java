package dna.parallel.test;

import dna.graph.datastructures.GDS;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weights.Weight.WeightSelection;
import dna.graph.weights.intW.TypedInt2dWeight;

public class TestTypedWeight {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphDataStructure gds = GDS.undirectedV(TypedInt2dWeight.class,
				WeightSelection.Zero);
		System.out.println(gds.newNodeInstance(3));
		System.out.println(gds.newNodeInstance("15@123123;3;12214"));
		System.out.println(gds.newNodeInstance("12@4twege;1;3"));
	}

}
