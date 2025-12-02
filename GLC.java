import java.util.*;

/**
 * Gramática Libre de Contexto (GLC)
 */
public class GLC {

    private String inicial;
    private Map<String, List<String>> producciones;

    public GLC(String inicial, Map<String, List<String>> producciones) {
        this.inicial = inicial;
        this.producciones = producciones;
    }

    // Derivación por la izquierda (solo ejemplo simple)
    public void derivarIzquierda(String palabra) {
        System.out.println("(Derivación izquierda simulada) No implementada completa aún.");
    }

    // Árbol sintáctico simple (solo esquema)
    public void imprimirArbol(String palabra) {
        System.out.println("(Árbol sintáctico simulado) No implementado visual completo.");
    }
}
