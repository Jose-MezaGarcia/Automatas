import java.util.*;
public class Transicion {
    /** se arma la transición con su origen, destino, los símbolos que la disparan (sin repetir) y si está resaltada */
    public Estado origen;
    public Estado destino;
    public Set<String> simbolos = new HashSet<>();
    public boolean resaltada = false;

    /** se construye la transición con origen, destino y un símbolo inicial */
    public Transicion(Estado origen, Estado destino, String simbolo) {
        this.origen = origen;
        this.destino = destino;
        this.simbolos.add(simbolo);
    }

    /** se arma la etiqueta juntando todos los símbolos ordenados y separados por comas */
    public String getEtiqueta() {
        List<String> ordenados = new ArrayList<>(simbolos);
        Collections.sort(ordenados);
        return String.join(", ", ordenados);
    }
}