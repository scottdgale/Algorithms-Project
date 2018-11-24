import java.util.ArrayList;


public class Main {

    public static void main(String[] args) {

        System.out.println("Create the Graph");
        Graph myGraph = new Graph(10,5,100);
        System.out.println(myGraph.toString());
        myGraph.deleteEdges(20);
        System.out.println(myGraph.toString());

    }
}
