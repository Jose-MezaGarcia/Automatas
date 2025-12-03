import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.QuadCurve2D;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PanelGramatica extends JPanel {
    private JTextArea entradaGramatica;
    private JTextArea areaResultados;
    private LienzoInteractivo lienzo; // ¡Ahora es interactivo!
    private JTextField campoPrueba;

    // Informacion del autómata
    private List<Estado> estados = new ArrayList<>();
    private List<Transicion> transiciones = new ArrayList<>();
    private Estado estadoInicial = null;
    private Set<Estado> estadosFinales = new HashSet<>();

    public PanelGramatica() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /* izquierda: Configuración */
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setPreferredSize(new Dimension(340, 0));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("1. Definir Gramática Regular"));

        String ejemplo = "S -> aA | bS\nA -> aS | bB\nB -> bB | a";
        entradaGramatica = new JTextArea(ejemplo);
        entradaGramatica.setFont(new Font("Monospaced", Font.PLAIN, 18));
        panelIzquierdo.add(new JScrollPane(entradaGramatica), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 5, 5));
        JButton btnConvertir = new JButton("Generar Autómata");
        btnConvertir.setBackground(new Color(33, 150, 243));
        btnConvertir.setForeground(Color.WHITE);
        btnConvertir.setFont(new Font("Arial", Font.BOLD, 14));
        btnConvertir.addActionListener(e -> convertirGramatica());

        JLabel lblInfo = new JLabel("<html><b>Formato:</b> S -> aA | b<br>Soporta No-Determinismo.<br>Mayúsculas=Estados, Min=Símbolos</html>");
        lblInfo.setForeground(Color.BLACK);

        panelBotones.add(lblInfo);
        panelBotones.add(btnConvertir);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        /*centro: Lienzo Interactivo*/
        lienzo = new LienzoInteractivo();
        lienzo.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        /* abajo la Validación ---*/
        JPanel panelAbajo = new JPanel(new BorderLayout());
        panelAbajo.setBorder(BorderFactory.createTitledBorder("2. Validar cadena"));

        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campoPrueba = new JTextField(20);
        JButton btnVerificar = new JButton("Verificar");
        btnVerificar.setBackground(new Color(76, 175, 80));
        btnVerificar.setForeground(Color.WHITE);
        btnVerificar.addActionListener(e -> probarCadena());

        panelInput.add(new JLabel("Cadena:"));
        panelInput.add(campoPrueba);
        panelInput.add(btnVerificar);

        areaResultados = new JTextArea(3, 1);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.BOLD, 14));
        panelAbajo.add(panelInput, BorderLayout.NORTH);
        panelAbajo.add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        add(panelIzquierdo, BorderLayout.WEST);
        add(lienzo, BorderLayout.CENTER);
        add(panelAbajo, BorderLayout.SOUTH);
    }

    private void convertirGramatica() {
        estados.clear(); transiciones.clear(); estadosFinales.clear(); estadoInicial = null;
        Map<String, Estado> mapa = new HashMap<>();
        String[] lineas = entradaGramatica.getText().split("\n");

        try {
            Estado estAceptacion = new Estado("Z", 0, 0);
            estAceptacion.esFinal = true;

            // 1. Estados
            for (String l : lineas) {
                if (!l.contains("->")) continue;
                String n = l.split("->")[0].trim();
                mapa.putIfAbsent(n, new Estado(n, 0, 0));
            }

            // 2. Reglas
            Pattern p = Pattern.compile("^([a-z0-9])\\s*([A-Z0-9]+)?$");
            for (String l : lineas) {
                if (!l.contains("->")) continue;
                String[] parts = l.split("->");
                Estado origen = mapa.get(parts[0].trim());
                if (estadoInicial == null) { estadoInicial = origen; origen.esInicial = true; }

                for (String prod : parts[1].split("\\|")) {
                    prod = prod.trim();
                    if (prod.equals("eps")) { origen.esFinal = true; estadosFinales.add(origen); continue; }
                    Matcher m = p.matcher(prod);
                    if (m.find()) {
                        String sim = m.group(1);
                        String destStr = m.group(2);
                        Estado destino = (destStr == null) ? estAceptacion : mapa.computeIfAbsent(destStr, k->new Estado(k,0,0));
                        if(destStr == null) { mapa.putIfAbsent("Z", estAceptacion); estadosFinales.add(estAceptacion); }
                        transiciones.add(new Transicion(origen, destino, sim));
                    }
                }
            }

            // 3. Posicionar en círculo
            this.estados = new ArrayList<>(mapa.values());
            int cx = lienzo.getWidth()/2, cy = lienzo.getHeight()/2, r = 180;
            for(int i=0; i<estados.size(); i++) {
                double ang = 2 * Math.PI * i / estados.size() + Math.PI;
                estados.get(i).x = cx + (int)(r * Math.cos(ang));
                estados.get(i).y = cy + (int)(r * Math.sin(ang));
            }

            lienzo.repaint();
            areaResultados.setText("Autómata generado. ¡Puedes mover los estados!");

        } catch (Exception e) { areaResultados.setText("Error: " + e.getMessage()); }
    }

    private void probarCadena() {
        if(estadoInicial == null) return;

        // Limpiar resaltados anteriores
        for(Estado e : estados) e.resaltado = false;
        for(Transicion t : transiciones) t.resaltada = false;

        String input = campoPrueba.getText().trim();
        Set<Estado> actuales = new HashSet<>();
        actuales.add(estadoInicial);

        // Simulación
        for(char c : input.toCharArray()) {
            String s = String.valueOf(c);
            Set<Estado> siguientes = new HashSet<>();
            for(Estado act : actuales) {
                for(Transicion t : transiciones) {
                    if(t.origen == act && t.simbolos.contains(s)) {
                        siguientes.add(t.destino);
                        t.resaltada = true; // Resaltar camino usado (visual)
                    }
                }
            }
            actuales = siguientes;
            if(actuales.isEmpty()) break;
        }

        // Verificar aceptación
        boolean ok = false;
        List<String> finalesAlcanzados = new ArrayList<>();
        for(Estado e : actuales) {
            if(e.esFinal || estadosFinales.contains(e)) {
                ok = true;
                e.resaltado = true; // Pintar de verde el estado final alcanzado
                finalesAlcanzados.add(e.nombre);
            }
        }

        lienzo.repaint(); // dibujo con colores

        if(ok) {
            areaResultados.setText("ACEPTADA \nTerminó en: " + finalesAlcanzados);
            areaResultados.setBackground(new Color(200, 255, 200));
        } else {
            areaResultados.setText("RECHAZADA");
            areaResultados.setBackground(new Color(255, 200, 200));
        }
    }

    /* Claze interna, lienzo interactivo*/
    class LienzoInteractivo extends JPanel {
        private Estado estadoArrastrado = null;

        public LienzoInteractivo() {
            setBackground(Color.WHITE);
            // ¡ESTO ES LO QUE FALTABA! El MouseListener para mover
            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    for (Estado s : estados) {
                        if (s.contiene(e.getX(), e.getY())) {
                            estadoArrastrado = s;
                            break;
                        }
                    }
                }
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (estadoArrastrado != null) {
                        estadoArrastrado.x = e.getX();
                        estadoArrastrado.y = e.getY();
                        repaint(); // Redibujar mientras mueves
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    estadoArrastrado = null;
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (estados.isEmpty()) return;

            /*Crea las transiciones*/
            for(Transicion t : transiciones) {
                g2.setColor(t.resaltada ? new Color(0, 150, 0) : Color.BLACK);
                g2.setStroke(new BasicStroke(t.resaltada ? 3 : 2));

                boolean curva = false;
                for(Transicion otra : transiciones) {
                    if(otra.origen == t.destino && otra.destino == t.origen && t.origen != t.destino) {
                        curva = true; break;
                    }
                }

                if (t.origen == t.destino) {
                    int r = Estado.RADIO;
                    int cx = t.origen.x, cy = t.origen.y;
                    QuadCurve2D q = new QuadCurve2D.Double(
                            cx + r*Math.cos(Math.toRadians(-135)), cy + r*Math.sin(Math.toRadians(-135)),
                            cx, cy - r - 90,
                            cx + r*Math.cos(Math.toRadians(-45)), cy + r*Math.sin(Math.toRadians(-45))
                    );
                    g2.draw(q);
                    double ang = Math.atan2(q.getY2() - (cy - r - 90), q.getX2() - cx);
                    dibujarPunta(g2, q.getX2(), q.getY2(), ang);
                    dibujarEtiqueta(g2, t.getEtiqueta(), cx, cy - r - 60, t.resaltada);

                } else if (curva) {
                    double mx = (t.origen.x + t.destino.x)/2.0, my = (t.origen.y + t.destino.y)/2.0;
                    double dx = t.destino.x - t.origen.x, dy = t.destino.y - t.origen.y;
                    double dist = Math.sqrt(dx*dx + dy*dy);
                    double ctrlX = mx - 50 * (dy / dist), ctrlY = my + 50 * (dx / dist);

                    double angLlegada = Math.atan2(t.destino.y - ctrlY, t.destino.x - ctrlX);
                    double finX = t.destino.x - Estado.RADIO * Math.cos(angLlegada);
                    double finY = t.destino.y - Estado.RADIO * Math.sin(angLlegada);
                    double angSalida = Math.atan2(ctrlY - t.origen.y, ctrlX - t.origen.x);
                    double iniX = t.origen.x + Estado.RADIO * Math.cos(angSalida);
                    double iniY = t.origen.y + Estado.RADIO * Math.sin(angSalida);

                    QuadCurve2D q = new QuadCurve2D.Double(iniX, iniY, ctrlX, ctrlY, finX, finY);
                    g2.draw(q);
                    dibujarPunta(g2, finX, finY, angLlegada);

                    double tVal = 0.5;
                    double lblX = (1-tVal)*(1-tVal)*iniX + 2*(1-tVal)*tVal*ctrlX + tVal*tVal*finX;
                    double lblY = (1-tVal)*(1-tVal)*iniY + 2*(1-tVal)*tVal*ctrlY + tVal*tVal*finY;
                    dibujarEtiqueta(g2, t.getEtiqueta(), (int)lblX, (int)lblY, t.resaltada);

                } else { // RECTA
                    double ang = Math.atan2(t.destino.y - t.origen.y, t.destino.x - t.origen.x);
                    int x1 = t.origen.x + (int)(Estado.RADIO * Math.cos(ang));
                    int y1 = t.origen.y + (int)(Estado.RADIO * Math.sin(ang));
                    int x2 = t.destino.x - (int)(Estado.RADIO * Math.cos(ang));
                    int y2 = t.destino.y - (int)(Estado.RADIO * Math.sin(ang));

                    g2.drawLine(x1, y1, x2, y2);
                    dibujarPunta(g2, x2, y2, ang);
                    dibujarEtiqueta(g2, t.getEtiqueta(), (x1+x2)/2, (y1+y2)/2, t.resaltada);
                }
            }

            /*Dibuja los estados*/
            for(Estado s : estados) {
                // Color verde si es final alcanzado, amarillo si no
                g2.setColor(s.resaltado ? new Color(100, 255, 100) : Color.YELLOW);
                g2.fillOval(s.x-Estado.RADIO, s.y-Estado.RADIO, Estado.RADIO*2, Estado.RADIO*2);

                g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(2));
                g2.drawOval(s.x-Estado.RADIO, s.y-Estado.RADIO, Estado.RADIO*2, Estado.RADIO*2);

                if(s.esFinal) g2.drawOval(s.x-Estado.RADIO+5, s.y-Estado.RADIO+5, (Estado.RADIO-5)*2, (Estado.RADIO-5)*2);
                if(s.esInicial) {
                    int[] px = {s.x-Estado.RADIO-20, s.x-Estado.RADIO, s.x-Estado.RADIO-20};
                    int[] py = {s.y-10, s.y, s.y+10};
                    g2.fillPolygon(px, py, 3);
                }
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                String texto = s.nombre;
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(texto, s.x - fm.stringWidth(texto)/2, s.y + 5);
            }
        }

        private void dibujarPunta(Graphics2D g2, double x, double y, double angRad) {
            int arrowSize = 15;
            int[] px = {(int)x, (int)(x - arrowSize * Math.cos(angRad - Math.PI/6)), (int)(x - arrowSize * Math.cos(angRad + Math.PI/6))};
            int[] py = {(int)y, (int)(y - arrowSize * Math.sin(angRad - Math.PI/6)), (int)(y - arrowSize * Math.sin(angRad + Math.PI/6))};
            g2.fillPolygon(px, py, 3);
        }

        private void dibujarEtiqueta(Graphics2D g2, String txt, int x, int y, boolean resaltado) {
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(txt);
            g2.setColor(Color.WHITE); g2.fillRect(x - w/2 - 2, y - 10, w + 4, 14);
            g2.setColor(resaltado ? new Color(0, 150, 0) : Color.BLUE);
            g2.drawString(txt, x - w/2, y + 2);
        }
    }
}