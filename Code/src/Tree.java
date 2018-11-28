import java.util.ArrayList;

public class Tree {

    private Vertex root;
    private ArrayList<Vertex> tree = new ArrayList<>();
    private int treeLevel;

    public void Tree(){

    }

    public void addLeaf(Vertex v){
        this.tree.add(v);

    }

}
