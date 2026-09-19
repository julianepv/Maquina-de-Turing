package turing.interfaz.paneles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Entrada de la cadena a simular. */
public final class PanelEntrada extends JPanel {

    private static final String TEXTO_AYUDA =
        "Ingresa solamente cadenas tipo a y b";

    private final JLabel etiquetaCadena = new JLabel("Cadena:");
    private final JTextField campoCadena = new JTextField();
    private final JButton botonCargar = new JButton("Cargar");
    private final JLabel ayuda = new JLabel(TEXTO_AYUDA);

    public PanelEntrada() {
        setLayout(null);
        setBackground(Color.WHITE);

        etiquetaCadena.setBounds(0, 3, 85, 32);
        etiquetaCadena.setFont(new Font("Arial", Font.BOLD, 15));
        etiquetaCadena.setForeground(Color.BLACK);
        etiquetaCadena.setLabelFor(campoCadena);
        add(etiquetaCadena);

        campoCadena.setBounds(85, 0, 690, 36);
        campoCadena.setFont(new Font("Monospaced", Font.PLAIN, 16));
        campoCadena.setMargin(new Insets(3, 8, 3, 8));
        campoCadena.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        campoCadena.setToolTipText("Escribe una cadena formada por a y b");
        campoCadena
            .getAccessibleContext()
            .setAccessibleName("Cadena de entrada");
        add(campoCadena);

        botonCargar.setBounds(800, 0, 155, 36);
        botonCargar.setFont(new Font("Tahoma", Font.BOLD, 14));
        botonCargar.setFocusPainted(false);
        botonCargar.setMnemonic('C');
        botonCargar.setToolTipText("Carga la cadena en la cinta.");
        add(botonCargar);

        ayuda.setBounds(85, 40, 690, 18);
        ayuda.setFont(new Font("Arial", Font.BOLD, 12));
        ayuda.setForeground(Color.BLUE);
        ayuda.getAccessibleContext().setAccessibleName("Ayuda de entrada");
        add(ayuda);

        campoCadena.getDocument().addDocumentListener(
            new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    limpiarError();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    limpiarError();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    limpiarError();
                }
            }
        );
    }

    public String getCadena() {
        return campoCadena.getText();
    }

    public void setCadena(String cadena) {
        campoCadena.setText(cadena);
    }

    public void alCargar(ActionListener accion) {
        ActionListener cargar = evento -> {
            if (botonCargar.isEnabled()) {
                limpiarError();
                accion.actionPerformed(evento);
            }
        };
        botonCargar.addActionListener(cargar);
        campoCadena.addActionListener(cargar);
    }

    public void setEdicionHabilitada(boolean habilitada) {
        campoCadena.setEditable(habilitada);
        botonCargar.setEnabled(habilitada);
    }

    public void enfocarEntrada() {
        campoCadena.requestFocusInWindow();
        campoCadena.selectAll();
    }

    public void mostrarError(String mensaje) {
        ayuda.setText(mensaje);
        ayuda.setForeground(new Color(185, 0, 0));
        ayuda.setToolTipText(mensaje);
        campoCadena.getAccessibleContext().setAccessibleDescription(mensaje);
        enfocarEntrada();
    }

    private void limpiarError() {
        ayuda.setText(TEXTO_AYUDA);
        ayuda.setForeground(new Color(70, 70, 70));
        ayuda.setToolTipText(null);
        campoCadena.getAccessibleContext().setAccessibleDescription(null);
    }
}
