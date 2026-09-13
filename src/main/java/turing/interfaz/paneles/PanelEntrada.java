package turing.interfaz.paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Recoge la entrada; la validación corresponde al controlador y al validador. */
public final class PanelEntrada extends JPanel {
    private final JTextField campoCadena = new JTextField(28);
    private final JButton botonCargar = new JButton("Cargar cadena");

    public PanelEntrada() {
        super(new BorderLayout(12, 8));
        setBorder(BorderFactory.createTitledBorder("Entrada · L = {aⁿbⁿ | n ≥ 1}"));

        JLabel etiqueta = new JLabel("Cadena:");
        etiqueta.setLabelFor(campoCadena);
        campoCadena.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 19));
        campoCadena.setToolTipText("Escribe únicamente a y b. Pulsa Enter para cargar.");
        campoCadena.getAccessibleContext().setAccessibleName("Cadena de entrada");
        botonCargar.setMnemonic('C');

        JPanel formulario = new JPanel(new BorderLayout(10, 0));
        formulario.add(etiqueta, BorderLayout.WEST);
        formulario.add(campoCadena, BorderLayout.CENTER);
        formulario.add(botonCargar, BorderLayout.EAST);
        add(formulario, BorderLayout.CENTER);

        JLabel ayuda = new JLabel(
                "Ejemplos: ab, aabb, aaabbb, aaaabbbb. Solo se admiten a y b; n debe ser al menos 1.");
        ayuda.setForeground(new Color(75, 85, 99));
        add(ayuda, BorderLayout.SOUTH);
    }

    public String getCadena() {
        return campoCadena.getText();
    }

    public void setCadena(String cadena) {
        campoCadena.setText(cadena);
    }

    public void alCargar(ActionListener accion) {
        botonCargar.addActionListener(accion);
        campoCadena.addActionListener(evento -> {
            if (botonCargar.isEnabled()) {
                accion.actionPerformed(evento);
            }
        });
    }

    public void setEdicionHabilitada(boolean habilitada) {
        campoCadena.setEditable(habilitada);
        botonCargar.setEnabled(habilitada);
    }

    public void enfocarEntrada() {
        campoCadena.requestFocusInWindow();
        campoCadena.selectAll();
    }
}
