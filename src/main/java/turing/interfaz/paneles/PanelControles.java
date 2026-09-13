package turing.interfaz.paneles;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JButton[] botones = {botonPaso, botonEjecutar, botonPausar, botonReiniciar};
        int ancho = 100;
        int alto = 34;
        for (JButton boton : botones) {
            boton.setFont(boton.getFont().deriveFont(Font.PLAIN, 13f));
            ancho = Math.max(ancho, boton.getPreferredSize().width);
            alto = Math.max(alto, boton.getPreferredSize().height);
        }
        Dimension tamano = new Dimension(ancho, alto);
        for (int indice = 0; indice < botones.length; indice++) {
            if (indice > 0) {
                add(Box.createHorizontalStrut(indice == 3 ? 24 : 8));
            }
            JButton boton = botones[indice];
            boton.setPreferredSize(tamano);
            boton.setMinimumSize(tamano);
            boton.setMaximumSize(tamano);
            add(boton);
        }

        botonPaso.setToolTipText("Aplica exactamente una transición de la máquina.");
        botonEjecutar.setToolTipText("Continúa automáticamente hasta aceptar o rechazar.");
        botonPausar.setToolTipText("Pausa la ejecución sin perder el paso actual.");
        botonReiniciar.setToolTipText("Vuelve a la entrada original y borra el historial.");
        botonPaso.setMnemonic('P');
        botonEjecutar.setMnemonic('E');
        botonPausar.setMnemonic('A');
        botonReiniciar.setMnemonic('R');

        add(Box.createHorizontalStrut(24));
        add(Box.createHorizontalGlue());
        JLabel etiquetaVelocidad = new JLabel("Velocidad:");
        etiquetaVelocidad.setFont(etiquetaVelocidad.getFont().deriveFont(Font.PLAIN, 13f));
        etiquetaVelocidad.setLabelFor(velocidad);
        add(etiquetaVelocidad);
        add(Box.createHorizontalStrut(8));

        velocidad.getAccessibleContext().setAccessibleName("Intervalo en milisegundos entre pasos");
        velocidad.setOpaque(false);
        velocidad.setToolTipText("Tiempo entre pasos: 50 ms (más rápido) a 1500 ms (más lento).");
        Dimension tamanoVelocidad = new Dimension(140, velocidad.getPreferredSize().height);
        velocidad.setPreferredSize(tamanoVelocidad);
        velocidad.setMinimumSize(tamanoVelocidad);
        velocidad.setMaximumSize(tamanoVelocidad);
        velocidad.addChangeListener(evento -> actualizarIntervalo());
        add(velocidad);
        add(Box.createHorizontalStrut(8));

        etiquetaIntervalo.setFont(etiquetaIntervalo.getFont().deriveFont(Font.PLAIN, 12f));
        Dimension tamanoIntervalo = new Dimension(
                etiquetaIntervalo.getFontMetrics(etiquetaIntervalo.getFont()).stringWidth("1500 ms"),
                etiquetaIntervalo.getFontMetrics(etiquetaIntervalo.getFont()).getHeight());
        etiquetaIntervalo.setPreferredSize(tamanoIntervalo);
        etiquetaIntervalo.setMinimumSize(tamanoIntervalo);
        etiquetaIntervalo.setMaximumSize(tamanoIntervalo);
        add(etiquetaIntervalo);

        actualizarIntervalo();
        actualizarDisponibilidad(false, false, false);
    }

    private void actualizarIntervalo() {
        etiquetaIntervalo.setText(velocidad.getValue() + " ms");
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
