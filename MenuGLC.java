import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class MenuGLC {

    public static void ejecutar(Scanner sc) {

        System.out.println("\n--- Gramática Libre de Contexto ---");
        System.out.println("Producciones tipo: S -> aSb | ε");
        System.out.println("Escribe 'fin' para terminar.\n");

        Map<String, List<String>> producciones = new HashMap<>();
        String inicial = "S";

        while (true) {
            System.out.print("Producción: ");
            String line = sc.nextLine().trim();

            if (line.equals("fin"))
                break;

            if (!line.contains("->")) {
                System.out.println("Formato inválido.");
                continue;
            }

            String[] partes = line.split("->");
            String izq = partes[0].trim();
            String[] der = partes[1].trim().split("\\|");

            producciones.putIfAbsent(izq, new ArrayList<>());
            for (String p : der)
                producciones.get(izq).add(p.trim());
        }

        GLC glc = new GLC(inicial, producciones);

        System.out.print("Ingresa palabra a derivar: ");
        String w = sc.nextLine();

        System.out.println("\nDerivación por la izquierda:");
        glc.derivarIzquierda(w);

        System.out.println("\nÁrbol sintáctico:");
        glc.imprimirArbol(w);
    }
}
