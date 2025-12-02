public class Resultado {

    private boolean aceptada;  // true si la palabra es aceptada
    private String recorrido;  // texto con el recorrido paso a paso

    public Resultado(boolean aceptada, String recorrido) {
        this.aceptada = aceptada;
        this.recorrido = recorrido;
    }

    public boolean isAceptada() {
        return aceptada;
    }

    public String getRecorrido() {
        return recorrido;
    }
}
