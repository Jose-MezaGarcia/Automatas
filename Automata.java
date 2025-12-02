import java.util.List;

public abstract class Automata {

    protected String estadoInicial;          // Estado inicial
    protected List<String> estadosFinales;   // Lista de estados finales
    protected List<Transicion> transiciones; // Todas las transiciones del autómata

    // Procesa una palabra y devuelve si es aceptada o no + recorrido
    public abstract Resultado procesar(String palabra);

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public List<String> getEstadosFinales() {
        return estadosFinales;
    }

    public List<Transicion> getTransiciones() {
        return transiciones;
    }
}
