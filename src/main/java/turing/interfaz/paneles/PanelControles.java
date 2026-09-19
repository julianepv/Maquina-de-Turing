package turing.interfaz.paneles;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/** Botones de control de la simulación. */
public final class PanelControles extends JPanel {

    private static final int ANCHO_BOTON = 125;
    private static final int ALTO_BOTON = 40;

    private final JButton botonPaso = new JButton("Paso");
    private final JButton botonEjecutar = new JButton("Ejecutar");
    private final JButton botonPausar = new JButton("Pausar");
    private final JButton botonReiniciar = new JButton("Reiniciar");

    public PanelControles() {
        setLayout(null);
        setBackground(Color.WHITE);

        Font fuente = new Font("Tahoma", Font.BOLD, 14);
        JButton[] botones = {
            botonPaso,
            botonEjecutar,
            botonPausar,
            botonReiniciar,
        };
        int[] posicionesX = { 200, 350, 500, 650 };

        for (int i = 0; i < botones.length; i++) {
            JButton boton = botones[i];
            boton.setBounds(posicionesX[i], 0, ANCHO_BOTON, ALTO_BOTON);
            boton.setFont(fuente);
            boton.setForeground(Color.BLACK);
            boton.setFocusPainted(false);
            add(boton);
        }

        botonPaso.setToolTipText("Ejecuta una sola transición.");
        botonEjecutar.setToolTipText("Ejecuta la máquina automáticamente.");
        botonPausar.setToolTipText("Pausa la ejecución automática.");
        botonReiniciar.setToolTipText(
            "Reinicia la simulación con la cadena original."
        );

        botonPaso.setMnemonic('P');
        botonEjecutar.setMnemonic('E');
        botonPausar.setMnemonic('A');
        botonReiniciar.setMnemonic('R');

        actualizarDisponibilidad(false, false, false);
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

    public void actualizarDisponibilidad(
        boolean cargada,
        boolean ejecutando,
        boolean terminada
    ) {
        boolean puedeAvanzar = cargada && !ejecutando && !terminada;
        botonPaso.setEnabled(puedeAvanzar);
        botonEjecutar.setEnabled(puedeAvanzar);
        botonPausar.setEnabled(cargada && ejecutando);
        botonReiniciar.setEnabled(cargada);
    }
}
