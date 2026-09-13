package turing.interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import turing.interfaz.paneles.PanelCinta;
import turing.interfaz.paneles.PanelControles;
import turing.interfaz.paneles.PanelEntrada;
import turing.interfaz.paneles.PanelEstado;
import turing.interfaz.paneles.PanelHistorial;
import turing.reglas.ConfiguracionAnBn;

/** Compone la vista Swing; el controlador conecta las acciones con la simulación. */
public final class VentanaPrincipal extends JFrame {
    private final PanelEntrada panelEntrada = new PanelEntrada();
    private final PanelControles panelControles = new PanelControles();
    private final PanelCinta panelCinta = new PanelCinta();
    private final PanelEstado panelEstado = new PanelEstado();
    private final PanelHistorial panelHistorial = new PanelHistorial();

    public VentanaPrincipal() {
        super("Laboratorio 3 · Máquina de Turing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JPanel encabezado = new JPanel(new BorderLayout(0, 6));
        JLabel titulo = new JLabel("Máquina de Turing · aⁿbⁿ");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 25f));
        titulo.setForeground(new Color(30, 58, 92));
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(new JLabel("Empareja cada a con una b mediante lectura, escritura y movimientos del cabezal."),
                BorderLayout.CENTER);
        encabezado.add(panelEntrada, BorderLayout.SOUTH);
        contenido.add(encabezado, BorderLayout.NORTH);

        JPanel simulacion = new JPanel();
        simulacion.setLayout(new BoxLayout(simulacion, BoxLayout.Y_AXIS));
        simulacion.add(panelCinta);
        simulacion.add(panelEstado);
        simulacion.add(panelControles);

        JTabbedPane registros = new JTabbedPane();
        registros.addTab("Historial de ejecución", panelHistorial);
        registros.addTab("Reglas de transición δ", crearPanelReglas());
        registros.setMinimumSize(new Dimension(200, 130));
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, simulacion, registros);
        division.setContinuousLayout(true);
        division.setResizeWeight(0.25);
        division.setBorder(BorderFactory.createEmptyBorder());
        contenido.add(division, BorderLayout.CENTER);
        setContentPane(contenido);

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int ancho = Math.min(1120, Math.max(640, pantalla.width - 80));
        int alto = Math.min(860, Math.max(600, pantalla.height - 80));
        setMinimumSize(new Dimension(Math.min(840, ancho), Math.min(700, alto)));
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        division.setDividerLocation(simulacion.getPreferredSize().height);
        SwingUtilities.invokeLater(() -> {
            int disponible = division.getHeight() - registros.getMinimumSize().height - division.getDividerSize();
            division.setDividerLocation(Math.min(simulacion.getPreferredSize().height,
                    Math.max(simulacion.getMinimumSize().height, disponible)));
        });
    }

    private JPanel crearPanelReglas() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[] {"Estado", "Lee", "Escribe", "Movimiento", "Nuevo estado", "Regla δ"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (var regla : ConfiguracionAnBn.crearTabla().getTransiciones()) {
            modelo.addRow(new Object[] {regla.estadoActual(), regla.simboloLeido(),
                    regla.simboloEscrito(), regla.direccion(), regla.estadoSiguiente(), regla.notacion()});
        }
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(26);
        tabla.setFillsViewportHeight(true);
        tabla.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        tabla.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(JLabel.CENTER);
        tabla.setDefaultRenderer(Object.class, centrado);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(280);
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setColumnHeaderView(tabla.getTableHeader());
        panel.add(desplazamiento, BorderLayout.CENTER);
        panel.add(new JLabel("δ(estado, leído) = (nuevo estado, escrito, dirección). Una regla ausente causa rechazo."),
                BorderLayout.SOUTH);
        return panel;
    }

    public PanelEntrada getPanelEntrada() {
        return panelEntrada;
    }

    public PanelControles getPanelControles() {
        return panelControles;
    }

    public PanelCinta getPanelCinta() {
        return panelCinta;
    }

    public PanelEstado getPanelEstado() {
        return panelEstado;
    }

    public PanelHistorial getPanelHistorial() {
        return panelHistorial;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Revisa la entrada", JOptionPane.WARNING_MESSAGE);
    }

    public void alCerrar(Runnable accion) {
        addWindowListener(new WindowAdapter() {
            private boolean notificado;

            @Override
            public void windowClosing(WindowEvent evento) {
                notificarCierre();
            }

            @Override
            public void windowClosed(WindowEvent evento) {
                notificarCierre();
            }

            private void notificarCierre() {
                if (!notificado) {
                    notificado = true;
                    accion.run();
                }
            }
        });
    }
}
