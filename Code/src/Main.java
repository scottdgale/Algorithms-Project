public class Main {

	public static void main(String[] args) {

		System.out.println("Create the Graph");
		Graph myGraph = new Graph(4000, 40000);
		//System.out.println(myGraph.toString());
        System.out.println(myGraph.revealRelationshipsInX());
		//myGraph.deleteEdges(5);

		// System.out.println(myGraph.toString());
		System.out.println(myGraph.revealRelationshipsInW());

	}
}
