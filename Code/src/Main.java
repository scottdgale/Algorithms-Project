public class Main {

	public static void main(String[] args) {

		System.out.println("Create the Graph");
		Graph myGraph = new Graph(4000);

		System.out.println(myGraph.createH());
		System.out.println(myGraph.connectH());
		System.out.println(myGraph.recoverH());
		System.out.println(myGraph.recoverW());
		System.out.println(myGraph.revealDiscoveredWRelationships(myGraph.getDiscoveredW()));
		System.out.println(myGraph.revealRelationshipsInW());

		/********* OPTIONAL FUNCTION CALLS - Uncomment if desired ************/

//		System.out.println(myGraph.toString()); // Shows all the relationships for each vertex in the graph
//		System.out.println("");
//		System.out.println(myGraph.revealRelationshipsInX()); // Shows all the relationships for each vertex in X
//		System.out.println("");
//		System.out.println(myGraph.revealAllRelationshipsInW()); // Shows all the relationships for each vertex in W
// 		myGraph.deleteEdges(1); //Removes given percentage of edges (EX: .deleteEdges(1) removes 1% of the edges)

	}
}
