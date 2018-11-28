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
		this.numWVertices = (int)Math.pow(Math.log(this.numNVertices),2);
		this.numXVertices = (int) ((2 + epsilon) * Math.log(this.numNVertices+this.numWVertices));
		this.desiredTotalEdges = edges;

		vertices = new ArrayList<Vertex>(0);

		// Create all the vertices in the graph
		this.createWVertices();
		this.createNVertices();
		// this.createXVerticies();

		// Create random edges between all the N and W vertices
		this.createWEdges();
		this.actualTotalEdges = this.createEdges();

		// Creating and connecting H
		this.determined0d1();
		this.createH();
		this.connectH();

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
	private void createWEdges() {
		for (int i = 0; i < this.numWVertices; i++) {
			for (int j = 0; j < this.randomGen(this.numWVertices); j++) {
				int neighbor = this.randomGen(this.numWVertices - 1);
				if (i != neighbor) {
					this.vertices.get(i).addNeighbor(this.vertices.get(neighbor));
					this.vertices.get(neighbor).addNeighbor(this.vertices.get(i));
				}
			}
		}
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
			this.vertices.get(i+1).addNeighbor(this.vertices.get(i));
		}

		// Now add random edges between X vertices with prob 1/2
		for (int i = start; i < end; i++) {
			for (int j = start; j < end; j++) {
				// add an edge if randomGen returns 0; don't add an edge if randomGen returns 1
				if (i != j) {
					int result = this.randomGen(2);
					//System.out.println(result);
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
			for (int j = 0; j <= this.randomGen(avgNumEdgesPerVertex * 2); j++) {
				// select a random vertex from this.vertices (vertex list) and add as neighbor
				// ensure we don't add a vertex as a neighbor of itself
				int neighbor = this.randomGen(this.vertices.size());
				if (neighbor != i)
					if (this.vertices.get(i).addNeighbor(this.vertices.get(neighbor))){
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
		// Connect X vertices to W vertices
		int c = 2;
		ArrayList<ArrayList<XVertex>> set = new ArrayList<>();

		for (int i = 0; i < this.numWVertices; i++) {   // for each W vertex
			int numXvertInSet = this.randomGen(c) + 2;  // get a random number between 1 and c inclusive -- gives us the
														// size of Nj

			// Set<XVertex> nj = new HashSet<XVertex>();
			ArrayList<XVertex> nj = new ArrayList<>();

			for (int j = 0; j < numXvertInSet; j++) {
				XVertex pick = this.pickAValidXForW(nj);

				// add the vertex to nj
				nj.add(pick);

			}

			// Check: does nj already exist in set?
			while (set.contains(nj)) {
				nj.remove(nj.size() - 1); // remove the last
				XVertex pick = this.pickAValidXForW(nj);
				nj.add(pick); // add new random x vertex to nj
			}

			// increment the currentExternalDegrees of all x's in nj
			for (XVertex x : nj) {
				x.incrementCurrentExternalDegree();
			}

			// add nj to set
			set.add(nj);

			// add links from w_i to x_i's in nj --- or maybe I should be adding links FROM
			// x_i's to respective w's?
			Vertex w = this.vertices.get(i);
			for (int k = 0; k < nj.size(); k++) {
				this.vertices.get(i).addNeighbor(nj.get(k));
				//w.addNeighbor(nj.get(k));
			}

			// Move on to the next W

		}

		System.out.println("The set: " + set.toString());
		// Check current external degrees for each x vertex now

		/* Testing print statements below */

//		int start = this.vertices.size() - this.numXVertices;
//		int end = this.vertices.size();
//

//		for (int i = start; i < end; i++) {
//			System.out.println("Xvertex: " + ((XVertex) this.vertices.get(i)).toString() + " currExt: "
//					+ ((XVertex) this.vertices.get(i)).getCurrentExternalDegree() + " deterExt: "
//					+ ((XVertex) this.vertices.get(i)).getDeterminedExternalDegree());
//		}
//
//		String str = "";
//		for (int i = start; i < end; i++) {
//			str += this.vertices.get(i).toString() + ": " + this.vertices.get(i).getNeighbors() + "\n";
//		}
//		System.out.println(str);
//
//		String s = "";
//		for (int i = 0; i < this.numWVertices; i++) {
//			s += this.vertices.get(i).toString() + ": " + this.vertices.get(i).getNeighbors() + "\n";
//
//		}
//		System.out.println(s);

		// Connect X vertices to G - H per 'external degree' of each X vertex

	}

	private XVertex pickAValidXForW(ArrayList<XVertex> nj) {
		// pick a random x vertex
		int xPick = randomGen(this.numXVertices);

		XVertex pick = (XVertex) this.vertices.get(this.vertices.size() - this.numXVertices + xPick);

		// Check: does this vertex already exist in Nj? If so, pick another...
		while (nj.contains(pick) || (pick.getCurrentExternalDegree() == pick.getDeterminedExternalDegree())) {
			// pick a new vertex
			System.out.println("I found one! " + pick.toString() + " ___ " + nj.toString());
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

    /**
     * Reveals the relationship between 'x' vertices in the following format:
     * x1: x2, x3, x4
     * x2: x1, x3: x1 . . . etc This will facilitate printing the relationship between 'x' nodes.
     *
     * @return String in the above format used to print to screen or compare.
     */
    public String revealRelationshipsInX() {
        String x = "";
        if (this.numXVertices > 0) {
            int start = this.vertices.size() - this.numXVertices; //location where X vertices start
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

		int upperBound = (int) (Math.log(this.numNVertices) / Math.log(2.0)); // pg 184 in paper
		int lowerBound = this.randomGen(upperBound); // gives me an external degree between 0 and upperBound

		this.d0 = lowerBound;
		this.d1 = upperBound;

	}

}
