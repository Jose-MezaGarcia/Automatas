import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.util.List;

  /* Esta clase sirve para dibujar los estados */
  /* PanelAFD y PanelGramatica lo aplicaron para no repetir codigo */
public class LienzoAutomata extends JPanel {
    private List<Estado> estados;
    private List<Transicion> transiciones;

    public LienzoAutomata(List<Estado> estados, List<Transicion> transiciones) {
        this.estados = estados;
        this.transiciones = transiciones;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (estados == null || transiciones == null) return;

        /*  Dibuja las transiciones */
        for (Transicion t : transiciones) {
            g2.setColor(t.resaltada ? Color.GREEN : Color.BLACK);
            g2.setStroke(new BasicStroke(t.resaltada ? 3 : 2));

            /* Checar si hay camino de vuelta para curvar la línea */
            boolean curva = false;
            for (Transicion otra : transiciones) {
                if (otra.origen == t.destino && otra.destino == t.origen && t.origen != t.destino) {
                    curva = true; break;
                }
            }

            if (t.origen == t.destino) { // BUCLE (Self-loop)
                dibujarBucle(g2, t);
            } else if (curva) { // CURVA (Ida y vuelta)
                dibujarCurva(g2, t);
            } else { // RECTA NORMAL
                dibujarRecta(g2, t);
            }
        }

        /*Dibuja los estados*/
        for (Estado s : estados) {
            dibujarEstado(g2, s);
        }
    }

    private void dibujarEstado(Graphics2D g2, Estado s) {
        Color relleno = s.resaltado ? new Color(144, 238, 144) : Color.YELLOW; // Verde si resaltado
        g2.setColor(relleno);
        g2.fillOval(s.x - Estado.RADIO, s.y - Estado.RADIO, Estado.RADIO * 2, Estado.RADIO * 2);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(s.x - Estado.RADIO, s.y - Estado.RADIO, Estado.RADIO * 2, Estado.RADIO * 2);

        if (s.esFinal) { // Doble círculo
            g2.drawOval(s.x - Estado.RADIO + 5, s.y - Estado.RADIO + 5, (Estado.RADIO - 5) * 2, (Estado.RADIO - 5) * 2);
        }
        if (s.esInicial) { // Triángulo de inicio
            int[] px = {s.x - Estado.RADIO - 20, s.x - Estado.RADIO, s.x - Estado.RADIO - 20};
            int[] py = {s.y - 10, s.y, s.y + 10};
            g2.fillPolygon(px, py, 3);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String texto = s.nombre;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, s.x - fm.stringWidth(texto) / 2, s.y + 5);
    }

    private void dibujarBucle(Graphics2D g2, Transicion t) {
        int r = Estado.RADIO;
        int cx = t.origen.x, cy = t.origen.y;
        QuadCurve2D q = new QuadCurve2D.Double(
                cx + r * Math.cos(Math.toRadians(-135)), cy + r * Math.sin(Math.toRadians(-135)),
                cx, cy - r - 90,
                cx + r * Math.cos(Math.toRadians(-45)), cy + r * Math.sin(Math.toRadians(-45))
        );
        g2.draw(q);
        double ang = Math.atan2(q.getY2() - (cy - r - 90), q.getX2() - cx);
        dibujarPunta(g2, q.getX2(), q.getY2(), ang);
        dibujarEtiqueta(g2, t.getEtiqueta(), cx, cy - r - 60);
    }

    private void dibujarCurva(Graphics2D g2, Transicion t) {
        double mx = (t.origen.x + t.destino.x) / 2.0;
        double my = (t.origen.y + t.destino.y) / 2.0;
        double dx = t.destino.x - t.origen.x;
        double dy = t.destino.y - t.origen.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        double ctrlX = mx - 50 * (dy / dist);
        double ctrlY = my + 50 * (dx / dist);

        double angLlegada = Math.atan2(t.destino.y - ctrlY, t.destino.x - ctrlX);
        double finX = t.destino.x - Estado.RADIO * Math.cos(angLlegada);
        double finY = t.destino.y - Estado.RADIO * Math.sin(angLlegada);
        double angSalida = Math.atan2(ctrlY - t.origen.y, ctrlX - t.origen.x);
        double iniX = t.origen.x + Estado.RADIO * Math.cos(angSalida);
        double iniY = t.origen.y + Estado.RADIO * Math.sin(angSalida);

        QuadCurve2D q = new QuadCurve2D.Double(iniX, iniY, ctrlX, ctrlY, finX, finY);
        g2.draw(q);
        dibujarPunta(g2, finX, finY, angLlegada);
        dibujarEtiqueta(g2, t.getEtiqueta(), (int) ctrlX, (int) ctrlY);
    }

    private void dibujarRecta(Graphics2D g2, Transicion t) {
        double ang = Math.atan2(t.destino.y - t.origen.y, t.destino.x - t.origen.x);
        int x1 = t.origen.x + (int) (Estado.RADIO * Math.cos(ang));
        int y1 = t.origen.y + (int) (Estado.RADIO * Math.sin(ang));
        int x2 = t.destino.x - (int) (Estado.RADIO * Math.cos(ang));
        int y2 = t.destino.y - (int) (Estado.RADIO * Math.sin(ang));

        g2.drawLine(x1, y1, x2, y2);
        dibujarPunta(g2, x2, y2, ang);
        dibujarEtiqueta(g2, t.getEtiqueta(), (x1 + x2) / 2, (y1 + y2) / 2);
    }

    private void dibujarPunta(Graphics2D g2, double x, double y, double angRad) {
        int arrowSize = 15;
        int[] px = {(int) x, (int) (x - arrowSize * Math.cos(angRad - Math.PI / 6)), (int) (x - arrowSize * Math.cos(angRad + Math.PI / 6))};
        int[] py = {(int) y, (int) (y - arrowSize * Math.sin(angRad - Math.PI / 6)), (int) (y - arrowSize * Math.sin(angRad + Math.PI / 6))};
        g2.fillPolygon(px, py, 3);
    }

    private void dibujarEtiqueta(Graphics2D g2, String txt, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(txt);
        g2.setColor(Color.WHITE);
        g2.fillRect(x - w / 2 - 2, y - 10, w + 4, 14);
        g2.setColor(Color.BLUE);
        g2.drawString(txt, x - w / 2, y + 2);
    }
}