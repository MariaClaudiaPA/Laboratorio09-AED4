package btree;

import java.util.ArrayList;
import java.util.List;

public class BNode<E extends Comparable<E>> {

    public int n;
    protected ArrayList<E> keys;
    protected ArrayList<BNode<E>> childs;
    protected int count;
    private int idNode;
    private static int nextId = 1;
    private BNode<E> parent;
    private int level;

    public BNode(int n) {
        this.level = 0;
        this.n = n;
        this.keys = new ArrayList<>(n - 1);
        this.childs = new ArrayList<>(n);
        this.count = 0;
        this.idNode = nextId++;
        this.parent = null;

        for (int i = 0; i < n - 1; i++) {
            keys.add(null);
        }
        for (int i = 0; i < n; i++) {
            childs.add(null);
        }

    }

    boolean isLeaf() {
        return childs.get(0) == null;
    }

    public List<BNode<E>> getChilds() {
        return childs;
    }

    public ArrayList<E> getKeys() {
        return keys;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void addChild(BNode<E> child) {
        childs.add(child);
        child.setParent(this);
    }

    public int getCount() {
        return count;
    }

    public int getIdNode() {
        return this.idNode;
    }

    public void setIdNode(int idNode) {
        this.idNode = idNode;
    }

    public void setParent(BNode<E> parent) {
        this.parent = parent;
    }

    public BNode<E> getParent() {
        return parent;
    }

    public boolean nodeFull() {
        return count == n - 1;
    }

    public boolean nodeEmpty() {
        return this.count == 0;
    }

    public void setKeys(ArrayList<E> keys) {
        this.keys = keys;
    }

    public boolean searchNode(E key, int[] pos) {
        int i = 0;
        while (i < count && key.compareTo(keys.get(i)) > 0) {
            i++;
        }
        pos[0] = i;
        return (i < count && key.compareTo(keys.get(i)) == 0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodo ").append(this.idNode).append(": [");
        for (int i = 0; i < this.count; i++) {
            sb.append(this.keys.get(i));
            if (i < this.count - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public boolean containsKey(E key) {
        for (int i = 0; i < count; i++) {
            if (keys.get(i).equals(key)) {
                return true;
            }
        }
        return false;
    }

    public BNode<E> getChild(int index) {
        if (index < 0 || index >= childs.size()) {
            return null; // o manejar el caso fuera de l�mites seg�n tu l�gica
        }
        return childs.get(index);
    }

 

}
