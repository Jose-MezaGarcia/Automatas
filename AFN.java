import java.util.*;

/**
 * Implementación de un Autómata Finito No Determinista (AFN)
 * Incluye soporte para transiciones epsilon ("e")
 */
public class AFN extends Automata {

    public AFN(String estadoInicial, List<String> estadosFinales, List<Transicion> transiciones) {
        this.estadoInicial = estadoInicial;
        this.estadosFinales = estadosFinales;
        this.transiciones = transiciones;
    }

    @Override
    public Resultado procesar(String palabra) {

        StringBuilder recorrido = new StringBuilder();

        Set<String> actuales = new HashSet<>();
        actuales.add(estadoInicial);

        recorrido.append("Inicio en: ").append(actuales).append("\n");

        // Cerradura epsilon inicial
        actuales = cerraduraEpsilon(actuales);
        recorrido.append("Cerradura ε inicial: ").append(actuales).append("\n");

        // Procesa símbolo por símbolo
        for (char simbolo : palabra.toCharArray()) {

            // Ignorar si el usuario escribe 'e'
            if (simbolo == 'e') continue;

            recorrido.append("Leyendo: ").append(simbolo).append("\n");

            Set<String> nuevos = new HashSet<>();

            for (String estado : actuales) {
                for (Transicion t : transiciones) {

                    if (t.getOrigen().equals(estado) &&
                            t.getSimbolo().equals(String.valueOf(simbolo))) {

                        nuevos.add(t.getDestino());
                        recorrido.append("De ").append(estado)
                                .append(" con ").append(simbolo)
                                .append(" → ").append(t.getDestino()).append("\n");
                    }
                }
            }

            if (nuevos.isEmpty()) {
                recorrido.append("No existen transiciones válidas.\n");
                return new Resultado(false, recorrido.toString());
            }

            // Cerradura epsilon después de cada movimiento
            nuevos = cerraduraEpsilon(nuevos);

            recorrido.append("Después de ε-cierre: ").append(nuevos).append("\n");

            actuales = nuevos;
        }

        boolean aceptada = false;
        for (String estado : actuales) {
            if (estadosFinales.contains(estado)) {
                aceptada = true;
                break;
            }
        }

        recorrido.append("Estados finales alcanzados: ").append(actuales).append("\n");

        return new Resultado(aceptada, recorrido.toString());
    }

    // Cerradura epsilon con símbolo "e"
    private Set<String> cerraduraEpsilon(Set<String> estados) {

        Set<String> resultado = new HashSet<>(estados);
        boolean cambio;

        do {
            cambio = false;

            for (String estado : new HashSet<>(resultado)) {
                for (Transicion t : transiciones) {

                    if (t.getOrigen().equals(estado) && t.getSimbolo().equals("e")) {

                        if (!resultado.contains(t.getDestino())) {
                            resultado.add(t.getDestino());
                            cambio = true;
                        }
                    }
                }
            }
        } while (cambio);

        return resultado;
    }

    // MÉTODO ESTÁTICO PARA USAR DESDE Main
    public static void ejecutar(Scanner sc) {
        System.out.println("\n=== Crear y probar AFN ===");

        // Ejemplo sencillo de AFN; luego puedes cambiarlo para leer desde teclado
        List<String> finales = new ArrayList<>();
        finales.add("q2");

        List<Transicion> transiciones = new ArrayList<>();
        // Ejemplo con épsilon: q0 --e--> q1, q1 --a--> q2
        transiciones.add(new Transicion("q0", "e", "q1"));
        transiciones.add(new Transicion("q1", "a", "q2"));

        AFN afn = new AFN("q0", finales, transiciones);

        System.out.print("Ingresa palabra a evaluar: ");
        String palabra = sc.nextLine();

        Resultado r = afn.procesar(palabra);
        System.out.println(r.getRecorrido());
        System.out.println("¿Aceptada? " + r.isAceptada());
    }
}
