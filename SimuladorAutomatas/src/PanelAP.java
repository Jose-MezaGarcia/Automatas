import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.QuadCurve2D;
import java.util.*;
import java.util.List;
import java.util.Stack;

public class PanelAP extends JPanel {
    private LienzoAP lienzo;
    private JTable tablaTransiciones;
    private DefaultTableModel modeloTransiciones;
    private JTable tablaPila;
    private DefaultTableModel modeloPila;
    private JTextField campoCadena;
    private JTextArea areaLog;
    private JLabel lblEstadoActual;

    private List<Estado> estados = new ArrayList<>();
    private List<TransicionAP> transiciones = new ArrayList<>();
    private Estado estadoInicial = null;
    private Set<Estado> estadosFinales = new HashSet<>();

    private Stack<String> pila = new Stack<>();
    private String cadenaRestante = "";
    private Estado estadoActualSim = null;

    private int modo = 0;
    private Estado estadoSeleccionado = null;

    public PanelAP() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        crearPanelIzquierdo(); crearPanelDerecho();
        cargarEjemplo();
    }

    private void cargarEjemplo() {
        estados.clear(); transiciones.clear();
        Estado q0 = new Estado("q0", 150, 250); q0.esInicial = true;
        Estado q1 = new Estado("q1", 450, 250); q1.esFinal = true;
        estados.add(q0); estados.add(q1);
        estadoInicial = q0; estadosFinales.add(q1);

        agregarTransicionLogica(q0, q0, "a", "e", "A");
        agregarTransicionLogica(q0, q0, "b", "A", "e");
        agregarTransicionLogica(q0, q1, "e", "Z0", "Z0");
        lienzo.repaint();
    }

    private void agregarTransicionLogica(Estado o, Estado d, String l, String s, String m) {
        transiciones.add(new TransicionAP(o, d, l, s, m));
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTransiciones.setRowCount(0);
        for(TransicionAP t : transiciones) {
            modeloTransiciones.addRow(new Object[]{t.origen.nombre, t.lee, t.saca, t.destino.nombre, t.mete});
        }
    }


    /*Crea los botones*/
    private void crearPanelIzquierdo() {
        JPanel panelIzq = new JPanel(new BorderLayout(5, 5));
        JToolBar toolbar = new JToolBar(); toolbar.setFloatable(false);
        JButton btnEstado = new JButton("Ag. Estado"); btnEstado.setBackground(new Color(255, 193, 7));
        btnEstado.addActionListener(e -> { modo = 1; estadoSeleccionado = null; });
        JButton btnTrans = new JButton("Ag. Transición"); btnTrans.setBackground(new Color(33, 150, 243)); btnTrans.setForeground(Color.WHITE);
        btnTrans.addActionListener(e -> { modo = 2; estadoSeleccionado = null; });
        JButton btnMover = new JButton("Mover");
        btnMover.addActionListener(e -> { modo = 0; estadoSeleccionado = null; });
        JButton btnBorrar = new JButton("Limpiar Todo"); btnBorrar.setBackground(new Color(244, 67, 54)); btnBorrar.setForeground(Color.WHITE);
        btnBorrar.addActionListener(e -> limpiar());

        toolbar.add(btnEstado); toolbar.add(btnTrans); toolbar.add(btnMover); toolbar.add(Box.createHorizontalGlue()); toolbar.add(btnBorrar);
        lienzo = new LienzoAP(); lienzo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPanel panelSim = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSim.setBorder(BorderFactory.createTitledBorder("Simulación"));
        campoCadena = new JTextField("aab", 10);
        JButton btnInit = new JButton("▶ Iniciar"); btnInit.addActionListener(e -> iniciarSimulacion());
        JButton btnStep = new JButton("Siguiente"); btnStep.setBackground(new Color(76, 175, 80)); btnStep.setForeground(Color.WHITE);
        btnStep.addActionListener(e -> darPaso());
        lblEstadoActual = new JLabel(" Estado: - "); lblEstadoActual.setFont(new Font("Arial", Font.BOLD, 14)); lblEstadoActual.setForeground(Color.BLUE);
        panelSim.add(new JLabel("Cadena:")); panelSim.add(campoCadena); panelSim.add(btnInit); panelSim.add(btnStep); panelSim.add(lblEstadoActual);
        panelIzq.add(toolbar, BorderLayout.NORTH); panelIzq.add(lienzo, BorderLayout.CENTER); panelIzq.add(panelSim, BorderLayout.SOUTH);
        add(panelIzq, BorderLayout.CENTER);
    }

    private void crearPanelDerecho() {
        JPanel panelDer = new JPanel(new GridLayout(2, 1, 5, 5)); panelDer.setPreferredSize(new Dimension(320, 0));

        // TABLA TRANSICIONES CON MENÚ DE BORRADO
        JPanel pTabla = new JPanel(new BorderLayout()); pTabla.setBorder(BorderFactory.createTitledBorder("Tabla de Transiciones (Clic Der para borrar)"));
        String[] cols = {"Estoy", "Leo", "Saco", "Voy", "Apilo"};
        modeloTransiciones = new DefaultTableModel(cols, 0);
        tablaTransiciones = new JTable(modeloTransiciones);

        // Menú popup para borrar filas
        JPopupMenu popupTabla = new JPopupMenu();
        JMenuItem itemBorrarFila = new JMenuItem("Eliminar Transición");
        itemBorrarFila.addActionListener(e -> {
            int row = tablaTransiciones.getSelectedRow();
            if(row >= 0) {
                transiciones.remove(row);
                actualizarTabla();
                lienzo.repaint();
            }
        });
        popupTabla.add(itemBorrarFila);
        tablaTransiciones.setComponentPopupMenu(popupTabla);

        pTabla.add(new JScrollPane(tablaTransiciones), BorderLayout.CENTER);

        JPanel pPila = new JPanel(new BorderLayout()); pPila.setBorder(BorderFactory.createTitledBorder("Pila (Tope arriba)"));
        modeloPila = new DefaultTableModel(new Object[]{"Símbolo"}, 0);
        tablaPila = new JTable(modeloPila); tablaPila.setEnabled(false); tablaPila.setFont(new Font("Monospaced", Font.BOLD, 16)); tablaPila.setRowHeight(25);
        ((DefaultTableCellRenderer)tablaPila.getDefaultRenderer(Object.class)).setHorizontalAlignment(JLabel.CENTER);
        areaLog = new JTextArea(); areaLog.setEditable(false); areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JSplitPane splitPila = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tablaPila), new JScrollPane(areaLog));
        splitPila.setDividerLocation(100); pPila.add(splitPila, BorderLayout.CENTER);
        panelDer.add(pTabla); panelDer.add(pPila); add(panelDer, BorderLayout.EAST);
    }

    private void limpiar() { estados.clear(); transiciones.clear(); actualizarTabla(); lienzo.repaint(); }

    private void iniciarSimulacion() {
        if (estadoInicial == null) { JOptionPane.showMessageDialog(this, "Falta estado inicial"); return; }
        cadenaRestante = campoCadena.getText().trim();
        estadoActualSim = estadoInicial;
        pila.clear(); pila.push("Z0");
        areaLog.setText("Simulación iniciada.\n"); actualizarGUI();
    }

    private boolean esEpsilon(String s) { return s.equals("e") || s.equals("eps") || s.equals("ε"); }

    private void darPaso() {
        if (estadoActualSim == null) return;
        String tope = pila.isEmpty() ? "" : pila.peek();
        String entrada = cadenaRestante.isEmpty() ? "e" : String.valueOf(cadenaRestante.charAt(0));

        TransicionAP t = null;
        for (TransicionAP tr : transiciones) {
            if (tr.origen == estadoActualSim) {
                boolean entradaOk = tr.lee.equals(entrada) || (esEpsilon(tr.lee) && !esEpsilon(entrada));
                boolean pilaOk = false;
                if (esEpsilon(tr.saca)) pilaOk = true;
                else if (!pila.isEmpty() && tr.saca.equals(tope)) pilaOk = true;

                if (entradaOk && pilaOk) {
                    if (!esEpsilon(tr.lee) && !esEpsilon(entrada)) { t = tr; break; }
                    if (t == null) t = tr;
                }
            }
        }

        if (t != null) {
            if (!esEpsilon(t.lee) && !cadenaRestante.isEmpty()) cadenaRestante = cadenaRestante.substring(1);
            if (!esEpsilon(t.saca) && !pila.isEmpty()) pila.pop();
            if (!esEpsilon(t.mete)) for (int i = t.mete.length() - 1; i >= 0; i--) pila.push(String.valueOf(t.mete.charAt(i)));
            estadoActualSim = t.destino;
            areaLog.append("Trans: (" + t.lee + ", " + t.saca + " -> " + t.mete + ")\n");

            /* Seleccionar en tabla */
            for(int i=0; i<transiciones.size(); i++) {
                if(transiciones.get(i) == t) {
                    tablaTransiciones.setRowSelectionInterval(i, i); break;
                }
            }
        } else {
            if (cadenaRestante.isEmpty() && estadosFinales.contains(estadoActualSim)) {
                areaLog.append("ACEPTADA\n"); JOptionPane.showMessageDialog(this, "¡Cadena Aceptada!");
            } else {
                areaLog.append("RECHAZADA\n");
            }
            estadoActualSim = null;
        }
        actualizarGUI();
    }

    private void actualizarGUI() {
        modeloPila.setRowCount(0); for (int i = pila.size() - 1; i >= 0; i--) modeloPila.addRow(new Object[]{pila.get(i)});
        lblEstadoActual.setText(" Estado: " + (estadoActualSim!=null ? estadoActualSim.nombre : "-") + " | Entrada: " + cadenaRestante);
        lienzo.repaint();
    }

    class TransicionAP {
        Estado origen, destino; String lee, saca, mete;
        public TransicionAP(Estado o, Estado d, String l, String s, String m) { origen=o; destino=d; lee=l; saca=s; mete=m; }
        public String getEtiqueta() { return lee + "," + saca + "/" + mete; }
    }

    class LienzoAP extends JPanel {
        public LienzoAP() {
            setBackground(Color.WHITE);
            MouseAdapter ma = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (modo == 1) { Estado n = new Estado("q" + estados.size(), e.getX(), e.getY()); if(estados.isEmpty()){n.esInicial=true; estadoInicial=n;} estados.add(n); repaint(); }
                    else if (modo == 2) { for(Estado s:estados) if(s.contiene(e.getX(),e.getY())){ if(estadoSeleccionado==null){estadoSeleccionado=s; s.resaltado=true;} else{pedirTransicion(estadoSeleccionado,s); estadoSeleccionado.resaltado=false; estadoSeleccionado=null;} repaint(); break; } }
                    else { for(Estado s:estados) if(s.contiene(e.getX(),e.getY())){ if(SwingUtilities.isRightMouseButton(e)) mostrarMenu(s,e.getX(),e.getY()); else estadoSeleccionado=s; } }
                }
                public void mouseDragged(MouseEvent e) { if(modo==0 && estadoSeleccionado!=null){estadoSeleccionado.x=e.getX(); estadoSeleccionado.y=e.getY(); repaint();} }
                public void mouseReleased(MouseEvent e) { if(modo==0) estadoSeleccionado=null; }
            };
            addMouseListener(ma); addMouseMotionListener(ma);
        }
        private void pedirTransicion(Estado ini, Estado fin) {
            JPanel p = new JPanel(new GridLayout(4, 2));
            JTextField t1=new JTextField("a"), t2=new JTextField("e"), t3=new JTextField("A");
            p.add(new JLabel("Leo (e=vacío):"));p.add(t1); p.add(new JLabel("Saco (e=nada):"));p.add(t2); p.add(new JLabel("Mete (e=nada):"));p.add(t3); p.add(new JLabel(" "));p.add(new JLabel(" "));
            if(JOptionPane.showConfirmDialog(this,p,"Transición",JOptionPane.OK_CANCEL_OPTION)==0) { agregarTransicionLogica(ini,fin,t1.getText(),t2.getText(),t3.getText()); repaint(); }
        }

        /* Menu estado con la accion de elimina*/
        private void mostrarMenu(Estado s, int x, int y) {
            JPopupMenu m=new JPopupMenu();
            m.add(new JMenuItem("Es Inicial")).addActionListener(e->{estadoInicial=s; for(Estado st:estados)st.esInicial=(st==s); repaint();});
            m.add(new JMenuItem("Es Final")).addActionListener(e->{if(estadosFinales.contains(s))estadosFinales.remove(s); else estadosFinales.add(s); s.esFinal=!s.esFinal; repaint();});
            m.addSeparator();
            // ¡AQUÍ ESTÁ EL BOTÓN DE BORRAR ESTADO!
            JMenuItem itemDel = new JMenuItem("Eliminar Estado");
            itemDel.addActionListener(e -> {
                estados.remove(s);
                if(estadoInicial == s) estadoInicial = null;
                estadosFinales.remove(s);
                // Borrar transiciones asociadas
                transiciones.removeIf(t -> t.origen == s || t.destino == s);
                actualizarTabla();
                repaint();
            });
            m.add(itemDel);

            m.show(this,x,y);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Map<String, List<TransicionAP>> grupos = new HashMap<>();
            for(TransicionAP t : transiciones) {
                String key = t.origen.nombre + "-" + t.destino.nombre;
                grupos.putIfAbsent(key, new ArrayList<>()); grupos.get(key).add(t);
            }
            for(String key : grupos.keySet()) {
                List<TransicionAP> lista = grupos.get(key);
                if(lista.isEmpty()) continue;
                Estado o = lista.get(0).origen; Estado d = lista.get(0).destino;
                g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(2));
                if(o==d) {
                    for(int i = 0; i < lista.size(); i++) {
                        TransicionAP t = lista.get(i);
                        int offset = i * 28; int ctrlY = o.y - 100 - offset; int textY = o.y - 60 - offset;
                        QuadCurve2D q = new QuadCurve2D.Double(o.x + Estado.RADIO*Math.cos(Math.toRadians(-135)), o.y + Estado.RADIO*Math.sin(Math.toRadians(-135)), o.x, ctrlY, o.x + Estado.RADIO*Math.cos(Math.toRadians(-45)), o.y + Estado.RADIO*Math.sin(Math.toRadians(-45)));
                        g2.draw(q); dibujarPunta(g2, q.getX2(), q.getY2(), Math.atan2(q.getY2()-ctrlY, q.getX2()-o.x));
                        dibujarEtiqueta(g2, t.getEtiqueta(), o.x, textY);
                    }
                } else {
                    g2.drawLine(o.x, o.y, d.x, d.y);
                    double ang = Math.atan2(d.y - o.y, d.x - o.x);
                    dibujarPunta(g2, d.x - Estado.RADIO*Math.cos(ang), d.y - Estado.RADIO*Math.sin(ang), ang);
                    int mx = (o.x+d.x)/2, my = (o.y+d.y)/2;
                    for(int i = 0; i < lista.size(); i++) { dibujarEtiqueta(g2, lista.get(i).getEtiqueta(), mx, my - 15 + (i*20)); }
                }
            }
            for(Estado s:estados) {
                g2.setColor(s==estadoActualSim?Color.GREEN:(s.resaltado?Color.CYAN:Color.YELLOW)); g2.fillOval(s.x-30,s.y-30,60,60);
                g2.setColor(Color.BLACK); g2.drawOval(s.x-30,s.y-30,60,60);
                if(s.esFinal)g2.drawOval(s.x-25,s.y-25,50,50);
                if(s.esInicial)g2.drawString("Inicio",s.x-20,s.y-35);
                g2.setFont(new Font("Arial", Font.BOLD, 14)); g2.drawString(s.nombre,s.x-g2.getFontMetrics().stringWidth(s.nombre)/2,s.y+5);
            }
        }
        private void dibujarPunta(Graphics2D g2, double x, double y, double ang) {
            int arrowSize=12; int[] px={(int)x, (int)(x-arrowSize*Math.cos(ang-Math.PI/6)), (int)(x-arrowSize*Math.cos(ang+Math.PI/6))};
            int[] py={(int)y, (int)(y-arrowSize*Math.sin(ang-Math.PI/6)), (int)(y-arrowSize*Math.sin(ang+Math.PI/6))}; g2.fillPolygon(px, py, 3);
        }
        private void dibujarEtiqueta(Graphics2D g2, String txt, int x, int y) {
            FontMetrics fm = g2.getFontMetrics(); int w = fm.stringWidth(txt);
            g2.setColor(Color.WHITE); g2.fillRect(x-w/2-2, y-10, w+4, 14); g2.setColor(Color.BLUE); g2.drawString(txt, x-w/2, y+2); g2.setColor(Color.BLACK);
        }
    }
}