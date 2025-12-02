import java.util.*;

/**
 * Convierte una Gramática Regular a un AFN equivalente
 */
public class ConvertidorGRaAFN {

    public static AFN convertir(Map<String, List<String>> producciones, String inicial) {

        List<Transicion> transiciones = new ArrayList<>();
        Set<String> finales = new HashSet<>();

        for (String noTerminal : producciones.keySet()) {
            for (String derivacion : producciones.get(noTerminal)) {

                if (derivacion.equals("ε")) {
                    finales.add(noTerminal);
                } else if (derivacion.length() == 1) { // terminal solo
                    String simbolo = derivacion;
                    String estadoFinal = "F_" + noTerminal;
                    finales.add(estadoFinal);
                    transiciones.add(new Transicion(noTerminal, simbolo, estadoFinal));
                } else { // terminal + no terminal
                    String simbolo = derivacion.substring(0,1);
                    String destino = derivacion.substring(1);
                    transiciones.add(new Transicion(noTerminal, simbolo, destino));
                }
            }
        }

        return new AFN(inicial, new ArrayList<>(finales), transiciones);
    }
}
