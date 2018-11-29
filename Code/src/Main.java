public class Main {

	public static void main(String[] args) {

		System.out.println("Create the Graph");
		Graph myGraph = new Graph(2000, 20000);
		// System.out.println(myGraph.toString());
		System.out.println(myGraph.revealRelationshipsInX());
		// System.out.println(myGraph.revealRelationshipsInW());
		System.out.println(myGraph.revealAllRelationshipsInW());

		System.out.println(myGraph.recoverH());

		// System.out.println(myGraph.revealRelationshipsInW());
		// myGraph.deleteEdges(5);

	}
}
