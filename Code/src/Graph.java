import java.util.ArrayList;
import java.util.Random;

/**
 * The Graph Class represents a graph undirected, unweighted simple graph that
 * contains vertices and edges (represented by neighbors in the Vertex class).
 *
 * @author  Gale, Roweton, Njeru
 * @version 1.0
 * @since   19 November 2018
 */
public class Graph {
    //contains all the vertices in the graph
    private ArrayList<Vertex> vertices;
    //counters used to assign id's to the different type of nodes
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
     * @param numN indicates the number of "normal" vertices to create (not W or X vertices)
     * @param numW indicates the number of "w" vertices to create (targeted nodes)
     * @param edges indicates the approximate number of edges to create in the graph
     */
    public Graph(int numN, int numW, int edges){
        this.countXID = 0;
        this.countWID = 0;
        this.countNID = 0;

        this.numNVertices = numN;
        this.numWVertices = numW;
        this.desiredTotalEdges = edges;
        //this.numXVertices = numX;


        vertices = new ArrayList<Vertex>(0);

        //Create all the vertices in the graph
        this.createNVertices();
        this.createWVertices();

        //Create random edges between all the N and W vertices
        this.actualTotalEdges = this.createEdges();

        //Creating and connecting H
        this.createH();
        this.connectH();


    }

    /**
     * Create this.numNVertices in the graph
     */
    private void createNVertices(){
        for (int i = 0; i<this.numNVertices; i++){
            vertices.add(new Vertex('n', this.countNID++));
        }
    }

    /**
     * Create this.numNVertices in the graph
     */
    private void createWVertices(){
        for (int i = 0; i<this.numWVertices; i++){
            vertices.add(new Vertex('w', this.countWID++));
        }
    }

    /**
     * Generates random edges for each vertex in the graph. The number of edges created for each
     * vertex is computed by dividing the total number of desired edges in the graph (this.numTotalEdges)
     * by the number of vertices in the graph (this.vertices.size()) and then generating a random
     * integer between 1 and 2 * that number.
     * @return the number of edges actually created.
     */
    private int createEdges(){
        //Use random numbers to generate random number of edges for each vertex
        int avgNumEdgesPerVertex = this.desiredTotalEdges/this.vertices.size();
        int countSuccessfulEdges = 0;
        //In order to determine the number of edges to create for each vertex
        //I am getting a random number between 0 and 2 times the avgNumEdgesPerVertex
        //then adding neighbors (edges are represented by the number of neighbors a vertex has)
        for (int i=0;i<this.vertices.size(); i++){
            for (int j=0; j<=this.randomGen(avgNumEdgesPerVertex*3); j++){
                //select a random vertex from this.vertices (vertex list) and add as neighbor
                //ensure we don't add a vertex as a neighbor of itself
                int neighbor = this.randomGen(this.vertices.size());
                if (neighbor != i)
                    if(this.vertices.get(i).addNeighbor(this.vertices.get(neighbor)))
                        countSuccessfulEdges++;
            }
        }
        return countSuccessfulEdges;
    }


    /**
     * Create H  will create a certain number of 'x' vertices and connect them to each other
     * according to the given algorithm.
     */
    public void createH(){
        //Create H per the algorithm
    }

    /**
     * ConnectH will connect 'x' vertices to 'w' vertices IAW the published algorithm.
     */
    private void connectH(){
        //Connect H to G per the algorithm

    }

    /**
     * Reveals the relationship between 'w' vertices in the following format:
     * w1 - w2, w3, w4
     * w2 - w1
     * w3 - w1 . . . etc
     * This will facilitate printing the relationship between 'w' nodes.
     * @return String in the above format used to print to screen or compare.
     */
    public String revealRelationshipsInW(){
        //This will return a string that explains how all the 'w' nodes are connected



        return null;
    }

    /**
     * Generates and return a random integer between 0 and upperBound
     *
     * @param upperBound The upperBound of the desired random integer.
     * @return an integer ranging from 0 to upperBound inclusive.
     */
    private int randomGen (int upperBound){
        Random r = new Random();
        return r.nextInt(upperBound);
    }


    /**
     * Return the number of edges in the graph.
     *
     * @return number of edges in the graph.
     */
    public int getNumEdges(){
        return this.actualTotalEdges;
    }

    /**
     * Generates a string that represents the graph - used for testing small graphs . . .
     * probably not good to test with graph of large size.
     *
     * @return a string that visualizes the connectivity of the graph
     */
    public String toString(){
        String s = "";
        //Loop through all the vertices and add their neighbors to a string
        for (int i=0; i<this.vertices.size(); i++){
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
    public int deleteEdges (int percentEdges) {
        int rand;
        int numEdgesToDelete = this.actualTotalEdges*percentEdges/100;
        while (numEdgesToDelete>0){
            rand = randomGen(numEdgesToDelete);
            if (vertices.get(rand).removeRandomEdge()){
                numEdgesToDelete--;
                this.actualTotalEdges--;
            }
        }
        return numEdgesToDelete;
    }

}
