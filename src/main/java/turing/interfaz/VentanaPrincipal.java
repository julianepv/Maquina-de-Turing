package turing.interfaz;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import turing.interfaz.paneles.PanelCinta;
import turing.interfaz.paneles.PanelControles;
import turing.interfaz.paneles.PanelEntrada;
import turing.interfaz.paneles.PanelEstado;
import turing.interfaz.paneles.PanelTransiciones;

/** Ventana principal con estilo Swing clásico y posicionamiento absoluto. */
public final class VentanaPrincipal extends JFrame {

    private final PanelEntrada panelEntrada = new PanelEntrada();
    private final PanelControles panelControles = new PanelControles();
    private final PanelCinta panelCinta = new PanelCinta();
    private final PanelEstado panelEstado = new PanelEstado();
    private final PanelTransiciones panelTransiciones = new PanelTransiciones();

    private JPanel panelEncabezado;
    private JPanel panelCuerpo;

    public VentanaPrincipal() {
        inicializarVentana();
        colocarPaneles();
        colocarTitulo();
        colocarComponentes();
        setLocationRelativeTo(null);
    }

    private void inicializarVentana() {
        setTitle("Máquina de Turing: Laboratorio #3");
        setSize(1030, 755);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void colocarPaneles() {
        panelEncabezado = crearPanel(
            10,
            5,
            1010,
            50,
            Color.WHITE,
            Color.BLACK,
            2
        );
        panelCuerpo = crearPanel(
            10,
            60,
            1010,
            650,
            Color.WHITE,
            Color.BLACK,
            2
        );
        add(panelEncabezado);
        add(panelCuerpo);
    }

    private JPanel crearPanel(
        int x,
        int y,
        int ancho,
        int alto,
        Color fondo,
        Color borde,
        int grosor
    ) {
        JPanel panel = new JPanel();
        panel.setBounds(x, y, ancho, alto);
        panel.setBackground(fondo);
        panel.setBorder(BorderFactory.createLineBorder(borde, grosor));
        panel.setLayout(null);
        return panel;
    }

    private void colocarTitulo() {
        JLabel titulo = new JLabel("Máquina de Turing", SwingConstants.CENTER);
        titulo.setBounds(25, 2, 955, 46);
        titulo.setForeground(Color.BLACK);
        titulo.setFont(new Font("Helvetica", Font.BOLD, 31));
        panelEncabezado.add(titulo);
    }

    private void colocarComponentes() {
        panelEntrada.setBounds(20, 16, 970, 60);
        panelCuerpo.add(panelEntrada);

        panelCinta.setBounds(20, 84, 970, 160);
        panelCuerpo.add(panelCinta);

        panelEstado.setBounds(20, 254, 970, 105);
        panelCuerpo.add(panelEstado);

        panelControles.setBounds(20, 372, 970, 42);
        panelCuerpo.add(panelControles);

        JLabel etiquetaTransiciones = new JLabel("Transiciones");
        etiquetaTransiciones.setBounds(20, 423, 200, 22);
        etiquetaTransiciones.setFont(new Font("Tahoma", Font.BOLD, 15));
        etiquetaTransiciones.setForeground(Color.BLACK);
        panelCuerpo.add(etiquetaTransiciones);

        panelTransiciones.setBounds(20, 448, 970, 178);
        panelCuerpo.add(panelTransiciones);
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

    public PanelTransiciones getPanelTransiciones() {
        return panelTransiciones;
    }

    public void mostrarError(String mensaje) {
        panelEntrada.mostrarError(mensaje);
    }

    public void alCerrar(Runnable accion) {
        addWindowListener(
            new WindowAdapter() {
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
            }
        );
    }
}
