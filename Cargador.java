import java.util.*;

public class Cargador {

    // Scanner para leer desde consola
    private final Scanner sc = new Scanner(System.in);

    // Pide lista de estados finales
    public List<String> pedirFinales() {
        System.out.println("Ingrese los estados finales separados por coma:");
        String linea = sc.nextLine();
        String[] partes = linea.split(",");
        List<String> finales = new ArrayList<>();
        for (String p : partes) finales.add(p.trim());
        return finales;
    }

    // Pide transiciones del autómata
    public List<Transicion> pedirTransiciones(boolean esAFN) {
        System.out.println("Ingrese las transiciones. Formato:");
        System.out.println("origen simbolo destino");
        System.out.println("Use 'e' para epsilon. Escriba 'fin' para terminar.");

        List<Transicion> lista = new ArrayList<>();

        while (true) {
            System.out.print("Transición: ");
            String linea = sc.nextLine();

            if (linea.equalsIgnoreCase("fin")) break; // Sale del ciclo

            String[] partes = linea.split(" ");
            if (partes.length != 3) {
                System.out.println("Formato incorrecto. Ejemplo: q0 a q1");
                continue;
            }

            String origen = partes[0];
            String simbolo = partes[1];
            String destino = partes[2];

            // Valida que el símbolo sea aceptado
            if (!simbolo.equals("a") && !simbolo.equals("b") &&
                    !simbolo.equals("0") && !simbolo.equals("1") &&
                    !(esAFN && simbolo.equals("e"))) {

                System.out.println("Símbolo no permitido. Use a, b, 0, 1 o e (solo AFN).");
                continue;
            }

            lista.add(new Transicion(origen, simbolo, destino));
        }

        return lista;
    }
}
