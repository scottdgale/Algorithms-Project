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

	/**
	 * Constructor to instantiate this object
	 *
	 * @param numN  indicates the number of "normal" vertices to create (not W or X
	 *              vertices)
	 * @param numW  indicates the number of "w" vertices to create (targeted nodes)
	 * @param edges indicates the approximate number of edges to create in the graph
	 */
	public Graph(int numN, int numW, int edges) {
		this.countXID = 0;
		this.countWID = 0;
		this.countNID = 0;

		int epsilon = 1;

		this.numNVertices = numN;
		this.numWVertices = numW;
		this.numXVertices = (int) ((2 + epsilon) * Math.log(this.numNVertices) / Math.log(2.0));
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

	private void createXVerticies() {
		System.out.println("Num x vertices: " + this.numXVertices);
		for (int i = 0; i < this.numXVertices; i++) {
			vertices.add(new Vertex('x', this.countXID++));
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
		int end = (this.vertices.size() - 1);
		// Add successive edges
		for (int i = start; i < end; i++) {
			this.vertices.get(i).addNeighbor(this.vertices.get(i + 1));
		}

		// Now add random edges between X vertices
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
					if (this.vertices.get(i).addNeighbor(this.vertices.get(neighbor)))
						countSuccessfulEdges++;
			}
		}
		return countSuccessfulEdges;
	}

	/**
	 * Create H will create a certain number of 'x' vertices and connect them to
	 * each other according to the given algorithm.
	 */
	public void createH() {
		// Create H per the algorithm

		// Create vertices of X --- The number of vertices in X is taken from the paper
		// k = (2 + epsilon)log n
		this.createXVerticies();

		// Create edges within X (both successive and random)
		this.createXEdges();

		// determine external degree for each x_i
		// add edges to G-H (number of edges matches the external degree of that vertex)

	}

	/**
	 * ConnectH will connect 'x' vertices to 'w' vertices IAW the published
	 * algorithm.
	 */
	private void connectH() {
		// Connect H to G per the algorithm
		// Connect X vertices to W vertices

		// Connect X vertices to G - H per 'external degree'

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

}
