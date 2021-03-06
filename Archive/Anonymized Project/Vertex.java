import java.util.ArrayList;
import java.util.Random;

/**
 * The Vertex Class represents a vertex object in a undirected, unweighted
 * simple graph
 *
 * @author Gale, Roweton, Njeru
 * @version 1.0
 * @since 19 November 2018
 */
public class Vertex {
	// type is either x (for vertices in H), w (vertices we want to exploit), or n
	// (neither x or w)
	private char type;
	// unique integer id - assigned by creating object
	private int id;
	// ArrayList of all connected vertices
	private ArrayList<Vertex> neighbors;
	private String parent;
	private int levelInTree;

	/**
	 * Constructor to instantiate this object
	 *
	 * @param typeOfVertex 'n' normal, 'w' targeted, 'x' exists in H
	 * @param uniqueId     primary key for each vertex
	 */
	public Vertex(char typeOfVertex, int uniqueId) {
		this.type = typeOfVertex;
		this.id = uniqueId;
		this.neighbors = new ArrayList<Vertex>(0);
	}

	/**
	 * Add neighbors (edges/connections) between vertices. This function will NOT
	 * allow duplicate edges
	 *
	 * @param v the vertex to add as a neighbor
	 * @return true if successfully added the vertex as a neighbor
	 */
	public boolean addNeighbor(Vertex v) {
		// check for duplicates before adding
		boolean successfulAdd = false;
		if (!this.neighbors.contains(v)) {
			this.neighbors.add(v);
			successfulAdd = true;
		}
		return successfulAdd;
	}

	/**
	 * Remove neighbors (edges/connections) between vertices. This function will NOT
	 * allow duplicate edges
	 *
	 * @param v the vertex to remove as a neighbor
	 * @return true if successfully removed the vertex as a neighbor
	 */
	public boolean removeNeighbor(Vertex v) {
		// check for duplicates before adding
		boolean successfulRemove = false;
		if (this.neighbors.contains(v)) {
			this.neighbors.remove(v);
			successfulRemove = true;
		}
		return successfulRemove;
	}

	/**
	 * Add neighbors (edges/connections) between vertices. This function will NOT
	 * allow duplicate edges
	 *
	 * @return the degree of the vertex (the number of neighbors)
	 */
	public int getVertexDegree() {
		return this.neighbors.size();
	}

	/**
	 * Generates a string representation of the vertex
	 *
	 * @return the type and id of the vertex in this form: "type_id" (ex "n_123").
	 */
	public String toString() {
		return this.type + "_" + this.id;
	}

	/**
	 * Returns the id of a vertex
	 *
	 * @return id of the vertex
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Generates a string representation all neighbors or connected vertices
	 *
	 * @return the a string of all connected vertices
	 */
	public String getNeighbors() {
		String s = "";
		for (int i = 0; i < this.neighbors.size(); i++) {
			s += this.neighbors.get(i).toString() + ", ";
		}
		return s;
	}

	/**
	 * Generates a string representation all neighbors or connected vertices
	 * 
	 * @param
	 * @return a neighbor of the vertex corresponding to the index
	 */
	public Vertex getNeighbor(int index) {
		if (index < this.neighbors.size()) {
			return this.neighbors.get(index);
		} else
			return new Vertex('q', 99); // error vertex
	}

	/**
	 * Generates an array list of all neighbors
	 * 
	 * @param
	 * @return a arraylist of all neighbors
	 */
	public ArrayList<Vertex> getNeighborsArrayList() {
		return this.neighbors;
	}

	/**
	 * Generates a string representation all 'w' neighbors
	 *
	 * @return only return 'w' neighbors
	 */
	public String getWNeighbors() {
		String s = "";
		for (int i = 0; i < this.neighbors.size(); i++) {
			if (this.neighbors.get(i).type == 'w') {
				s += this.neighbors.get(i).toString() + ", ";
			}
		}
		return s;
	}

	/**
	 * Removes an edge/connection between two vertices
	 *
	 * @param v remove the edge connecting to this vertex (if it exists)
	 * @return true if the vertex was removed / false if the vertex 'v' was NOT a
	 *         neighbor
	 */
	public boolean removeConnection(Vertex v) {
		return this.neighbors.remove(v);
	}

	/**
	 * Removes a random edge between two vertices
	 *
	 * @return true if the vertex was removed / false if the vertex 'v' was NOT a
	 *         neighbor
	 */
	public boolean removeRandomEdge() {
		boolean removed = false;
		if (this.neighbors.size() > 0) {
			// Delete a neighbor
			int rand = this.randomGen(this.neighbors.size());
			this.neighbors.remove(rand);
			removed = true;
		}
		return removed;
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
	 * Sets the parent of the vertex for the search tree
	 *
	 * @param parent
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * Sets the level of the vertex in the tree
	 *
	 * @param level
	 */
	public void setLevelInTree(int level) {
		this.levelInTree = level;
	}

}