import java.util.*;

/**
 * Autómata de Pila (AP)
 */
public class AutomataPila {

    private String estadoInicial;
    private String simboloPilaInicial;
    private List<String> estadosFinales;
    private List<TransicionAP> transiciones;

    public AutomataPila(String inicial, String pilaInicial, List<String> finales, List<TransicionAP> trans) {
        this.estadoInicial = inicial;
        this.simboloPilaInicial = pilaInicial;
        this.estadosFinales = finales;
        this.transiciones = trans;
    }

    public void simular(String palabra) {
        System.out.println("\nSimulación (modo paso a paso):");
        String estadoActual = estadoInicial;
        Stack<String> pila = new Stack<>();
        pila.push(simboloPilaInicial);

        for (int i = 0; i <= palabra.length(); i++) {
            String simbolo = (i < palabra.length()) ? palabra.substring(i,i+1) : "ε";

            System.out.println("\nEstado actual: " + estadoActual);
            System.out.println("Pila: " + pila);
            System.out.println("Símbolo leído: " + simbolo);

            boolean aplicado = false;

            for (TransicionAP t : transiciones) {
                if (t.getEstadoOrigen().equals(estadoActual) &&
                        t.getSimboloEntrada().equals(simbolo) &&
                        !pila.isEmpty() &&
                        t.getSimboloPila().equals(pila.peek())) {

                    estadoActual = t.getEstadoDestino();
                    pila.pop();
                    if (!t.getReemplazoPila().equals("ε")) {
                        for (int j = t.getReemplazoPila().length()-1; j >= 0; j--) {
                            pila.push("" + t.getReemplazoPila().charAt(j));
                        }
                    }
                    aplicado = true;
                    break;
                }
            }

            if (!aplicado) {
                System.out.println("No hay transición válida. Cadena rechazada.");
                return;
            }
        }

        if (estadosFinales.contains(estadoActual) && pila.isEmpty())
            System.out.println("Cadena aceptada.");
        else
            System.out.println("Cadena rechazada.");
    }
}
