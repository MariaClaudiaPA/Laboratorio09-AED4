package btree;
//
import exceptions.ItemNoFound;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
        Btree<Integer> btree = Btree.building_btree("D:\\CLAUDIA\\catolica\\5 - Quinto Semestre\\04 - ALGORITMOS Y ESTRUCTURA DE DATOS\\Fase3\\9\\arbol.txt");
        System.out.println("Árbol B construido:\n" + btree);
//        printFile("D:\\CLAUDIA\\catolica\\5 - Quinto Semestre\\04 - ALGORITMOS Y ESTRUCTURA DE DATOS\\Fase3\\9\\arbol.txt");
        
    }

    public static void printFile(String fileName) {
        try ( BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

}
