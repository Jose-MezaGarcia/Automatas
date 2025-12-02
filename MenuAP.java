import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MenuAP {

    public static void ejecutar(Scanner sc) {

        System.out.println("\n--- Autómata de Pila (AP) ---");
        System.out.println("Transiciones tipo:");
        System.out.println("(q, a, X) -> (p, YZ)");

        List<TransicionAP> trans = new ArrayList<>();

        System.out.print("Estado inicial: ");
        String inicial = sc.nextLine();

        System.out.print("Símbolo inicial de la pila: ");
        String simboloInicial = sc.nextLine();

        System.out.println("Estados finales (coma): ");
        List<String> finales = Arrays.asList(sc.nextLine().split(","));

        System.out.println("Escribe 'fin' para terminar transiciones:");

        while (true) {
            System.out.print("Transición: ");
            String linea = sc.nextLine();
            if (linea.equals("fin"))
                break;

            try {
                trans.add(TransicionAP.parse(linea));
            } catch (Exception e) {
                System.out.println("Formato incorrecto.");
            }
        }

        AutomataPila ap = new AutomataPila(inicial, simboloInicial, finales, trans);

        while (true) {
            System.out.print("Palabra: ");
            String palabra = sc.nextLine();

            ap.simular(palabra);

            System.out.print("¿Probar otra palabra? (s/n): ");
            if (!sc.nextLine().equals("s"))
                break;
        }
    }
}
