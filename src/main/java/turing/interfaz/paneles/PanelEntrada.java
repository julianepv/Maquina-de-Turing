package turing.interfaz.paneles;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Recoge la entrada; la validación corresponde al motor y al validador. */
public final class PanelEntrada extends JPanel {
    private static final String EJEMPLOS = "Ejemplos: ab · aabb · aaabbb";
    private final JTextField campoCadena = new JTextField(30);
    private final JButton botonCargar = new JButton("Cargar");
    private final JLabel ayuda = new JLabel(EJEMPLOS);

    public PanelEntrada() {
        super(new BorderLayout(0, 8));
        JLabel etiqueta = new JLabel("Cadena");
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.PLAIN, 14f));
        etiqueta.setLabelFor(campoCadena);
        add(etiqueta, BorderLayout.NORTH);

        campoCadena.setFont(campoCadena.getFont().deriveFont(Font.PLAIN, 16f));
        campoCadena.setMargin(new Insets(4, 8, 4, 8));
        campoCadena.setToolTipText("Escribe a y b. Pulsa Enter para cargar.");
        campoCadena.getAccessibleContext().setAccessibleName("Cadena de entrada");
        campoCadena.setMinimumSize(new Dimension(0, campoCadena.getPreferredSize().height));
        botonCargar.setFont(botonCargar.getFont().deriveFont(Font.PLAIN, 13f));
        botonCargar.setPreferredSize(new Dimension(
                Math.max(100, botonCargar.getPreferredSize().width),
                Math.max(34, botonCargar.getPreferredSize().height)));
        botonCargar.setToolTipText("Carga la cadena en la cinta e inicializa la máquina en q0.");
        botonCargar.setMnemonic('C');

        JPanel campoYCarga = new JPanel(new BorderLayout(12, 0));
        campoYCarga.add(campoCadena, BorderLayout.CENTER);
        campoYCarga.add(botonCargar, BorderLayout.EAST);
        add(campoYCarga, BorderLayout.CENTER);

        ayuda.setFont(ayuda.getFont().deriveFont(Font.PLAIN, 12f));
        ayuda.setMinimumSize(new Dimension(0, ayuda.getPreferredSize().height));
        ayuda.getAccessibleContext().setAccessibleName("Ayuda de entrada");
        add(ayuda, BorderLayout.SOUTH);
        campoCadena.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                limpiarError();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                limpiarError();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                limpiarError();
            }
        });
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
        ayuda.setToolTipText(mensaje);
        campoCadena.getAccessibleContext().setAccessibleDescription(mensaje);
        enfocarEntrada();
    }

    private void limpiarError() {
        ayuda.setText(EJEMPLOS);
        ayuda.setToolTipText(null);
        campoCadena.getAccessibleContext().setAccessibleDescription(null);
    }
}
