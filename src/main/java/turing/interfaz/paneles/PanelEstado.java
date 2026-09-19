package turing.interfaz.paneles;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import turing.modelo.maquina.Estado;

/** Resumen visual de la configuración actual y del resultado de la simulación. */
public final class PanelEstado extends JPanel {
    private static final Color AZUL = new Color(0, 70, 145);
    private static final Color FONDO_TARJETA = new Color(245, 248, 252);
    private static final Color BORDE_TARJETA = new Color(190, 205, 222);
    private static final Color VERDE = new Color(0, 125, 55);
    private static final Color FONDO_ACEPTADA = new Color(230, 248, 235);
    private static final Color ROJO = new Color(185, 0, 0);
    private static final Color FONDO_RECHAZADA = new Color(255, 235, 235);

    private final JPanel tarjetaEstado = crearTarjeta();
    private final JLabel valorEstado = crearValor();
    private final JPanel tarjetaPasos = crearTarjeta();
    private final JLabel valorPasos = crearValor();
    private final JPanel tarjetaCabezal = crearTarjeta();
    private final JLabel valorCabezal = crearValor();

    private final JPanel panelContexto = crearTarjeta();
    private final JLabel valorContexto = new JLabel();
    private final JPanel panelResultado = new JPanel(null);
    private final JLabel iconoResultado = new JLabel();
    private final JLabel etiquetaResultado = new JLabel();
    private final JLabel detalleResultado = new JLabel();

    private final JLabel tituloTransicion = new JLabel("Última transición");
    private final JLabel transicion = new JLabel("Sin transición ejecutada");

    public PanelEstado() {
        setLayout(null);
        setBackground(Color.WHITE);
        setBorder(new LineBorder(AZUL, 1));
        getAccessibleContext().setAccessibleName("Resumen de la ejecución");

        configurarTarjeta(tarjetaEstado, "ESTADO ACTUAL", valorEstado);
        tarjetaEstado.setBounds(12, 8, 220, 54);
        add(tarjetaEstado);

        configurarTarjeta(tarjetaPasos, "PASO", valorPasos);
        tarjetaPasos.setBounds(242, 8, 135, 54);
        add(tarjetaPasos);

        configurarTarjeta(tarjetaCabezal, "CABEZAL", valorCabezal);
        tarjetaCabezal.setBounds(387, 8, 145, 54);
        add(tarjetaCabezal);

        configurarContexto();
        panelContexto.setBounds(542, 8, 416, 54);
        add(panelContexto);

        configurarResultado();
        panelResultado.setBounds(542, 8, 416, 54);
        add(panelResultado);

        tituloTransicion.setBounds(14, 72, 128, 26);
        tituloTransicion.setFont(new Font("Arial", Font.BOLD, 12));
        tituloTransicion.setForeground(new Color(60, 60, 60));
        add(tituloTransicion);

        transicion.setBounds(145, 70, 813, 29);
        transicion.setFont(new Font("Monospaced", Font.BOLD, 13));
        transicion.setForeground(AZUL);
        transicion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_TARJETA, 1),
                BorderFactory.createEmptyBorder(0, 9, 0, 9)));
        transicion.getAccessibleContext().setAccessibleName("Última transición ejecutada");
        add(transicion);

        limpiar();
    }

    public JPanel getPanelResultado() {
        return panelResultado;
    }

    public void mostrar(Estado estado, long pasos, int posicion, String regla, String mensaje) {
        valorEstado.setText(estado.toString());
        valorEstado.setForeground(colorEstado(estado));
        valorPasos.setText(String.valueOf(pasos));
        valorCabezal.setText("celda " + posicion);
        actualizarTransicion(regla, pasos);

        if (estado.esFinal()) {
            mostrarResultado(estado);
        } else {
            panelResultado.setVisible(false);
            panelContexto.setVisible(true);
            valorContexto.setText(mensaje);
            valorContexto.setToolTipText(mensaje);
        }
    }

    /** Conserva la firma anterior para las integraciones que aún entregan la cadena inicial. */
    public void mostrar(Estado estado, long pasos, int posicion, String regla, String mensaje,
            String cadenaInicial) {
        mostrar(estado, pasos, posicion, regla, mensaje);
    }

    public void limpiar() {
        valorEstado.setText("—");
        valorEstado.setForeground(AZUL);
        valorPasos.setText("0");
        valorCabezal.setText("—");
        transicion.setText("Sin transición ejecutada");
        transicion.setToolTipText(null);
        panelResultado.setVisible(false);
        panelContexto.setVisible(true);
        valorContexto.setText("Carga una cadena para iniciar la simulación.");
        valorContexto.setToolTipText(null);
    }

    private JPanel crearTarjeta() {
        JPanel tarjeta = new JPanel(null);
        tarjeta.setBackground(FONDO_TARJETA);
        tarjeta.setBorder(BorderFactory.createLineBorder(BORDE_TARJETA, 1));
        return tarjeta;
    }

    private JLabel crearValor() {
        JLabel valor = new JLabel();
        valor.setFont(new Font("Tahoma", Font.BOLD, 20));
        valor.setForeground(AZUL);
        return valor;
    }

    private void configurarTarjeta(JPanel tarjeta, String titulo, JLabel valor) {
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setBounds(11, 5, 195, 15);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 10));
        etiqueta.setForeground(new Color(85, 95, 110));
        tarjeta.add(etiqueta);

        valor.setBounds(11, 20, 195, 29);
        tarjeta.add(valor);
    }

    private void configurarContexto() {
        JLabel titulo = new JLabel("SITUACIÓN");
        titulo.setBounds(11, 5, 390, 15);
        titulo.setFont(new Font("Arial", Font.BOLD, 10));
        titulo.setForeground(new Color(85, 95, 110));
        panelContexto.add(titulo);

        valorContexto.setBounds(11, 21, 390, 26);
        valorContexto.setFont(new Font("Arial", Font.PLAIN, 12));
        valorContexto.setForeground(new Color(45, 45, 45));
        valorContexto.getAccessibleContext().setAccessibleName("Situación de la simulación");
        panelContexto.add(valorContexto);
    }

    private void configurarResultado() {
        panelResultado.setBorder(new LineBorder(VERDE, 1));
        panelResultado.getAccessibleContext().setAccessibleName("Resultado de la simulación");

        iconoResultado.setBounds(12, 8, 30, 35);
        iconoResultado.setFont(new Font("Arial", Font.BOLD, 25));
        panelResultado.add(iconoResultado);

        etiquetaResultado.setBounds(51, 6, 350, 20);
        etiquetaResultado.setFont(new Font("Arial", Font.BOLD, 15));
        panelResultado.add(etiquetaResultado);

        detalleResultado.setBounds(51, 27, 350, 20);
        detalleResultado.setFont(new Font("Arial", Font.PLAIN, 11));
        panelResultado.add(detalleResultado);
    }

    private void actualizarTransicion(String regla, long pasos) {
        if (pasos == 0 || regla == null || regla.isBlank() || regla.startsWith("Todavía no")) {
            transicion.setText("Sin transición ejecutada");
            transicion.setToolTipText(null);
            return;
        }
        if (regla.contains("indefinida")) {
            String corta = regla.replace(" indefinida → RECHAZAR (sin escritura ni movimiento)",
                    " → RECHAZAR");
            transicion.setText(corta.replace(",", ", "));
            transicion.setToolTipText(regla);
            return;
        }
        String formateada = regla.replace(",", ", ").replace(" = ", " → ");
        transicion.setText(formateada);
        transicion.setToolTipText(formateada);
    }

    private void mostrarResultado(Estado estado) {
        boolean aceptada = estado.esAceptar();
        Color color = aceptada ? VERDE : ROJO;
        panelContexto.setVisible(false);
        panelResultado.setVisible(true);
        panelResultado.setBackground(aceptada ? FONDO_ACEPTADA : FONDO_RECHAZADA);
        panelResultado.setBorder(new LineBorder(color, 1));
        iconoResultado.setText(aceptada ? "✓" : "✕");
        iconoResultado.setForeground(color);
        etiquetaResultado.setText(aceptada ? "CADENA ACEPTADA" : "CADENA RECHAZADA");
        etiquetaResultado.setForeground(color);
        detalleResultado.setText("Estado final: " + estado);
        detalleResultado.setForeground(new Color(55, 55, 55));
    }

    private Color colorEstado(Estado estado) {
        if (estado.esAceptar()) {
            return VERDE;
        }
        return estado.esFinal() ? ROJO : AZUL;
    }
}
