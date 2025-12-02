import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class MenuGR {

    public static void ejecutar(Scanner sc) {

        System.out.println("\n--- Gramática Regular ---");
        System.out.println("Formato de producción: A -> aB");
        System.out.println("Escribe 'fin' para terminar.\n");

        Map<String, List<String>> producciones = new HashMap<>();
        String inicial = "S";

        while (true) {
            System.out.print("Producción: ");
            String linea = sc.nextLine().trim();

            if (linea.equalsIgnoreCase("fin"))
                break;

            if (!linea.contains("->")) {
                System.out.println("Formato inválido.");
                continue;
            }

            String[] partes = linea.split("->");
            String izq = partes[0].trim();
            String[] der = partes[1].trim().split("\\|");

            producciones.putIfAbsent(izq, new ArrayList<>());
            for (String p : der)
                producciones.get(izq).add(p.trim());
        }

        AFN afn = ConvertidorGRaAFN.convertir(producciones, inicial);

        System.out.println("Gramática cargada. Probando palabras...\n");

        while (true) {
            System.out.print("Ingresa una palabra: ");
            String palabra = sc.nextLine();

            Resultado r = afn.procesar(palabra);

            System.out.println("\nRecorrido:");
            System.out.println(r.getRecorrido());
            System.out.println("Resultado: " + (r.isAceptada() ? "ACEPTADA" : "RECHAZADA"));

            System.out.print("\n¿Probar otra palabra? (s/n): ");
            if (!sc.nextLine().equalsIgnoreCase("s"))
                break;
        }
    }
}
