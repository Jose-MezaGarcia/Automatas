import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.QuadCurve2D; // IMPORTANTE: Necesario para las curvas suaves
import java.util.*;
import java.util.List;

public class PanelAFD extends JPanel {
    private JPanel panelIzquierdo, panelCentral, panelDerecho;
    private PanelLienzo lienzo;
    private JTextField campoAlfabeto, campoEstadoInicial, campoEstadosFinales, campoCadenaEntrada;
    private JTextArea areaResultados;
    private JTable tablaTransiciones;
    private DefaultTableModel modeloTabla;
    private JLabel etiquetaModo;

    /*Lista principales*/
    private List<Estado> estados = new ArrayList<>();
    private List<Transicion> transiciones = new ArrayList<>();
    private Estado estadoInicial = null;
    private Set<String> alfabeto = new HashSet<>();
    private Set<Estado> estadosFinales = new HashSet<>();

    /* Modos de interaccion */
    public enum Modo { SELECCIONAR, AGREGAR_ESTADO, AGREGAR_TRANSICION }
    private Modo modoActual = Modo.SELECCIONAR;
    private Estado inicioTransicion = null;

    public PanelAFD() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        crearPanelIzquierdo();
        crearPanelCentral();
        crearPanelDerecho();

        add(panelIzquierdo, BorderLayout.WEST);
        add(panelCentral, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);
    }

    private void crearPanelIzquierdo() {
        panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setPreferredSize(new Dimension(280, 0));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Configuración"));

        // Alfabeto
        panelIzquierdo.add(new JLabel("Alfabeto entrada:"));
        campoAlfabeto = new JTextField("a,b");
        panelIzquierdo.add(campoAlfabeto);
        panelIzquierdo.add(Box.createVerticalStrut(10));

        // Estado inicial
        panelIzquierdo.add(new JLabel("Estado inicial:"));
        campoEstadoInicial = new JTextField("q0");
        panelIzquierdo.add(campoEstadoInicial);
        panelIzquierdo.add(Box.createVerticalStrut(10));

        // Estados finales
        panelIzquierdo.add(new JLabel("Estados finales:"));
        campoEstadosFinales = new JTextField("q1,q2");
        panelIzquierdo.add(campoEstadosFinales);
        panelIzquierdo.add(Box.createVerticalStrut(20));

        // Etiqueta de modo
        panelIzquierdo.add(new JLabel("Modo de edición:"));
        etiquetaModo = new JLabel("● Seleccionar/Mover");
        etiquetaModo.setFont(new Font("Arial", Font.BOLD, 12));
        etiquetaModo.setForeground(new Color(33, 150, 243));
        panelIzquierdo.add(etiquetaModo);
        panelIzquierdo.add(Box.createVerticalStrut(10));

        // Botones
        JButton btnAgregarEstado = new JButton("Agregar Estado");
        btnAgregarEstado.setBackground(new Color(76, 175, 80));
        btnAgregarEstado.setForeground(Color.WHITE);
        btnAgregarEstado.addActionListener(e -> setModo(Modo.AGREGAR_ESTADO));
        panelIzquierdo.add(btnAgregarEstado);
        panelIzquierdo.add(Box.createVerticalStrut(5));

        JButton btnAgregarTrans = new JButton("Agregar Transición");
        btnAgregarTrans.setBackground(new Color(255, 152, 0));
        btnAgregarTrans.setForeground(Color.WHITE);
        btnAgregarTrans.addActionListener(e -> setModo(Modo.AGREGAR_TRANSICION));
        panelIzquierdo.add(btnAgregarTrans);
        panelIzquierdo.add(Box.createVerticalStrut(5));

        JButton btnSeleccionar = new JButton("Modo Seleccionar");
        btnSeleccionar.addActionListener(e -> setModo(Modo.SELECCIONAR));
        panelIzquierdo.add(btnSeleccionar);
        panelIzquierdo.add(Box.createVerticalStrut(20));

        JButton btnAplicar = new JButton("Aplicar Configuración");
        btnAplicar.setBackground(new Color(33, 150, 243));
        btnAplicar.setForeground(Color.WHITE);
        btnAplicar.addActionListener(e -> aplicarConfiguracion());
        panelIzquierdo.add(btnAplicar);
        panelIzquierdo.add(Box.createVerticalStrut(5));

        JButton btnLimpiar = new JButton("Limpiar Todo");
        btnLimpiar.addActionListener(e -> limpiarTodo());
        panelIzquierdo.add(btnLimpiar);

        panelIzquierdo.add(Box.createVerticalGlue());

        // Resultados
        panelIzquierdo.add(new JLabel("Historial y resultados:"));
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 10));
        JScrollPane scroll = new JScrollPane(areaResultados);
        scroll.setPreferredSize(new Dimension(260, 200));
        panelIzquierdo.add(scroll);
    }

    private void crearPanelCentral() {
        panelCentral = new JPanel(new BorderLayout(5, 5));

        lienzo = new PanelLienzo(this);
        lienzo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panelCentral.add(lienzo, BorderLayout.CENTER);

        JPanel panelPrueba = new JPanel(new BorderLayout(5, 5));
        panelPrueba.setBorder(BorderFactory.createTitledBorder("Probar cadena"));

        campoCadenaEntrada = new JTextField("ab");
        JButton btnProcesar = new JButton("Procesar");
        btnProcesar.setBackground(new Color(33, 150, 243));
        btnProcesar.setForeground(Color.WHITE);
        btnProcesar.addActionListener(e -> procesarCadena());

        panelPrueba.add(campoCadenaEntrada, BorderLayout.CENTER);
        panelPrueba.add(btnProcesar, BorderLayout.EAST);

        panelCentral.add(panelPrueba, BorderLayout.SOUTH);
    }

    private void crearPanelDerecho() {
        panelDerecho = new JPanel(new BorderLayout(5, 5));
        panelDerecho.setPreferredSize(new Dimension(300, 0));

        JPanel panelTrans = new JPanel(new BorderLayout());
        panelTrans.setBorder(BorderFactory.createTitledBorder("Tabla Transiciones"));

        String[] columnas = {"Origen", "Símbolo", "Destino"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaTransiciones = new JTable(modeloTabla);
        panelTrans.add(new JScrollPane(tablaTransiciones), BorderLayout.CENTER);

        JButton btnEliminar = new JButton("Eliminar seleccionada");
        btnEliminar.addActionListener(e -> eliminarTransicionSeleccionada());
        panelTrans.add(btnEliminar, BorderLayout.SOUTH);

        panelDerecho.add(panelTrans, BorderLayout.CENTER);
    }

    /* Logica Principal*/

    private void setModo(Modo nuevoModo) {
        this.modoActual = nuevoModo;
        this.inicioTransicion = null;
        lienzo.repaint();

        switch (modoActual) {
            case SELECCIONAR:
                etiquetaModo.setText("● Seleccionar/Mover");
                etiquetaModo.setForeground(new Color(33, 150, 243));
                lienzo.setCursor(Cursor.getDefaultCursor());
                break;
            case AGREGAR_ESTADO:
                etiquetaModo.setText("● Agregar Estado (clic)");
                etiquetaModo.setForeground(new Color(76, 175, 80));
                lienzo.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                break;
            case AGREGAR_TRANSICION:
                etiquetaModo.setText("● Transición (clic origen -> destino)");
                etiquetaModo.setForeground(new Color(255, 152, 0));
                lienzo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;
        }
    }

    public void agregarEstado(int x, int y) {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del estado:", "q" + estados.size());
        if (nombre != null && !nombre.trim().isEmpty()) {
            estados.add(new Estado(nombre.trim(), x, y));
            lienzo.repaint();
        }
    }

    public void manejarClicEstado(Estado e) {
        if (modoActual == Modo.AGREGAR_TRANSICION) {
            if (inicioTransicion == null) {
                inicioTransicion = e;
                areaResultados.setText("Origen: " + e.nombre + "\nSelecciona destino...\n");
                lienzo.repaint();
            } else {
                String input = JOptionPane.showInputDialog(this, "Símbolos (separados por coma):");
                if (input != null && !input.trim().isEmpty()) {
                    String[] simbolosArr = input.split(",");

                    Transicion existente = null;
                    for (Transicion t : transiciones) {
                        if (t.origen == inicioTransicion && t.destino == e) {
                            existente = t;
                            break;
                        }
                    }

                    for (String s : simbolosArr) {
                        String limpio = s.trim();
                        if (existente != null) {
                            existente.simbolos.add(limpio);
                        } else {
                            Transicion t = new Transicion(inicioTransicion, e, limpio);
                            transiciones.add(t);
                            existente = t;
                        }
                        modeloTabla.addRow(new Object[]{inicioTransicion.nombre, limpio, e.nombre});
                    }
                }
                inicioTransicion = null;
                lienzo.repaint();
            }
        }
    }

    private void procesarCadena() {
        if (estadoInicial == null) {
            JOptionPane.showMessageDialog(this, "Define un estado inicial primero.");
            return;
        }

        String cadena = campoCadenaEntrada.getText().trim();
        areaResultados.setText("Procesando: \"" + cadena + "\"\n-----------------\n");

        new Thread(() -> {
            try {
                Estado actual = estadoInicial;

                SwingUtilities.invokeLater(() -> lienzo.resaltarEstado(estadoInicial));
                Thread.sleep(1000);

                for (int i = 0; i < cadena.length(); i++) {
                    String simbolo = String.valueOf(cadena.charAt(i));
                    Estado siguiente = null;
                    Transicion transicionUsada = null;

                    for (Transicion t : transiciones) {
                        if (t.origen == actual && t.simbolos.contains(simbolo)) {
                            siguiente = t.destino;
                            transicionUsada = t;
                            break;
                        }
                    }

                    if (siguiente == null) {
                        SwingUtilities.invokeLater(() -> areaResultados.append("❌ Fallo en: " + simbolo + "\n"));
                        return;
                    }

                    Estado finalSiguiente = siguiente;
                    Transicion finalTrans = transicionUsada;
                    String finalSimbolo = simbolo;
                    Estado finalActual = actual;

                    SwingUtilities.invokeLater(() -> {
                        lienzo.resaltarTransicion(finalTrans);
                        lienzo.resaltarEstado(finalSiguiente);
                        areaResultados.append("(" + finalActual.nombre + ", " + finalSimbolo + ") -> " + finalSiguiente.nombre + "\n");
                    });

                    actual = siguiente;
                    Thread.sleep(1000);
                }

                Estado ultimo = actual;
                SwingUtilities.invokeLater(() -> {
                    lienzo.limpiarResaltado();
                    if (ultimo.esFinal || estadosFinales.contains(ultimo)) {
                        areaResultados.append("\n✅ ACEPTADA\n");
                        ultimo.resaltado = true;
                    } else {
                        areaResultados.append("\n❌ RECHAZADA (No es final)\n");
                        ultimo.resaltado = true;
                    }
                    lienzo.repaint();
                });

                Thread.sleep(2000);
                SwingUtilities.invokeLater(() -> lienzo.limpiarResaltado());

            } catch (InterruptedException ex) { ex.printStackTrace(); }
        }).start();
    }

    private void aplicarConfiguracion() {
        String init = campoEstadoInicial.getText().trim();
        estadoInicial = null;
        estadosFinales.clear();

        for (Estado e : estados) {
            e.esInicial = false;
            e.esFinal = false;
            if (e.nombre.equals(init)) {
                e.esInicial = true;
                estadoInicial = e;
            }
        }

        String[] finales = campoEstadosFinales.getText().split(",");
        for (String f : finales) {
            for (Estado e : estados) {
                if (e.nombre.equals(f.trim())) {
                    e.esFinal = true;
                    estadosFinales.add(e);
                }
            }
        }
        lienzo.repaint();
        areaResultados.setText("Configuración actualizada.");
    }

    private void eliminarTransicionSeleccionada() {
        int fila = tablaTransiciones.getSelectedRow();
        if (fila >= 0) {
            modeloTabla.removeRow(fila);
            /* Esto solo borra visualmente de la tabla, no del modelo lógico 'transiciones' */
        }
    }

    private void limpiarTodo() {
        estados.clear();
        transiciones.clear();
        modeloTabla.setRowCount(0);
        estadoInicial = null;
        estadosFinales.clear();
        lienzo.repaint();
    }

    public List<Estado> getEstados() { return estados; }
    public List<Transicion> getTransiciones() { return transiciones; }
    public Modo getModoActual() { return modoActual; }
    public Estado getInicioTransicion() { return inicioTransicion; }

    /* Clase interna parac dibujar (se hizo la mejora de las curvas)*/
    class PanelLienzo extends JPanel {
        private PanelAFD padre;
        private Estado estadoArrastrado = null;

        public PanelLienzo(PanelAFD padre) {
            this.padre = padre;
            setBackground(Color.WHITE);

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (padre.getModoActual() == PanelAFD.Modo.AGREGAR_ESTADO) {
                        padre.agregarEstado(e.getX(), e.getY());
                        return;
                    }
                    for (Estado s : padre.getEstados()) {
                        if (s.contiene(e.getX(), e.getY())) {
                            if (padre.getModoActual() == PanelAFD.Modo.AGREGAR_TRANSICION) {
                                padre.manejarClicEstado(s);
                            } else {
                                estadoArrastrado = s;
                            }
                            return;
                        }
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) { estadoArrastrado = null; }
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (estadoArrastrado != null && padre.getModoActual() == PanelAFD.Modo.SELECCIONAR) {
                        estadoArrastrado.x = e.getX();
                        estadoArrastrado.y = e.getY();
                        repaint();
                    }
                }
            };
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }

        public void resaltarEstado(Estado e) {
            limpiarResaltado();
            if(e != null) e.resaltado = true;
            repaint();
        }
        public void resaltarTransicion(Transicion t) {
            if(t != null) t.resaltada = true;
            repaint();
        }
        public void limpiarResaltado() {
            for(Estado e : padre.getEstados()) e.resaltado = false;
            for(Transicion t : padre.getTransiciones()) t.resaltada = false;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. DIBUJAR TRANSICIONES
            for (Transicion t : padre.getTransiciones()) {
                if (t.resaltada) {
                    g2.setColor(Color.GREEN);
                    g2.setStroke(new BasicStroke(3));
                } else {
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2));
                }

                // CASO A: AUTO-CICLO (Bucle Suave)
                if (t.origen == t.destino) {
                    int r = Estado.RADIO;
                    int cx = t.origen.x;
                    int cy = t.origen.y;

                    /* Puntos de inicio y fin en el borde del estado (ángulos de -150 y -30 grados)*/
                    double x1 = cx + r * Math.cos(Math.toRadians(-150));
                    double y1 = cy + r * Math.sin(Math.toRadians(-150));
                    double x2 = cx + r * Math.cos(Math.toRadians(-30));
                    double y2 = cy + r * Math.sin(Math.toRadians(-30));

                    /* Punto de control para jalar la curva hacia arriba */
                    double ctrlx = cx;
                    double ctrly = cy - r - 60;

                    // Dibujar la curva suave
                    QuadCurve2D q = new QuadCurve2D.Double(x1, y1, ctrlx, ctrly, x2, y2);
                    g2.draw(q);

                    /* Calcular ángulo para la punta de flecha  */
                    double angle = Math.toDegrees(Math.atan2(y2 - ctrly, x2 - ctrlx));
                    dibujarPuntaFlecha(g2, (int)x2, (int)y2, angle);

                    /* Etiqueta arriba del bucle */
                    g2.setColor(Color.BLUE);
                    g2.drawString(t.getEtiqueta(), (int)ctrlx - 10, (int)ctrly + 5);
                }
                /* segundo caso, flecha normal */
                else {
                    double angulo = Math.atan2(t.destino.y - t.origen.y, t.destino.x - t.origen.x);
                    int r = Estado.RADIO;
                    int x1 = t.origen.x + (int)(r * Math.cos(angulo));
                    int y1 = t.origen.y + (int)(r * Math.sin(angulo));
                    int x2 = t.destino.x - (int)(r * Math.cos(angulo));
                    int y2 = t.destino.y - (int)(r * Math.sin(angulo));

                    g2.drawLine(x1, y1, x2, y2);
                    dibujarPuntaFlecha(g2, x2, y2, Math.toDegrees(angulo));

                    /* Etiqueta centrada con fondo blanco */
                    int mx = (x1 + x2)/2;
                    int my = (y1 + y2)/2;
                    String label = t.getEtiqueta();
                    FontMetrics fm = g2.getFontMetrics();
                    int w = fm.stringWidth(label);

                    g2.setColor(Color.WHITE);
                    g2.fillRect(mx - w/2, my - 15, w, 15); // Fondo
                    g2.setColor(t.resaltada ? Color.GREEN : Color.BLUE);
                    g2.drawString(label, mx - w/2, my - 5);
                }
            }

            /* dibuja  estados */
            for (Estado e : padre.getEstados()) {
                g2.setColor(e.resaltado ? Color.GREEN : Color.YELLOW);
                g2.fillOval(e.x - Estado.RADIO, e.y - Estado.RADIO, Estado.RADIO*2, Estado.RADIO*2);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(e.x - Estado.RADIO, e.y - Estado.RADIO, Estado.RADIO*2, Estado.RADIO*2);


                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int w = fm.stringWidth(e.nombre);
                g2.drawString(e.nombre, e.x - w/2, e.y + 5);

                // Estado FINAL ( con el doble circulo)
                if(e.esFinal) {
                    g2.drawOval(e.x - Estado.RADIO + 5, e.y - Estado.RADIO + 5, (Estado.RADIO-5)*2, (Estado.RADIO-5)*2);
                }
                // Estado INICIAL (con el triangulo que hace que sea de inicio)
                if(e.esInicial) {
                    int[] px = {e.x - Estado.RADIO - 20, e.x - Estado.RADIO, e.x - Estado.RADIO - 20};
                    int[] py = {e.y - 10, e.y, e.y + 10};
                    g2.fillPolygon(px, py, 3);
                    g2.drawString("Inicio", e.x - Estado.RADIO - 35, e.y - 15);
                }
            }


            if (padre.getModoActual() == PanelAFD.Modo.AGREGAR_TRANSICION && padre.getInicioTransicion() != null) {
                Estado inicio = padre.getInicioTransicion();
                Point p = getMousePosition();
                if (p != null) {
                    g2.setColor(Color.GRAY);
                    Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                    g2.setStroke(dashed);
                    g2.drawLine(inicio.x, inicio.y, p.x, p.y);
                }
            }
        }

        private void dibujarPuntaFlecha(Graphics2D g2, int x, int y, double grados) {
            double angle = Math.toRadians(grados);
            int arrowSize = 10;
            int[] xPoints = {
                    x,
                    (int) (x - arrowSize * Math.cos(angle - Math.PI / 6)),
                    (int) (x - arrowSize * Math.cos(angle + Math.PI / 6))
            };
            int[] yPoints = {
                    y,
                    (int) (y - arrowSize * Math.sin(angle - Math.PI / 6)),
                    (int) (y - arrowSize * Math.sin(angle + Math.PI / 6))
            };
            g2.fillPolygon(xPoints, yPoints, 3);
        }
    }
}