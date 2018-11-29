import java.util.ArrayList;
import java.util.Random;

/**
 * The Graph Class represents a graph undirected, unweighted simple graph that
 * contains vertices and edges (represented by neighbors in the Vertex class).
 *
 * @author Gale, Roweton, Njeru
 * @version 1.0
 * @since 19 November 2018
 */
public class Graph {
	// contains all the vertices in the graph
	private ArrayList<Vertex> vertices;
	// counters used to assign id's to the different type of nodes
	private int countXID;
	private int countWID;
	private int countNID;
	private int desiredTotalEdges;
	private int actualTotalEdges;

	private int numNVertices;
	private int numXVertices;
	private int numWVertices;

	private int d0;
	private int d1;

	private int[] degreeX; // array containing value corresponding to degree of each xi
	private Tree myTree;
	private ArrayList<ArrayList<Vertex>> branches;
	private boolean found;

	private ArrayList<ArrayList<XVertex>> set = new ArrayList<>();

	/**
	 * Constructor to instantiate this object
	 *
	 * @param numN  indicates the number of "normal" vertices to create (not W or X
	 *              vertices)
	 * @param edges indicates the approximate number of edges to create in the graph
	 */
	public Graph(int numN, int edges) {
		this.countXID = 0;
		this.countWID = 0;
		this.countNID = 0;

		int epsilon = 1;

		this.numNVertices = numN;
		this.numWVertices = (int) Math.pow(Math.log(this.numNVertices), 2);
		this.numWVertices = 34;
		// this.numWVertices = 50;
		// this.numXVertices = (int) ((2 + epsilon) * Math.log(this.numNVertices +
		// this.numWVertices));
		this.numXVertices = 10;
		this.degreeX = new int[this.numXVertices];

		this.myTree = new Tree();
		this.branches = new ArrayList<>();
		this.found = false;

		this.desiredTotalEdges = edges;

		vertices = new ArrayList<Vertex>(0);

		// Create all the vertices in the graph
		this.createWVertices();
		this.createNVertices();

		// Create random edges between all the N and W vertices
		this.actualTotalEdges = 0;
		this.actualTotalEdges += this.createWEdges();
		this.actualTotalEdges += this.createEdges();

		// Creating and connecting H
		this.determined0d1();
		this.createH();
		this.connectH();

		System.out.println("Actual Total Edges: " + this.actualTotalEdges);

	}

	/**
	 * Create this.numNVertices in the graph
	 */
	private void createNVertices() {
		for (int i = 0; i < this.numNVertices; i++) {
			vertices.add(new Vertex('n', this.countNID++));
		}
	}

	/**
	 * Create this.numNVertices in the graph
	 */
	private void createWVertices() {
		for (int i = 0; i < this.numWVertices; i++) {
			vertices.add(new Vertex('w', this.countWID++));
		}
	}

	private void createXVertices() {

		for (int i = 0; i < this.numXVertices; i++) {
			// determine external degree -- random number between d_0 and d_1
			int upperBoundAdjusted = this.d1 - this.d0; // Shift to 0 so that the randomGen can be used
			int externalDegree = this.randomGen(upperBoundAdjusted) + this.d0; // Shift back for a degree within the
																				// range
			vertices.add(new XVertex('x', this.countXID++, externalDegree));
		}
	}

	/**
	 * Create edges between the 'w' nodes AKA targeted nodes. This ensures that at
	 * least some relationship exists between some of the 'w' nodes.
	 *
	 */
	private int createWEdges() {
		int scaler = 5; // Adjusts the number of edges in W
		int count = 0;
		for (int i = 0; i < this.numWVertices; i++) {
			for (int j = 0; j < this.randomGen(scaler); j++) {
				int neighbor = this.randomGen(this.numWVertices - 1);
				if (i != neighbor) {
					this.vertices.get(i).addNeighbor(this.vertices.get(neighbor));
					this.vertices.get(neighbor).addNeighbor(this.vertices.get(i));
					count++;

				}
			}
		}
		return count;
	}

	/**
	 * Create edges within X vertices Create successive edges between X edges (ie.
	 * x_1 to x_2, x_2 to x_3, ... ) Create other edges with probability 1/2
	 *
	 */
	private void createXEdges() {
		// find out where to start in the vertices array list
		// the X vertices are created after the W and N vertices, so they are at the end
		int start = this.vertices.size() - this.numXVertices;
		int end = this.vertices.size();

		// Add successive edges
		for (int i = start; i < end - 1; i++) {
			this.vertices.get(i).addNeighbor(this.vertices.get(i + 1));
			this.vertices.get(i + 1).addNeighbor(this.vertices.get(i));
		}

		// Now add random edges between X vertices with prob 1/2
		for (int i = start; i < end; i++) {
			for (int j = start; j < end; j++) {
				// add an edge if randomGen returns 0; don't add an edge if randomGen returns 1
				if (i != j) {
					int result = this.randomGen(2);
					// System.out.println(result);
					if (result == 0) { // add an edge!
						this.vertices.get(i).addNeighbor(this.vertices.get(j));
						this.vertices.get(j).addNeighbor(this.vertices.get(i));
					}
				}
			}
		}
	}

	/**
	 * Generates random edges for each vertex in the graph. The number of edges
	 * created for each vertex is computed by dividing the total number of desired
	 * edges in the graph (this.numTotalEdges) by the number of vertices in the
	 * graph (this.vertices.size()) and then generating a random integer between 1
	 * and 2 * that number.
	 *
	 * @return the number of edges actually created.
	 */
	private int createEdges() {
		// Use random numbers to generate random number of edges for each vertex
		int avgNumEdgesPerVertex = this.desiredTotalEdges / this.vertices.size();
		int countSuccessfulEdges = 0;
		// In order to determine the number of edges to create for each vertex
		// get a random number between 0 and 2 times the avgNumEdgesPerVertex
		// then add neighbors (edges are represented by the number of neighbors each
		// vertex has)
		for (int i = 0; i < this.vertices.size(); i++) {
			for (int j = 0; j <= this.randomGen(avgNumEdgesPerVertex * 70); j++) {
				// select a random vertex from this.vertices (vertex list) and add as neighbor
				// ensure we don't add a vertex as a neighbor of itself
				int neighbor = this.randomGen(this.vertices.size());
				if (neighbor != i)
					if (this.vertices.get(i).addNeighbor(this.vertices.get(neighbor))) {
						this.vertices.get(neighbor).addNeighbor(this.vertices.get(i));
						countSuccessfulEdges++;
					}

			}
		}
		return countSuccessfulEdges;
	}

	/**
	 * Create H will create a certain number of 'x' vertices and connect them to
	 * each other according to the given algorithm.
	 */
	private void createH() {
		// Create H per the algorithm
		// Create vertices of X --- The number of vertices in X is taken from the paper
		// k = (2 + epsilon)log n
		this.createXVertices();
		// Create edges within X (both successive and random)
		this.createXEdges();
	}

	/**
	 * ConnectH will connect 'x' vertices to 'w' vertices IAW the published
	 * algorithm.
	 * 
	 */
	private void connectH() {
		// Connect H to G per the algorithm

		/**
		 * STEP 1 : CONNECT X Vertices (ie. H) to W Vertices
		 * 
		 */
		int c = 2;

		// This set contains all of the NJs (one for each W)

		for (int i = 0; i < this.numWVertices; i++) { // for each W vertex
			int numXvertInSet = this.randomGen(c) + 2; // get a random number between 1
			// and c inclusive -- gives us the
			// size of Nj

			// Set<XVertex> nj = new HashSet<XVertex>();
			ArrayList<XVertex> nj = new ArrayList<>();

			for (int j = 0; j < numXvertInSet; j++) {
				XVertex pick = this.pickAValidXForW(nj);

				// add the vertex to nj
				nj.add(pick);

			}

			// Sorting the vertices in nj to ensure that we can identify if the set contains
			// nj (this avoids [x1, x2] [x2, x1] situation
			XVertexSorter xVertexSorter = new XVertexSorter(nj);
			ArrayList<XVertex> sortedNJ = xVertexSorter.getSortedXVertices();

			// Check: does nj already exist in set?
			while (set.contains(sortedNJ)) {
				sortedNJ.remove(sortedNJ.size() - 1); // remove the last
				XVertex pick = this.pickAValidXForW(sortedNJ);
				sortedNJ.add(pick); // add new random x vertex to nj
				XVertexSorter xVertexSorter2 = new XVertexSorter(sortedNJ);
				sortedNJ = xVertexSorter2.getSortedXVertices();

			}

			// increment the currentExternalDegrees of all x's in nj
			for (XVertex x : sortedNJ) {
				x.incrementCurrentExternalDegree();
			}

			// add nj to set
			set.add(sortedNJ);

			// Add edges between w and each xi in nj
			for (int k = 0; k < sortedNJ.size(); k++) {
				this.vertices.get(i).addNeighbor(nj.get(k));
				sortedNJ.get(k).addNeighbor(this.vertices.get(i));
			}

		}

		System.out.println("The set of Nj's: " + set.toString());

		/**
		 * STEP 2: CONNECT X Vertices to G-H per 'external degree' of each X Vertex
		 * 
		 * Plan: Loop through each X vertex -- find difference between
		 * currExternalDegree and determinedExternalDegree --- add vertices in N for the
		 * difference Then check to make sure the determinedExternalDegree ==
		 * currentExternalDegree (ie. we've added all the nodes we needed to)
		 * 
		 */
		int start = this.vertices.size() - this.numXVertices;
		int end = this.vertices.size();

		for (int i = start; i < end; i++) {
			XVertex vertexOfInterest = (XVertex) this.vertices.get(i);
			System.out.println("Vertex: " + vertexOfInterest.toString() + " | DeterminedExternalDegree: "
					+ vertexOfInterest.getDeterminedExternalDegree());

			int diff = ((XVertex) this.vertices.get(i)).getDeterminedExternalDegree()
					- ((XVertex) this.vertices.get(i)).getCurrentExternalDegree();
			if (diff > 0) {

				for (int j = 0; j < diff; j++) { // for each remaining needed external degree
					// Pick a random index between the w and x vertices (ie. in the 'n' vertices)
					// Example: if I have 200 total vertices, 20 W, 30 X, and 150 N, then I can pick
					// a random number between 0 and 149 then add 20 to get an index in the N vertex
					// range
					int neighbor = this.randomGen(this.numNVertices) + this.numWVertices;

					// This makes sure that a valid neighbor is select out of N -- if a node has
					// already been picked, pick another
					// Uncomment print statements and run several times to see how this works
					while (!(this.vertices.get(i).addNeighbor(this.vertices.get(neighbor)))) {
//						System.out.println("Couldn't add " + this.vertices.get(neighbor)
//								+ " because it is already a neighbor of " + this.vertices.get(i));
//						System.out.println("Here are the neighbors: " + this.vertices.get(i).getNeighbors());
						neighbor = this.randomGen(this.numNVertices) + this.numWVertices;
						// System.out.println("The need neighbor chosen is: " +
						// this.vertices.get(neighbor));
					}

					this.vertices.get(neighbor).addNeighbor(this.vertices.get(i));
					// countSuccessfulEdges++;

					((XVertex) this.vertices.get(i)).incrementCurrentExternalDegree();
				}

			}

			// Check to make sure that the expected external degree matches the current
			// external degree
			if (((XVertex) this.vertices.get(i)).getDeterminedExternalDegree() != ((XVertex) this.vertices.get(i))
					.getCurrentExternalDegree()) {
				System.out.println("Something went wrong.... external degree is not matching for "
						+ ((XVertex) this.vertices.get(i)).toString());
			}
		}

	}

	private XVertex pickAValidXForW(ArrayList<XVertex> nj) {
		// pick a random x vertex
		int xPick = randomGen(this.numXVertices);
		ArrayList<Integer> picks = new ArrayList<>();

		XVertex pick = (XVertex) this.vertices.get(this.vertices.size() - this.numXVertices + xPick);
		// Check: does this vertex already exist in Nj? If so, pick another...
		while (nj.contains(pick) || (pick.getCurrentExternalDegree() == pick.getDeterminedExternalDegree())) {

			// pick a new vertex
			xPick = randomGen(this.numXVertices);
			pick = (XVertex) this.vertices.get(this.vertices.size() - this.numXVertices + xPick);
		}

		return pick;

	}

	/**
	 * Reveals the relationship between 'w' vertices in the following format: w1:
	 * w2, w3, w4 w2: w1, w3: w1 . . . etc This will facilitate printing the
	 * relationship between 'w' nodes.
	 * 
	 * @return String in the above format used to print to screen or compare.
	 */
	public String revealRelationshipsInW() {
		String w = "";
		if (this.numWVertices > 0) {
			for (int i = 0; i < this.numWVertices; i++) {
				// find w_i and print it with all its neighbors
				w += "w" + i + ": " + this.vertices.get(i).getWNeighbors() + "\n";
			}
		}
		return w;
	}

	public String revealAllRelationshipsInW() {
		String w = "";
		if (this.numWVertices > 0) {
			for (int i = 0; i < this.numWVertices; i++) {
				// find w_i and print it with all its neighbors
				w += "w" + i + ": " + this.vertices.get(i).getNeighbors() + "\n";
			}
		}
		return w;
	}

	/**
	 * Reveals the relationship between 'x' vertices in the following format: x1:
	 * x2, x3, x4 x2: x1, x3: x1 . . . etc This will facilitate printing the
	 * relationship between 'x' nodes.
	 *
	 * @return String in the above format used to print to screen or compare.
	 */
	public String revealRelationshipsInX() {
		String x = "";
		if (this.numXVertices > 0) {
			int start = this.vertices.size() - this.numXVertices; // location where X vertices start
			for (int i = start; i < this.vertices.size(); i++) {
				// find x_i and print it with all its neighbors
				x += this.vertices.get(i).toString() + ": " + this.vertices.get(i).getNeighbors() + "\n";
			}
		}
		return x;
	}

	/**
	 * Generates and return a random integer between 0 and upperBound
	 *
	 * @param upperBound The upperBound of the desired random integer.
	 * @return an integer ranging from 0 to upperBound inclusive.
	 */
	private int randomGen(int upperBound) {
		Random r = new Random();
		return r.nextInt(upperBound);
	}

	/**
	 * Return the number of edges in the graph.
	 *
	 * @return number of edges in the graph.
	 */
	public int getNumEdges() {
		return this.actualTotalEdges;
	}

	/**
	 * Generates a string that represents the graph - used for testing small graphs
	 * . . . probably not good to test with graph of large size.
	 *
	 * @return a string that visualizes the connectivity of the graph
	 */
	public String toString() {
		String s = "";
		// Loop through all the vertices and add their neighbors to a string
		for (int i = 0; i < this.vertices.size(); i++) {
			s += this.vertices.get(i).toString() + ": " + this.vertices.get(i).getNeighbors() + "\n";

		}
		s += "Number of Total Edges: " + this.actualTotalEdges;
		return s;
	}

	/**
	 * Delete a given number of edges at random from the graph
	 *
	 * @param percentEdges is the percentage of edges to delete.
	 * @return the number of deleted edges
	 */
	public int deleteEdges(int percentEdges) {
		int rand;
		int numEdgesToDelete = this.actualTotalEdges * percentEdges / 100;
		while (numEdgesToDelete > 0) {
			rand = randomGen(this.vertices.size());
			if (vertices.get(rand).removeRandomEdge()) {
				numEdgesToDelete--;
				this.actualTotalEdges--;
			}
		}
		return numEdgesToDelete;
	}

	public void determined0d1() {

		int upperBound = (int) (Math.log(this.numNVertices + this.numWVertices)); // pg 184 in paper
		int lowerBound = this.randomGen(upperBound); // gives me an external degree between 0 and upperBound

//		this.d0 = lowerBound;
//		this.d1 = upperBound;

		this.d0 = 10;
		this.d1 = 20;

	}

	/**
	 * Recover the subgraph H
	 *
	 * @param
	 * @return
	 */
	public String recoverH() {
		ArrayList<Vertex> candidateX0 = new ArrayList<>();
		// populate an array with all the degree values for all x vertices
		for (int i = 0; i < this.numXVertices; i++) {
			this.degreeX[i] = this.vertices.get(this.vertices.size() - this.numXVertices + i).getVertexDegree();
		}

		// Create a list of all vertices that match the degree of x0
		for (int k = 0; k < this.vertices.size(); k++) {
			if (this.vertices.get(k).getVertexDegree() == this.degreeX[0]) {
				// create an initial list of candidates for x0

				candidateX0.add(this.vertices.get(k));
			}
		}

		System.out.println("Pruned array list: " + candidateX0.toString());

		for (int j = 0; j < candidateX0.size(); j++) {
			// Loop through all possible x0 to look at their neighbors and compare to degree
			// of x1
			this.branches.add(new ArrayList<Vertex>());
			this.branches.get(j).add(candidateX0.get(j));
			addVertexToTree(candidateX0.get(j), 1, j);

		}
		System.out.println(branches.toString());
		return "Possible: " + candidateX0.size();
	}

	private void addVertexToTree(Vertex candidate, int degree, int j) {

		if (candidate.getVertexDegree() > 0 || degree < this.numXVertices) {
			for (int i = 0; i < candidate.getVertexDegree(); i++) {
				// Add leafs only if they match the corresponding degree of xi

//				if (this.numXVertices == degree) {
//					break;
//				}

				if ((candidate.getNeighbor(i).getVertexDegree() == this.degreeX[degree])
						&& !(this.branches.get(j).contains(candidate.getNeighbor(i))) && !this.found) {
					candidate.getNeighbor(i).setParent(candidate.toString());
					candidate.getNeighbor(i).setLevelInTree(degree);
					myTree.addLeaf(candidate.getNeighbor(i));

					// Add to branch
					this.branches.get(j).add(candidate.getNeighbor(i));

					if (degree == (this.numXVertices - 1)) {
						this.found = true;
					} else {

						this.addVertexToTree(candidate.getNeighbor(i), ++degree, j);
					}
				}
			}
		}

	}

	public String recoverW(){
		//Call function to remove all X edges in H

		this.trimX();
		this.findA
		return "";
	}


	private void trimX(){
		int indexIntoBranches=0;
		for (int i=0; i<this.branches.size(); i++){
			if (this.branches.get(i).size()==this.numXVertices){
				indexIntoBranches = i;
			}
		}
		for (int k=0; k<this.branches.get(indexIntoBranches).size(); k++){
			for (int j=0; j<this.branches.get(indexIntoBranches).size(); j++){
				//Ignore if the index is the same - otherwise remove the edges
				if (!(k==j)){
					this.branches.get(indexIntoBranches).get(k).removeNeighbor(this.branches.get(indexIntoBranches).get(j));
					this.branches.get(indexIntoBranches).get(j).removeNeighbor(this.branches.get(indexIntoBranches).get(k));
				}
			}
		}
	}

}
