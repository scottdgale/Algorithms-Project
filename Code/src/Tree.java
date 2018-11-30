import java.util.ArrayList;

public class Tree {

	private ArrayList<Vertex> tree = new ArrayList<>();

	public void Tree() {

	}

	/**
	 * Add a vertex to the tree
	 * 
	 * @param v - vertex to be added
	 */
	public void addLeaf(Vertex v) {
		this.tree.add(v);

	}

}
