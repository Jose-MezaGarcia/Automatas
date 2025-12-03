import java.awt.*;

public class Estado {
    public String nombre;
    public int x, y;
    public boolean esInicial = false;
    public boolean esFinal = false;
    public boolean resaltado = false; // Para la animación

    /* Constante para el tamaño del círculo */
    public static final int RADIO = 30;

    public Estado(String nombre, int x, int y) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
    }

    /* Verifica si el clic cayó dentro del círculo */
    public boolean contiene(int px, int py) {
        return Math.pow(px - x, 2) + Math.pow(py - y, 2) <= RADIO * RADIO;
    }
}