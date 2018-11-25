
public class XVertex extends Vertex {

	private int externalDegree;

	public XVertex(char typeOfVertex, int uniqueId, int externalDegree) {
		super(typeOfVertex, uniqueId);
		this.externalDegree = externalDegree;

	}

	public int getExternalDegree() {
		return this.externalDegree;
	}

}
