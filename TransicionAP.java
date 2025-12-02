/**
 * Representa una transición de un Autómata de Pila
 */
public class TransicionAP {

    private String estadoOrigen;
    private String simboloEntrada;
    private String simboloPila;
    private String estadoDestino;
    private String reemplazoPila;

    public TransicionAP(String origen, String entrada, String pila, String destino, String reemplazo) {
        this.estadoOrigen = origen;
        this.simboloEntrada = entrada;
        this.simboloPila = pila;
        this.estadoDestino = destino;
        this.reemplazoPila = reemplazo;
    }

    // Convierte una línea de texto a TransicionAP
    // Formato: (q, a, X) -> (p, YZ)
    public static TransicionAP parse(String linea) {
        linea = linea.replaceAll("[()\\s]", "");
        String[] partes = linea.split("->");
        String[] izq = partes[0].split(",");
        String[] der = partes[1].split(",");
        return new TransicionAP(
                izq[0], izq[1], izq[2],
                der[0], der[1]
        );
    }

    public String getEstadoOrigen() { return estadoOrigen; }
    public String getSimboloEntrada() { return simboloEntrada; }
    public String getSimboloPila() { return simboloPila; }
    public String getEstadoDestino() { return estadoDestino; }
    public String getReemplazoPila() { return reemplazoPila; }
}
