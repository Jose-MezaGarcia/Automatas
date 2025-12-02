import java.util.*;

/**
 * Implementación de un Autómata Finito Determinista (AFD)
 */
public class AFD extends Automata {

    // Constructor: recibe inicial, finales y transiciones
    public AFD(String estadoInicial, List<String> estadosFinales, List<Transicion> transiciones) {
        this.estadoInicial = estadoInicial;
        this.estadosFinales = estadosFinales;
        this.transiciones = transiciones;
    }

    @Override
    public Resultado procesar(String palabra) {

        StringBuilder recorrido = new StringBuilder(); // Guarda el recorrido
        String estadoActual = estadoInicial;           // Comienza en el estado inicial

        recorrido.append("Inicio en: ").append(estadoActual).append("\n");

        // Recorre cada símbolo de la palabra
        for (int i = 0; i < palabra.length(); i++) {
            char simbolo = palabra.charAt(i);

            recorrido.append("Leyendo: ").append(simbolo).append("\n");

            String siguiente = null;

            // Busca una transición válida del AFD
            for (Transicion t : transiciones) {
                if (t.getOrigen().equals(estadoActual) && t.getSimbolo().equals(String.valueOf(simbolo))) {
                    siguiente = t.getDestino();
                    break; // AFD → solo una transición posible
                }
            }

            if (siguiente == null) {
                // No hay transición → rechazada
                recorrido.append("No existe transición desde ")
                        .append(estadoActual).append(" con ").append(simbolo).append("\n");
                return new Resultado(false, recorrido.toString());
            }

            recorrido.append("Va a: ").append(siguiente).append("\n");
            estadoActual = siguiente; // Actualiza estado
        }

        // Verifica si terminó en un estado final
        boolean aceptada = estadosFinales.contains(estadoActual);
        recorrido.append("Termina en: ").append(estadoActual).append("\n");

        return new Resultado(aceptada, recorrido.toString());
    }

    // MÉTODO ESTÁTICO PARA USAR DESDE Main
    public static void ejecutar(Scanner sc) {
        System.out.println("\n=== Crear y probar AFD ===");

        // Ejemplo sencillo de AFD (luego lo puedes cambiar a lectura dinámica)
        // Lenguaje: a+ (una o más 'a')
        List<String> finales = new ArrayList<>();
        finales.add("q1");

        List<Transicion> transiciones = new ArrayList<>();
        transiciones.add(new Transicion("q0", "a", "q1"));
        transiciones.add(new Transicion("q1", "a", "q1"));

        AFD afd = new AFD("q0", finales, transiciones);

        System.out.print("Ingresa palabra a evaluar: ");
        String palabra = sc.nextLine();

        Resultado r = afd.procesar(palabra);
        System.out.println(r.getRecorrido());
        System.out.println("¿Aceptada? " + r.isAceptada());
    }
}
