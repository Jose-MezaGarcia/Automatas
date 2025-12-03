import javax.swing.*;

public class SimuladorAutomatas extends JFrame {
    private JTabbedPane pestanas;

    public SimuladorAutomatas() {
        setTitle("Simulador Automatas");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        pestanas = new JTabbedPane();

        // AFD
        pestanas.addTab("Autómata Finito", new PanelAFD());

        // Gramática Regular
        pestanas.addTab("Gramática Regular", new PanelGramatica());

        // GLC (Gramática Libre de Contexto)
        pestanas.addTab("Gramática Libre de Contexto", new PanelGLC());

        // Pila
        pestanas.addTab("Autómata de Pila", new PanelAP());

        // Turing
        pestanas.addTab("Máquina de Turing", new PanelMT());

        add(pestanas);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimuladorAutomatas().setVisible(true);
        });
    }
}