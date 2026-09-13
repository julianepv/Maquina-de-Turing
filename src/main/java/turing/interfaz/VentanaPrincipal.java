package turing.interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.Scrollable;
import javax.swing.table.DefaultTableModel;
import turing.interfaz.componentes.TablaTransiciones;
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
        super("Máquina de Turing — aⁿbⁿ");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Deshabilita la opción de maximizar y redimensionar bordes
        setResizable(false);

        // Impide la minimización restaurando inmediatamente la ventana a su estado normal
        addWindowStateListener(evento -> {
            if ((evento.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                setExtendedState(Frame.NORMAL);
                toFront();
                requestFocus();
            }
            if ((evento.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                setExtendedState(Frame.NORMAL);
            }
        });

        JTabbedPane registros = new JTabbedPane();
        registros.addTab("Historial", panelHistorial);
        registros.addTab("Reglas", crearPanelReglas());
        registros.setFont(registros.getFont().deriveFont(Font.PLAIN, 13f));

        Contenido contenido = new Contenido();
        contenido.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JComponent[] secciones = {crearPanelCabecera(), panelEntrada, panelCinta, panelEstado,
                panelControles, registros, panelEstado.getPanelResultado()};
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.gridx = 0;
        posicion.weightx = 1;
        posicion.fill = GridBagConstraints.BOTH;
        for (int fila = 0; fila < secciones.length; fila++) {
            posicion.gridy = fila;
            posicion.weighty = fila == 2 ? 0.4 : fila == 5 ? 0.6 : 0;
            int separacion = fila == 0 ? 0 : fila == 1 || fila == 5 ? 24 : 16;
            posicion.insets = new Insets(separacion, 0, 0, 0);
            contenido.add(secciones[fila], posicion);
        }

        // En pantallas pequeñas se desplaza el contenido, en lugar de recortar controles.
        JScrollPane desplazamiento = new JScrollPane(contenido);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getVerticalScrollBar().setUnitIncrement(24);
        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        desplazamiento.setPreferredSize(new Dimension(Math.min(980, pantalla.width - 48),
                Math.min(contenido.getPreferredSize().height, pantalla.height - 96)));
        setContentPane(desplazamiento);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelCabecera() {
        JPanel encabezado = new JPanel(new BorderLayout(0, 8));
        JLabel titulo = new JLabel("Máquina de Turing — aⁿbⁿ");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 21f));
        encabezado.add(titulo, BorderLayout.NORTH);

        JLabel subtitulo = new JLabel(
                "Laboratorio 3 · Computabilidad y Complejidad de Algoritmos");
        subtitulo.setFont(subtitulo.getFont().deriveFont(Font.PLAIN, 12f));
        encabezado.add(subtitulo, BorderLayout.CENTER);

        return encabezado;
    }

    private JPanel crearPanelReglas() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[] {"Estado", "Lee", "Escribe", "Mueve", "Siguiente"}, 0);
        for (var regla : ConfiguracionAnBn.crearTabla().getTransiciones()) {
            modelo.addRow(new Object[] {regla.estadoActual(), regla.simboloLeido(),
                    regla.simboloEscrito(), regla.direccion(), regla.estadoSiguiente()});
        }
        JTable tabla = new TablaTransiciones(modelo, 140, 90, 100, 100, 160);
        tabla.getAccessibleContext().setAccessibleName("Reglas de la máquina");
        tabla.setToolTipText("L: izquierda · R: derecha");
        tabla.setPreferredScrollableViewportSize(new Dimension(760, tabla.getRowHeight() * 5));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        panel.add(desplazamiento, BorderLayout.CENTER);

        JLabel explicacion = new JLabel("L = {aⁿbⁿ | n ≥ 1}    ·    Una regla ausente causa rechazo.");
        explicacion.setFont(explicacion.getFont().deriveFont(Font.PLAIN, 12f));
        panel.add(explicacion, BorderLayout.SOUTH);
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
        panelEntrada.mostrarError(mensaje);
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

    private static final class Contenido extends JPanel implements Scrollable {
        private Contenido() {
            super(new GridBagLayout());
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visible, int orientacion, int direccion) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visible, int orientacion, int direccion) {
            return Math.max(24, (orientacion == javax.swing.SwingConstants.VERTICAL
                    ? visible.height : visible.width) - 24);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return getParent() != null && getParent().getWidth() >= getMinimumSize().width;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent() != null && getParent().getHeight() >= getPreferredSize().height;
        }
    }
}
