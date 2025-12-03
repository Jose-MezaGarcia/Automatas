import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PanelMT extends JPanel {
    private LienzoMT lienzo;
    private PanelCinta panelCinta;
    private JTable tablaTransiciones;
    private DefaultTableModel modeloTransiciones;
    private JTextField campoEntrada;
    private JTextArea areaLog;
    private JLabel lblEstadoActual;

    private List<Estado> estados = new ArrayList<>();
    private List<TransicionMT> transiciones = new ArrayList<>();
    private Estado estadoInicial = null;
    private Set<Estado> estadosFinales = new HashSet<>();

    private Map<Integer, String> cinta = new HashMap<>();
    private int cabezal = 0;
    private Estado estadoActualSim = null;
    private boolean simulando = false;

    private int modo = 0;
    private Estado estadoSeleccionado = null;

    public PanelMT() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        crearInterfazIzquierda(); crearInterfazDerecha(); crearPanelInferior();
        cargarEjemplo();
    }

    private void cargarEjemplo() {
        estados.clear(); transiciones.clear();
        Estado q0 = new Estado("q0", 150, 200); q0.esInicial = true;
        Estado q1 = new Estado("q1", 350, 200);
        Estado q2 = new Estado("q2", 550, 200); q2.esFinal = true;
        estados.add(q0); estados.add(q1); estados.add(q2);
        estadoInicial = q0; estadosFinales.add(q2);
        agregarTransicionLogica(q0, q0, "a", "b", "R");
        agregarTransicionLogica(q0, q0, "b", "b", "R");
        agregarTransicionLogica(q0, q1, "B", "B", "L");
        agregarTransicionLogica(q1, q2, "b", "b", "L");
        lienzo.repaint();
    }

    private void agregarTransicionLogica(Estado o, Estado d, String l, String e, String m) {
        transiciones.add(new TransicionMT(o, d, l, e, m));
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTransiciones.setRowCount(0);
        for(TransicionMT t : transiciones) {
            modeloTransiciones.addRow(new Object[]{t.origen.nombre, t.lee, t.escribe, t.mueve, t.destino.nombre});
        }
    }

    /* Crea los botones */
    private void crearInterfazIzquierda() {
        JPanel panelIzq = new JPanel(new BorderLayout(5, 5));
        JToolBar toolbar = new JToolBar(); toolbar.setFloatable(false);
        JButton btnEstado = new JButton("Ag. Estado"); btnEstado.setBackground(new Color(255, 193, 7));
        btnEstado.addActionListener(e -> { modo = 1; estadoSeleccionado = null; });
        JButton btnTrans = new JButton("Ag. Transición"); btnTrans.setBackground(new Color(33, 150, 243)); btnTrans.setForeground(Color.WHITE);
        btnTrans.addActionListener(e -> { modo = 2; estadoSeleccionado = null; });
        JButton btnMover = new JButton("Mover");
        btnMover.addActionListener(e -> { modo = 0; estadoSeleccionado = null; });
        JButton btnLimpiar = new JButton("Limpiar"); btnLimpiar.setBackground(new Color(244, 67, 54)); btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.addActionListener(e -> limpiar());

        toolbar.add(btnEstado); toolbar.add(btnTrans); toolbar.add(btnMover); toolbar.add(Box.createHorizontalGlue()); toolbar.add(btnLimpiar);
        lienzo = new LienzoMT(); lienzo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panelIzq.add(toolbar, BorderLayout.NORTH); panelIzq.add(lienzo, BorderLayout.CENTER);
        add(panelIzq, BorderLayout.CENTER);
    }

    private void crearInterfazDerecha() {
        JPanel panelDer = new JPanel(new BorderLayout(5, 5)); panelDer.setPreferredSize(new Dimension(300, 0));
        panelDer.setBorder(BorderFactory.createTitledBorder("Tabla de Transiciones (Clic Der para Borrar)"));
        String[] cols = {"Estado", "Lee", "Escribe", "Mueve", "Siguiente"};
        modeloTransiciones = new DefaultTableModel(cols, 0);
        tablaTransiciones = new JTable(modeloTransiciones);

        /* para borrar la fila */
        JPopupMenu pop = new JPopupMenu();
        JMenuItem delItem = new JMenuItem("Eliminar Transición");
        delItem.addActionListener(e -> {
            int row = tablaTransiciones.getSelectedRow();
            if(row >= 0) { transiciones.remove(row); actualizarTabla(); lienzo.repaint(); }
        });
        pop.add(delItem); tablaTransiciones.setComponentPopupMenu(pop);

        areaLog = new JTextArea(); areaLog.setEditable(false); areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaLog.setBorder(BorderFactory.createTitledBorder("Log de ejecución"));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tablaTransiciones), new JScrollPane(areaLog));
        split.setDividerLocation(300); panelDer.add(split, BorderLayout.CENTER);
        add(panelDer, BorderLayout.EAST);
    }

    private void crearPanelInferior() {
        JPanel panelInf = new JPanel(new BorderLayout(5, 5)); panelInf.setPreferredSize(new Dimension(0, 150));
        panelInf.setBorder(BorderFactory.createTitledBorder("Cinta ( _  = nada)"));
        panelCinta = new PanelCinta();
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campoEntrada = new JTextField("aaabbb", 15);
        JButton btnCargar = new JButton("▶ Cargar Cinta"); btnCargar.addActionListener(e -> cargarCinta());
        JButton btnPaso = new JButton("Paso "); btnPaso.setBackground(new Color(76, 175, 80)); btnPaso.setForeground(Color.WHITE);
        btnPaso.addActionListener(e -> darPaso());
        lblEstadoActual = new JLabel(" Estado: - "); lblEstadoActual.setFont(new Font("Arial", Font.BOLD, 14)); lblEstadoActual.setForeground(Color.BLUE);
        panelControles.add(new JLabel("Entrada:")); panelControles.add(campoEntrada); panelControles.add(btnCargar); panelControles.add(btnPaso); panelControles.add(lblEstadoActual);
        panelInf.add(panelCinta, BorderLayout.CENTER); panelInf.add(panelControles, BorderLayout.SOUTH);
        add(panelInf, BorderLayout.SOUTH);
    }

    private void limpiar() { estados.clear(); transiciones.clear(); actualizarTabla(); lienzo.repaint(); }

    private void cargarCinta() {
        if (estadoInicial == null) { JOptionPane.showMessageDialog(this, "Falta estado inicial"); return; }
        cinta.clear(); String input = campoEntrada.getText().trim();
        for (int i = 0; i < input.length(); i++) cinta.put(i, String.valueOf(input.charAt(i)));
        cabezal = 0; estadoActualSim = estadoInicial; simulando = true;
        areaLog.setText("Cinta cargada. Cabezal en 0.\n"); actualizarGUI();
    }

    private void darPaso() {
        if (!simulando || estadoActualSim == null) return;
        String simboloLeido = cinta.getOrDefault(cabezal, "_");
        TransicionMT t = null;
        for (TransicionMT tr : transiciones) { if (tr.origen == estadoActualSim && tr.lee.equals(simboloLeido)) { t = tr; break; } }

        if (t != null) {
            cinta.put(cabezal, t.escribe);
            if (t.mueve.equalsIgnoreCase("R")) cabezal++; else if (t.mueve.equalsIgnoreCase("L")) cabezal--;
            estadoActualSim = t.destino;
            areaLog.append("Lee " + t.lee + " -> Escribe " + t.escribe + ", Mueve " + t.mueve + "\n");
        } else {
            if (estadosFinales.contains(estadoActualSim)) {
                areaLog.append("PALABRA ACEPTADA\n"); JOptionPane.showMessageDialog(this, "¡Cadena Aceptada!");
            } else {
                areaLog.append("PALABRA RECHAZADA\n");
            }
            simulando = false;
        }
        actualizarGUI();
    }

    private void actualizarGUI() {
        lblEstadoActual.setText(" Estado: " + (estadoActualSim!=null ? estadoActualSim.nombre : "-") + " | Cabezal: " + cabezal);
        lienzo.repaint(); panelCinta.repaint();
    }

    class TransicionMT {
        Estado origen, destino; String lee, escribe, mueve;
        public TransicionMT(Estado o, Estado d, String l, String e, String m) { origen=o; destino=d; lee=l; escribe=e; mueve=m; }
        public String getEtiqueta() { return lee + "/" + escribe + "," + mueve; }
    }

    /*Muestra el panel de la cinta*/
    class PanelCinta extends JPanel {
        public PanelCinta() { setBackground(new Color(50, 50, 50)); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int ancho=50, alto=50, cx=getWidth()/2, y=(getHeight()-alto)/2;
            for (int i=-8; i<=8; i++) {
                int idx=cabezal+i; int x=cx+(i*ancho)-(ancho/2);
                g.setColor(i==0 ? new Color(255,193,7) : Color.WHITE);
                g.fillRect(x, y, ancho, alto); g.setColor(Color.BLACK); g.drawRect(x, y, ancho, alto);
                String val = cinta.getOrDefault(idx, "B");
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString(val, x+(ancho-g.getFontMetrics().stringWidth(val))/2, y+32);
            }
            g.setColor(new Color(255, 193, 7)); g.fillPolygon(new int[]{cx-10,cx+10,cx}, new int[]{y-10,y-10,y}, 3);
        }
    }

    class LienzoMT extends JPanel {
        public LienzoMT() {
            setBackground(Color.WHITE);
            MouseAdapter ma = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (modo==1) {
                        Estado n = new Estado("q" + estados.size(), e.getX(), e.getY());
                        if(estados.isEmpty()){n.esInicial=true; estadoInicial=n;}
                        estados.add(n); repaint();
                    } else if (modo==2) {
                        for(Estado s:estados) if(s.contiene(e.getX(),e.getY())){
                            if(estadoSeleccionado==null){estadoSeleccionado=s; s.resaltado=true;}
                            else{pedirTransicion(estadoSeleccionado,s); estadoSeleccionado.resaltado=false; estadoSeleccionado=null;}
                            repaint(); break;
                        }
                    } else {
                        for(Estado s:estados) if(s.contiene(e.getX(),e.getY())){
                            if(SwingUtilities.isRightMouseButton(e)) mostrarMenu(s,e.getX(),e.getY());
                            else estadoSeleccionado=s;
                        }
                    }
                }
                public void mouseDragged(MouseEvent e) { if(modo==0 && estadoSeleccionado!=null){estadoSeleccionado.x=e.getX(); estadoSeleccionado.y=e.getY(); repaint();} }
                public void mouseReleased(MouseEvent e) { if(modo==0) estadoSeleccionado=null; }
            };
            addMouseListener(ma); addMouseMotionListener(ma);
        }
        private void pedirTransicion(Estado ini, Estado fin) {
            JTextField t1=new JTextField("a"), t2=new JTextField("b"), t3=new JTextField("R");
            JPanel p=new JPanel(new GridLayout(3,2)); p.add(new JLabel("Lee:")); p.add(t1); p.add(new JLabel("Escribe:")); p.add(t2); p.add(new JLabel("Mete:")); p.add(t3);
            if(JOptionPane.showConfirmDialog(this,p,"Transición",JOptionPane.OK_CANCEL_OPTION)==0) { agregarTransicionLogica(ini, fin, t1.getText(), t2.getText(), t3.getText()); repaint(); }
        }

        /*Menu haciendo click derecho*/
        private void mostrarMenu(Estado s, int x, int y) {
            JPopupMenu m=new JPopupMenu();
            m.add(new JMenuItem("Es Inicial")).addActionListener(e->{estadoInicial=s; for(Estado st:estados)st.esInicial=(st==s); repaint();});
            m.add(new JMenuItem("Es Final")).addActionListener(e->{if(estadosFinales.contains(s))estadosFinales.remove(s); else estadosFinales.add(s); s.esFinal=!s.esFinal; repaint();});
            m.addSeparator();
            // BORRAR ESTADO
            JMenuItem itemDel = new JMenuItem("Eliminar Estado");
            itemDel.addActionListener(e -> {
                estados.remove(s);
                if(estadoInicial==s) estadoInicial=null;
                estadosFinales.remove(s);
                transiciones.removeIf(t -> t.origen==s || t.destino==s);
                actualizarTabla();
                repaint();
            });
            m.add(itemDel);
            m.show(this,x,y);
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for(TransicionMT t:transiciones) {
                g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(2));
                g2.drawLine(t.origen.x, t.origen.y, t.destino.x, t.destino.y);
                g2.setColor(Color.BLUE); g2.drawString(t.lee+"/"+t.escribe+","+t.mueve, (t.origen.x+t.destino.x)/2, (t.origen.y+t.destino.y)/2);
            }
            for(Estado s:estados) {
                g2.setColor(s==estadoActualSim?Color.GREEN:Color.YELLOW); g2.fillOval(s.x-30,s.y-30,60,60);
                g2.setColor(Color.BLACK); g2.drawOval(s.x-30,s.y-30,60,60);
                if(s.esFinal)g2.drawOval(s.x-25,s.y-25,50,50);
                if(s.esInicial)g2.drawString("Inicio",s.x-20,s.y-35);
                g2.drawString(s.nombre,s.x-5,s.y+5);
            }
        }
    }
}