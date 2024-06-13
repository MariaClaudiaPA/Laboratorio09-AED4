package btree;

import exceptions.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Btree<E extends Comparable<E>> {

    private BNode<E> root;
    private int orden;
    private boolean up;
    private BNode<E> nDes;

    public Btree(int orden) {
        this.orden = orden;
        this.root = null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void insert(E cl) {
        up = false;
        E mediana;
        BNode<E> pnew;
        mediana = push(this.root, cl);
        if (up) {
            pnew = new BNode<E>(this.orden);
            pnew.count = 1;
            pnew.keys.set(0, mediana);
            pnew.childs.set(0, this.root);
            pnew.childs.set(1, nDes);
            this.root = pnew;
        }
    }

    private E push(BNode<E> current, E cl) {
        int pos[] = new int[1];
        E mediana;
        if (current == null) {
            up = true;
            nDes = null;
            return cl;
        } else {
            boolean fl;
            fl = current.searchNode(cl, pos);
            if (fl) {
                System.out.println("Item duplicado\n");
                up = false;
                return null;
            }
            mediana = push(current.childs.get(pos[0]), cl);
            if (up) {
                if (current.nodeFull()) {
                    mediana = dividedNode(current, mediana, pos[0]);
                } else {
                    up = false;
                    putNode(current, mediana, nDes, pos[0]);
                }
            }
            return mediana;
        }
    }

    private void putNode(BNode<E> current, E cl, BNode<E> rd, int k) {
        int i;
        for (i = current.count - 1; i >= k; i--) {
            current.keys.set(i + 1, current.keys.get(i));
            current.childs.set(i + 2, current.childs.get(i + 1));
        }
        current.keys.set(k, cl);
        current.childs.set(k + 1, rd);
        current.count++;
    }

    private E dividedNode(BNode<E> current, E cl, int k) {
        BNode<E> rd = nDes;
        int i, posMdna;
        if (k <= this.orden / 2) {
            posMdna = this.orden / 2;
        } else {
            posMdna = this.orden / 2 + 1;
        }
        nDes = new BNode<>(this.orden);
        for (i = posMdna; i < this.orden - 1; i++) {
            nDes.keys.set(i - posMdna, current.keys.get(i));
            nDes.childs.set(i - posMdna + 1, current.childs.get(i + 1));
        }
        nDes.count = (this.orden - 1) - posMdna;
        current.count = posMdna;
        if (k <= this.orden / 2) {
            putNode(current, cl, rd, k);
        } else {
            putNode(nDes, cl, rd, k - posMdna);
        }
        E median = current.keys.get(current.count - 1);
        nDes.childs.set(0, current.childs.get(current.count));
        current.count--;
        return median;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isEmpty()) {
            sb.append("BTree vacío");
        } else {
            sb.append("ID Nodo\tClaves\t\tID Padre\tID Hijos\n");
            sb.append(writeTree(this.root));
        }
        return sb.toString();
    }

    private String writeTree(BNode<E> current) {
        StringBuilder sb = new StringBuilder();
        if (current != null) {
            sb.append(current.getIdNode()).append("\t");
            for (int i = 0; i < current.count; i++) {
                sb.append(current.keys.get(i));
                if (i < current.count - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\t\t");
            if (current.getParent() != null) {
                sb.append(current.getParent().getIdNode());
            } else {
                sb.append("-");
            }
            sb.append("\t\t");
            for (int i = 0; i <= current.count; i++) {
                if (current.childs.get(i) != null) {
                    sb.append(current.childs.get(i).getIdNode());
                } else {
                    sb.append("-");
                }
                if (i < current.count) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
            for (int i = 0; i <= current.count; i++) {
                sb.append(writeTree(current.childs.get(i)));
            }
        }
        return sb.toString();
    }

    public boolean search(E cl) {
        if (isEmpty()) {
            return false;
        } else {
            return searchKey(root, cl);
        }
    }

    private boolean searchKey(BNode<E> current, E key) {
        if (current == null) {
            return false;
        }

        int pos[] = new int[1];
        boolean found = current.searchNode(key, pos);

        if (found) {
            System.out.println(key + " se encuentra en el nodo " + current.getIdNode() + " en la posición " + pos[0]);
            return true;
        } else if (current.childs.get(pos[0]) != null) {
            return searchKey(current.childs.get(pos[0]), key);
        } else {
            return false;
        }
    }

    public void remove(E cl) {
        if (root == null) {
            System.out.println("El árbol está vacío.");
            return;
        }

        borrarDeNodo(root, cl);

        if (root.count == 0) {
            if (root.childs.get(0) != null) {
                root = root.childs.get(0);
            } else {
                root = null;
            }
        }
    }

    private void borrarDeNodo(BNode<E> node, E key) {
        int pos[] = new int[1];
        boolean found = node.searchNode(key, pos);

        if (found) {
            if (node.childs.get(pos[0]) == null) {
                borrarDeHoja(node, pos[0]);
            } else {
                borrarDeNodoInterno(node, pos[0]);
            }
        } else {
            if (node.childs.get(pos[0]) == null) {
                System.out.println("La clave " + key + " no se encuentra en el árbol.");
                return;
            }
            boolean isLastChild = (pos[0] == node.count);

            if (node.childs.get(pos[0]).count < orden / 2) {
                fill(node, pos[0]);
            }

            if (isLastChild && pos[0] > node.count) {
                borrarDeNodo(node.childs.get(pos[0] - 1), key);
            } else {
                borrarDeNodo(node.childs.get(pos[0]), key);
            }
        }
    }

    private void borrarDeHoja(BNode<E> node, int idx) {
        for (int i = idx + 1; i < node.count; i++) {
            node.keys.set(i - 1, node.keys.get(i));
        }
        node.count--;
    }

    private void borrarDeNodoInterno(BNode<E> node, int idx) {
        E key = node.keys.get(idx);

        if (node.childs.get(idx).count >= orden / 2) {
            E pred = getAntecesor(node, idx);
            node.keys.set(idx, pred);
            borrarDeNodo(node.childs.get(idx), pred);
        } else if (node.childs.get(idx + 1).count >= orden / 2) {
            E succ = getSucesor(node, idx);
            node.keys.set(idx, succ);
            borrarDeNodo(node.childs.get(idx + 1), succ);
        } else {
            merge(node, idx);
            borrarDeNodo(node.childs.get(idx), key);
        }
    }

    private E getAntecesor(BNode<E> node, int idx) {
        BNode<E> current = node.childs.get(idx);
        while (current.childs.get(current.count) != null) {
            current = current.childs.get(current.count);
        }
        return current.keys.get(current.count - 1);
    }

    private E getSucesor(BNode<E> node, int idx) {
        BNode<E> current = node.childs.get(idx + 1);
        while (current.childs.get(0) != null) {
            current = current.childs.get(0);
        }
        return current.keys.get(0);
    }

    private void fill(BNode<E> node, int idx) {
        if (idx != 0 && node.childs.get(idx - 1).count >= orden / 2) {
            borrowFromLeft(node, idx);
        } else if (idx != node.count && node.childs.get(idx + 1).count >= orden / 2) {
            borrowFromRight(node, idx);
        } else {
            if (idx != node.count) {
                merge(node, idx);
            } else {
                merge(node, idx - 1);
            }
        }
    }

    private void borrowFromLeft(BNode<E> node, int idx) {
        BNode<E> child = node.childs.get(idx);
        BNode<E> sibling = node.childs.get(idx - 1);
        for (int i = child.count - 1; i >= 0; i--) {
            child.keys.set(i + 1, child.keys.get(i));
        }

        if (child.childs.get(0) != null) {
            for (int i = child.count; i >= 0; i--) {
                child.childs.set(i + 1, child.childs.get(i));
            }
        }
        child.keys.set(0, node.keys.get(idx - 1));

        if (child.childs.get(0) != null) {
            child.childs.set(0, sibling.childs.get(sibling.count));
        }
        node.keys.set(idx - 1, sibling.keys.get(sibling.count - 1));
        child.count++;
        sibling.count--;
    }

    private void borrowFromRight(BNode<E> node, int idx) {
        BNode<E> child = node.childs.get(idx);
        BNode<E> sibling = node.childs.get(idx + 1);
        child.keys.set(child.count, node.keys.get(idx));

        if (child.childs.get(0) != null) {
            child.childs.set(child.count + 1, sibling.childs.get(0));
        }
        node.keys.set(idx, sibling.keys.get(0));

        for (int i = 1; i < sibling.count; i++) {
            sibling.keys.set(i - 1, sibling.keys.get(i));
        }
        if (sibling.childs.get(0) != null) {
            for (int i = 1; i <= sibling.count; i++) {
                sibling.childs.set(i - 1, sibling.childs.get(i));
            }
        }
        child.count++;
        sibling.count--;
    }

    private void merge(BNode<E> node, int idx) {
        BNode<E> child = node.childs.get(idx);
        BNode<E> sibling = node.childs.get(idx + 1);

        child.keys.set(orden / 2 - 1, node.keys.get(idx));
        for (int i = 0; i < sibling.count; i++) {
            child.keys.set(i + orden / 2, sibling.keys.get(i));
        }
        if (child.childs.get(0) != null) {
            for (int i = 0; i <= sibling.count; i++) {
                child.childs.set(i + orden / 2, sibling.childs.get(i));
            }
        }
        for (int i = idx + 1; i < node.count; i++) {
            node.keys.set(i - 1, node.keys.get(i));
        }
        for (int i = idx + 2; i <= node.count; i++) {
            node.childs.set(i - 1, node.childs.get(i));
        }
        child.count += sibling.count + 1;
        node.count--;
    }

    public static Btree<Integer> building_btree(String fileName) throws IOException, Exception, ItemNoFound {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        int orden = Integer.parseInt(reader.readLine().trim());
        Btree<Integer> bTree = new Btree<>(orden);

        Map<Integer, BNode<Integer>> nodes = new HashMap<>();
        Map<Integer, Integer> levels = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            int nivel = Integer.parseInt(parts[0].trim());
            int idNode = Integer.parseInt(parts[1].trim());
            BNode<Integer> node = new BNode<>(orden);

            node.setIdNode(idNode);
            for (int i = 2; i < parts.length; i++) {
                node.keys.set(i - 2, Integer.valueOf(parts[i].trim()));
            }
            node.count = parts.length - 2;

            nodes.put(idNode, node);
            levels.put(idNode, nivel);

            if (nivel == 0) {
                bTree.root = node;
            }
        }
        reader.close();
        for (BNode<Integer> node : nodes.values()) {
            int level = levels.get(node.getIdNode());
            if (level > 0) {
                BNode<Integer> parentNode = encontrarPadresPorClaves(bTree.root, node.keys.get(0), level - 1);
                if (parentNode != null) {
                    boolean childAdded = false;
                    for (int i = 0; i <= parentNode.count; i++) {
                        if (parentNode.childs.get(i) == null) {
                            parentNode.childs.set(i, node);
                            childAdded = true;
                            break;
                        }
                    }
                    if (!childAdded) {
                        throw new ItemNoFound("No se encontró espacio para agregar el nodo hijo.");
                    }
                }
            }
        }
        if (!bTree.verificarPropiedades()) {
            throw new Exception("El árbol no cumple con las propiedades de un BTree.");
        }
        return bTree;
    }

    private static BNode<Integer> encontrarPadresPorClaves(BNode<Integer> node, int key, int targetLevel) {
        if (node != null) {
            if (node.count > 0) {
                if (targetLevel == 0) {
                    return node;
                }
                for (int i = 0; i < node.count; i++) {
                    if (key < node.keys.get(i)) {
                        return encontrarPadresPorClaves(node.childs.get(i), key, targetLevel - 1);
                    }
                }
                return encontrarPadresPorClaves(node.childs.get(node.count), key, targetLevel - 1);
            }
        }
        return null;
    }

    private boolean verificarPropiedades() {
        return verificarNodos(this.root);
    }

    private boolean verificarNodos(BNode<E> node) {
        if (node == null) {
            return true;
        }
        if (node.count < (orden / 2) && node != this.root) {
            return false;
        }
        if (node.count > orden - 1) {
            return false;
        }
        for (int i = 1; i < node.count; i++) {
            if (node.keys.get(i - 1).compareTo(node.keys.get(i)) > 0) {
                return false;
            }
        }
        for (int i = 0; i <= node.count; i++) {
            if (node.childs.get(i) != null) {
                if (!verificarNodos(node.childs.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public BNode<E> getRoot() {
        return root;
    }

    public int height() {
        return getHeight(root);
    }

    public int getHeight(BNode<E> node) {
        if (node == null) {
            return 0;
        }
        if (node.isLeaf()) {
            return 1;
        }
        int maxHeight = 0;
        for (BNode<E> child : node.childs) {
            int height = getHeight(child);
            if (height > maxHeight) {
                maxHeight = height;
            }
        }
        return maxHeight + 1;
    }

//    private static BNode<Integer> encontrarPadreLevel(Map<Integer, BNode<Integer>> nodes, Map<Integer, Integer> levels, int key, int targetLevel) {
//        BNode<Integer> parent = null;
//        for (Map.Entry<Integer, BNode<Integer>> entry : nodes.entrySet()) {
//            BNode<Integer> node = entry.getValue();
//            int level = levels.get(node.getIdNode());
//            if (level == targetLevel) {
//                if (node.keys.get(0) == null) {
//                    continue; // Ignorar nodos hoja vacíos
//                }
//                if (parent == null || (key >= parent.keys.get(0) && key < node.keys.get(0))) {
//                    parent = node;
//                }
//            }
//        }
//        return parent;
//    }
    private void setRoot(BNode<Integer> node) {
        this.root = (BNode<E>) node;
    }

}
