package turing.interfaz.paneles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Entrada de la cadena y del programa de la máquina a simular. */
public final class PanelEntrada extends JPanel {

    private static final String TEXTO_AYUDA =
        "Escribe la regla y pulsa Agregar transición. B representa el blanco de la cinta.";

    private final JLabel etiquetaCadena = new JLabel("Cadena:");
    private final JTextField campoCadena = new JTextField();
    private final JButton botonCargar = new JButton("Cargar");
    private final JLabel etiquetaInicial = new JLabel("Estado inicial:");
    private final JTextField campoInicial = new JTextField();
    private final JLabel etiquetaAceptacion = new JLabel("Estado final:");
    private final JTextField campoAceptacion = new JTextField();
    private final JLabel etiquetaAlfabeto = new JLabel("Alfabeto: {a, b, X, Y}");
    private final JLabel etiquetaCinta = new JLabel("Cinta: {a, b, X, Y, B}");
    private final JLabel etiquetaDelta = new JLabel("δ");
    private final JTextField campoDelta = new JTextField();
    private final JLabel etiquetaIgual = new JLabel("=");
    private final JTextField campoMovimiento = new JTextField();
    private final JButton botonAgregarTransicion = new JButton("Agregar transición");
    private final JButton botonCargarArchivo = new JButton("Cargar .txt");
    private final JLabel ayuda = new JLabel(TEXTO_AYUDA);
    private final List<String> transiciones = new ArrayList<>();

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

        etiquetaInicial.setBounds(0, 48, 105, 28);
        etiquetaInicial.setFont(new Font("Arial", Font.BOLD, 13));
        etiquetaInicial.setLabelFor(campoInicial);
        add(etiquetaInicial);

        campoInicial.setBounds(110, 48, 125, 28);
        campoInicial.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(campoInicial);

        etiquetaAceptacion.setBounds(260, 48, 100, 28);
        etiquetaAceptacion.setFont(new Font("Arial", Font.BOLD, 13));
        etiquetaAceptacion.setLabelFor(campoAceptacion);
        add(etiquetaAceptacion);

        campoAceptacion.setBounds(360, 48, 125, 28);
        campoAceptacion.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(campoAceptacion);

        etiquetaAlfabeto.setBounds(0, 76, 220, 24);
        etiquetaAlfabeto.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(etiquetaAlfabeto);

        etiquetaCinta.setBounds(280, 76, 250, 24);
        etiquetaCinta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(etiquetaCinta);

        etiquetaDelta.setBounds(0, 108, 22, 28);
        etiquetaDelta.setFont(new Font("Serif", Font.BOLD, 22));
        etiquetaDelta.setLabelFor(campoDelta);
        add(etiquetaDelta);

        campoDelta.setBounds(25, 106, 300, 32);
        campoDelta.setFont(new Font("Monospaced", Font.PLAIN, 14));
        campoDelta.setToolTipText("Ejemplo: (q0, a)");
        campoDelta.getAccessibleContext().setAccessibleName("Lado izquierdo de delta");
        add(campoDelta);

        etiquetaIgual.setBounds(337, 108, 20, 28);
        etiquetaIgual.setFont(new Font("Arial", Font.BOLD, 18));
        etiquetaIgual.setLabelFor(campoMovimiento);
        add(etiquetaIgual);

        campoMovimiento.setBounds(362, 106, 300, 32);
        campoMovimiento.setFont(new Font("Monospaced", Font.PLAIN, 14));
        campoMovimiento.setToolTipText("Ejemplo: (q1, X, R)");
        campoMovimiento.getAccessibleContext().setAccessibleName("Movimiento de la transición");
        add(campoMovimiento);

        botonAgregarTransicion.setBounds(680, 106, 160, 32);
        botonAgregarTransicion.setFont(new Font("Tahoma", Font.BOLD, 13));
        botonAgregarTransicion.setFocusPainted(false);
        botonAgregarTransicion.setToolTipText("Añade la transición a la tabla inferior.");
        add(botonAgregarTransicion);

        botonCargarArchivo.setBounds(850, 106, 105, 32);
        botonCargarArchivo.setFont(new Font("Tahoma", Font.BOLD, 12));
        botonCargarArchivo.setFocusPainted(false);
        botonCargarArchivo.setToolTipText("Importa transiciones desde un archivo .txt.");
        add(botonCargarArchivo);

        ayuda.setBounds(0, 146, 955, 18);
        ayuda.setFont(new Font("Arial", Font.BOLD, 12));
        ayuda.setForeground(Color.BLUE);
        ayuda.getAccessibleContext().setAccessibleName("Ayuda de entrada");
        add(ayuda);

        DocumentListener limpiarAlEditar = new DocumentListener() {
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
        };
        campoCadena.getDocument().addDocumentListener(limpiarAlEditar);
        campoInicial.getDocument().addDocumentListener(limpiarAlEditar);
        campoAceptacion.getDocument().addDocumentListener(limpiarAlEditar);
        campoDelta.getDocument().addDocumentListener(limpiarAlEditar);
        campoMovimiento.getDocument().addDocumentListener(limpiarAlEditar);
    }

    public String getCadena() {
        return campoCadena.getText();
    }

    public void setCadena(String cadena) {
        campoCadena.setText(cadena);
    }

    public String getEstadoInicial() {
        return campoInicial.getText();
    }

    public String getEstadosAceptacion() {
        return campoAceptacion.getText();
    }

    public String getTransiciones() {
        return String.join("\n", transiciones);
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

    public void alAgregarTransicion(ActionListener accion) {
        botonAgregarTransicion.addActionListener(evento -> {
            if (botonAgregarTransicion.isEnabled()) {
                limpiarError();
                accion.actionPerformed(evento);
            }
        });
    }

    public void alCargarArchivo(ActionListener accion) {
        botonCargarArchivo.addActionListener(evento -> {
            if (botonCargarArchivo.isEnabled()) {
                limpiarError();
                accion.actionPerformed(evento);
            }
        });
    }

    public String getDeltaActual() {
        return campoDelta.getText().trim();
    }

    public String getMovimientoActual() {
        return campoMovimiento.getText().trim();
    }

    /** Registra la regla solo después de que la tabla visual la haya validado. */
    public void confirmarTransicionActual() {
        String izquierda = getDeltaActual();
        String derecha = getMovimientoActual();
        if (izquierda.isBlank() || derecha.isBlank()) {
            throw new IllegalArgumentException("Completa las dos partes de la transición δ.");
        }
        transiciones.add(izquierda + " = " + derecha);
        campoDelta.setText("");
        campoMovimiento.setText("");
        campoDelta.requestFocusInWindow();
    }

    public void reemplazarTransiciones(List<String> nuevasTransiciones) {
        transiciones.clear();
        transiciones.addAll(nuevasTransiciones);
        campoDelta.setText("");
        campoMovimiento.setText("");
    }

    public void setEdicionHabilitada(boolean habilitada) {
        campoCadena.setEditable(habilitada);
        campoInicial.setEditable(habilitada);
        campoAceptacion.setEditable(habilitada);
        campoDelta.setEditable(habilitada);
        campoMovimiento.setEditable(habilitada);
        botonAgregarTransicion.setEnabled(habilitada);
        botonCargarArchivo.setEnabled(habilitada);
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
