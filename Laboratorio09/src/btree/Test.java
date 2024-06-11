package btree;

public class Test {

    public static void main(String[] args) {
        Btree<Integer> btree = new Btree<>(4);
        btree.insert(6);
        btree.insert(25);
        btree.insert(12);
        btree.insert(16);
        btree.insert(30);
        btree.insert(32);
        btree.insert(8);

        System.out.println("Árbol B inicial:");
        System.out.println(btree);

        btree.remove(25);
        System.out.println("Árbol B después de eliminar 30:");
        System.out.println(btree);

    }
}
