/**
 * Representa una transición en un autómata (AFD o AFN).
 * Una transición está formada por:
 *  - Estado origen
 *  - Símbolo leído (puede ser 'ε' en AFN)
 *  - Estado destino
 */
public class Transicion {

    private String origen;     // Estado desde el que parte la transición
    private String simbolo;    // Símbolo leído (String porque puede ser "ε")
    private String destino;    // Estado al que llega la transición

    //Constructor de la transición.
    public Transicion(String origen, String simbolo, String destino) {
        this.origen = origen;
        this.simbolo = simbolo;
        this.destino = destino;
    }


    public String getOrigen() {
        return origen;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getDestino() {
        return destino;
    }



    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    @Override
    public String toString() {
        return origen + " --" + simbolo + "--> " + destino;
    }
}
