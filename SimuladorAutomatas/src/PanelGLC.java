import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PanelGLC extends JPanel {
    private JTextArea areaGramatica;
    private JTextArea areaEstado; // Muestra la cadena que llevas armada
    private LienzoArbol lienzo;

    /* Reglas: "S" -> ["SS", "bSaa", "a"] */
    private Map<String, List<String>> reglas = new HashMap<>();
    private Nodo raizArbol = null;

    public PanelGLC() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /*  panel izquierdo Configuración */
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setPreferredSize(new Dimension(340, 0));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("2. Definir Gramática (GLC)"));

        /* Gramatica compleja */
        String ejemplo = "S -> SS | bSaa | abSa | aab | aba | baa | a";
        areaGramatica = new JTextArea(ejemplo);
        areaGramatica.setFont(new Font("Monospaced", Font.PLAIN, 18));
        panelIzquierdo.add(new JScrollPane(areaGramatica), BorderLayout.CENTER);

        /* Botonera */
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 5, 5));

        /* Botón Amarillo para Iniciar */
        JButton btnReiniciar = new JButton("1. Iniciar / Reiniciar Árbol ");
        btnReiniciar.setBackground(new Color(255, 193, 7)); // Amarillo
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 12));
        btnReiniciar.addActionListener(e -> iniciarArbolManual());

        /* Botón Naranja para Chomsky */
        JButton btnChomsky = new JButton("Ver en Chomsky (FNC) ");
        btnChomsky.setBackground(new Color(255, 152, 0)); // Naranja
        btnChomsky.setForeground(Color.WHITE);
        btnChomsky.setFont(new Font("Arial", Font.BOLD, 12));
        btnChomsky.addActionListener(e -> mostrarChomsky());

        JLabel lblInfo = new JLabel("<html><center><b>MODO INTERACTIVO:</b><br>Haz <b>Clic Derecho</b> en los nodos<br>amarillos para elegir la derivación.</center></html>");

        panelBotones.add(lblInfo);
        panelBotones.add(btnReiniciar);
        panelBotones.add(btnChomsky);

        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        /* panel del centro (Lienzo Interactivo) */
        lienzo = new LienzoArbol();
        lienzo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollLienzo = new JScrollPane(lienzo);
        scrollLienzo.getVerticalScrollBar().setUnitIncrement(16);

        /* panel de abajo*/
        JPanel panelAbajo = new JPanel(new BorderLayout(5, 5));
        panelAbajo.setBorder(BorderFactory.createTitledBorder("3. Cadena Resultante (Hojas del Árbol)"));

        areaEstado = new JTextArea(2, 1);
        areaEstado.setEditable(false);
        areaEstado.setFont(new Font("Monospaced", Font.BOLD, 16));
        areaEstado.setForeground(new Color(0, 100, 0)); // Verde oscuro
        areaEstado.setText("Presiona 'Iniciar' para comenzar.");

        panelAbajo.add(new JScrollPane(areaEstado), BorderLayout.CENTER);

        add(panelIzquierdo, BorderLayout.WEST);
        add(scrollLienzo, BorderLayout.CENTER);
        add(panelAbajo, BorderLayout.SOUTH);
    }

    /* Logica Manual */
    private void parsearGramatica() {
        reglas.clear();
        String texto = areaGramatica.getText();
        for (String linea : texto.split("\n")) {
            if (!linea.contains("->")) continue;
            String[] partes = linea.split("->");
            String variable = partes[0].trim();
            String[] producciones = partes[1].split("\\|");

            reglas.putIfAbsent(variable, new ArrayList<>());
            for (String p : producciones) reglas.get(variable).add(p.trim());
        }
    }

    private void iniciarArbolManual() {
        parsearGramatica();
        if (!reglas.containsKey("S")) {
            JOptionPane.showMessageDialog(this, "La gramática debe tener una regla inicial 'S'.");
            return;
        }

        /* Crear raíz S */
        raizArbol = new Nodo("S");
        raizArbol.esVariable = true;

        /* Posición inicial */
        raizArbol.x = lienzo.getWidth() / 2;
        raizArbol.y = 50;

        actualizarEstado();
        lienzo.repaint();
    }

    /* Recorre el árbol para leer la cadena formada por las hojas */
    private void actualizarEstado() {
        if (raizArbol == null) return;
        StringBuilder sb = new StringBuilder();
        obtenerHojas(raizArbol, sb);
        String resultado = sb.toString();
        if (resultado.isEmpty()) resultado = "(vacío)";
        areaEstado.setText(resultado);
    }

    private void obtenerHojas(Nodo n, StringBuilder sb) {
        if (n.hijos.isEmpty()) {
            /* Si es hoja terminal (no variable) o variable sin derivar */
            if (!n.esVariable && !n.valor.equals("eps")) sb.append(n.valor);
        } else {
            for (Nodo h : n.hijos) obtenerHojas(h, sb);
        }
    }

    private void mostrarChomsky() {
        parsearGramatica();
        if (reglas.isEmpty()) return;
        StringBuilder sb = new StringBuilder("=== FNC ===\n\n");
        int z = 1;
        for(String k : reglas.keySet()) {
            for(String p : reglas.get(k)) {
                if(p.length() > 2) sb.append(k).append(" -> ").append(p.charAt(0)).append("Z").append(z++).append("\n");
                else sb.append(k).append(" -> ").append(p).append("\n");
            }
        }
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(sb.toString())), "FNC", JOptionPane.INFORMATION_MESSAGE);
    }

    /* Clase interna */
    class Nodo {
        String valor;
        int x, y;
        boolean esVariable;
        List<Nodo> hijos = new ArrayList<>();

        public Nodo(String v) {
            this.valor = v;
            // Si es mayúscula, es Variable (se puede derivar)
            this.esVariable = v.matches("[A-Z]");
        }

        public boolean contiene(int px, int py) {
            return Math.pow(px - x, 2) + Math.pow(py - y, 2) <= 900; // Radio 30^2
        }
    }

    class LienzoArbol extends JPanel {
        private Nodo nodoArrastrado = null;
        private Nodo nodoHover = null; // Para saber sobre cuál está el mouse

        public LienzoArbol() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(1200, 800));

            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (raizArbol == null) return;

                    Nodo n = buscarNodo(raizArbol, e.getX(), e.getY());
                    if (n != null) {
                        // CLIC DERECHO: Derivar (Solo si es variable y no tiene hijos aún)
                        if (SwingUtilities.isRightMouseButton(e) && n.esVariable && n.hijos.isEmpty()) {
                            mostrarMenuDerivacion(n, e.getX(), e.getY());
                        }
                        // CLIC IZQUIERDO: Arrastrar
                        else if (SwingUtilities.isLeftMouseButton(e)) {
                            nodoArrastrado = n;
                        }
                    }
                }
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (nodoArrastrado != null) {
                        nodoArrastrado.x = e.getX();
                        nodoArrastrado.y = e.getY();
                        repaint();
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) { nodoArrastrado = null; }

                @Override
                public void mouseMoved(MouseEvent e) {
                    if (raizArbol != null) {
                        Nodo nuevoHover = buscarNodo(raizArbol, e.getX(), e.getY());
                        if (nuevoHover != nodoHover) {
                            nodoHover = nuevoHover;
                            repaint();
                        }
                    }
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        private Nodo buscarNodo(Nodo n, int x, int y) {
            if (n.contiene(x, y)) return n;
            for (Nodo h : n.hijos) {
                Nodo res = buscarNodo(h, x, y);
                if (res != null) return res;
            }
            return null;
        }

        private void mostrarMenuDerivacion(Nodo n, int x, int y) {
            JPopupMenu menu = new JPopupMenu("Derivar " + n.valor);
            if (reglas.containsKey(n.valor)) {
                for (String regla : reglas.get(n.valor)) {
                    // Crea una opción de menú por cada regla disponible
                    JMenuItem item = new JMenuItem(n.valor + " -> " + regla);
                    item.setFont(new Font("Arial", Font.BOLD, 14));
                    item.addActionListener(e -> aplicarRegla(n, regla));
                    menu.add(item);
                }
            } else {
                menu.add(new JMenuItem("Sin reglas definidas"));
            }
            menu.show(this, x, y);
        }

        private void aplicarRegla(Nodo padre, String regla) {
            padre.hijos.clear();

            // Caso epsilon
            if (regla.equals("eps") || regla.equals("ε")) {
                Nodo hijo = new Nodo("ε");
                hijo.esVariable = false;
                hijo.x = padre.x;
                hijo.y = padre.y + 80;
                padre.hijos.add(hijo);
            } else {
                /* Crea hijos por cada letra de la regla */
                // Esto es lo que querías: ÁRBOL LITERAL
                int cantidad = regla.length();
                int espacio = 60;
                int inicioX = padre.x - ((cantidad - 1) * espacio) / 2;

                for (int i = 0; i < cantidad; i++) {
                    String simbolo = String.valueOf(regla.charAt(i));
                    Nodo hijo = new Nodo(simbolo);

                    /*posicion inicial del padre*/
                    hijo.x = inicioX + (i * espacio);
                    hijo.y = padre.y + 80;

                    padre.hijos.add(hijo);
                }
            }
            actualizarEstado(); /* Actualiza el texto de abajo */
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(raizArbol!=null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                dibujarRecursivo(g2, raizArbol);
            }
        }

        private void dibujarRecursivo(Graphics2D g, Nodo n) {
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));

            /* las lineas de un nodo a otro*/
            for(Nodo h : n.hijos) {
                g.drawLine(n.x, n.y+30, h.x, h.y-30); // Conexión
                dibujarRecursivo(g, h);
            }

            /* Estados  */
            if (n.esVariable) {
                if (n.hijos.isEmpty()) g.setColor(new Color(255, 193, 7)); // Amarillo Fuerte (Pendiente)
                else g.setColor(new Color(255, 235, 59)); // Amarillo Claro (Listo)
            } else {
                g.setColor(new Color(144, 238, 144)); // Verde (Terminal)
            }

            /* Resaltar si el mouse está encima */
            if (n == nodoHover) g.setColor(Color.CYAN);

            g.fillOval(n.x-30, n.y-30, 60, 60);

            /* Borde */
            g.setColor(Color.BLACK);
            g.drawOval(n.x-30, n.y-30, 60, 60);

            /* Texto */
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(n.valor, n.x - fm.stringWidth(n.valor)/2, n.y+8);
        }
    }
}