
import java.util.ArrayList;
import java.util.Collections;

public class XVertexSorter {

	ArrayList<XVertex> xVertex = new ArrayList<>();

	public XVertexSorter(ArrayList<XVertex> xVertex) {
		this.xVertex = xVertex;
	}

	public ArrayList<XVertex> getSortedXVertices() {
		Collections.sort(xVertex);
		return xVertex;
	}

}
