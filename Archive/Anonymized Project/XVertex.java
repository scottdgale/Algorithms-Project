
public class XVertex extends Vertex implements Comparable<XVertex> {

	private int currentExternalDegree;
	private int determinedExternalDegree;

	public XVertex(char typeOfVertex, int uniqueId, int externalDegree) {
		super(typeOfVertex, uniqueId);
		this.determinedExternalDegree = externalDegree;
		this.currentExternalDegree = 0;

	}

	public int getCurrentExternalDegree() {
		return this.currentExternalDegree;
	}

	public int getDeterminedExternalDegree() {
		return this.determinedExternalDegree;
	}

	public void incrementCurrentExternalDegree() {
		this.currentExternalDegree++;
	}

	@Override
	public int compareTo(XVertex v) {

	    return (this.getId() < v.getId() ? -1 : 
	            (this.getId() == v.getId() ? 0 : 1));
	}

}
