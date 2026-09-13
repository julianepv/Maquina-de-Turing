package turing.interfaz.paneles;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

/** Expone acciones de ejecución y el intervalo de tiempo elegido por el usuario. */
public final class PanelControles extends JPanel {
    private final JButton botonPaso = new JButton("Paso");
    private final JButton botonEjecutar = new JButton("Ejecutar");
    private final JButton botonPausar = new JButton("Pausar");
    private final JButton botonReiniciar = new JButton("Reiniciar");
    private final JSlider velocidad = new JSlider(50, 1500, 500);
    private final JLabel etiquetaIntervalo = new JLabel();

    public PanelControles() {
        super(new BorderLayout(16, 0));
        setBorder(BorderFactory.createTitledBorder("Ejecución"));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        botones.add(botonPaso);
        botones.add(botonEjecutar);
        botones.add(botonPausar);
        botones.add(botonReiniciar);
        botonPaso.setToolTipText("Aplica exactamente una transición de la máquina.");
        botonEjecutar.setToolTipText("Continúa automáticamente hasta aceptar o rechazar.");
        botonPausar.setToolTipText("Detiene el temporizador y conserva el estado de la máquina.");
        botonReiniciar.setToolTipText("Vuelve a la entrada original y borra el historial.");
        add(botones, BorderLayout.WEST);

        JPanel panelVelocidad = new JPanel(new BorderLayout(0, 0));
        panelVelocidad.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        velocidad.setPreferredSize(new Dimension(270, 40));
        velocidad.getAccessibleContext().setAccessibleName("Intervalo en milisegundos entre pasos");
        Hashtable<Integer, JLabel> etiquetas = new Hashtable<>();
        etiquetas.put(50, new JLabel("Rápido"));
        etiquetas.put(1500, new JLabel("Lento"));
        velocidad.setLabelTable(etiquetas);
        velocidad.setPaintLabels(true);
        velocidad.addChangeListener(evento -> actualizarIntervalo());
        panelVelocidad.add(etiquetaIntervalo, BorderLayout.NORTH);
        panelVelocidad.add(velocidad, BorderLayout.CENTER);
        add(panelVelocidad, BorderLayout.CENTER);
        actualizarIntervalo();
        actualizarDisponibilidad(false, false, false);
    }

    private void actualizarIntervalo() {
        etiquetaIntervalo.setText("Intervalo: " + velocidad.getValue() + " ms por paso");
    }

    public void alPaso(ActionListener accion) {
        botonPaso.addActionListener(accion);
    }

    public void alEjecutar(ActionListener accion) {
        botonEjecutar.addActionListener(accion);
    }

    public void alPausar(ActionListener accion) {
        botonPausar.addActionListener(accion);
    }

    public void alReiniciar(ActionListener accion) {
        botonReiniciar.addActionListener(accion);
    }

    public void alCambiarVelocidad(ChangeListener accion) {
        velocidad.addChangeListener(accion);
    }

    public int getRetardoMilisegundos() {
        return velocidad.getValue();
    }

    public void actualizarDisponibilidad(boolean cargada, boolean ejecutando, boolean terminada) {
        boolean puedeAvanzar = cargada && !ejecutando && !terminada;
        botonPaso.setEnabled(puedeAvanzar);
        botonEjecutar.setEnabled(puedeAvanzar);
        botonPausar.setEnabled(cargada && ejecutando);
        botonReiniciar.setEnabled(cargada);
    }
}
