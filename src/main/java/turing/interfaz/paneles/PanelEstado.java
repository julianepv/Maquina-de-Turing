package turing.interfaz.paneles;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import turing.modelo.maquina.Estado;

/** Presenta el estado actual y la última transición ejecutada. */
public final class PanelEstado extends JPanel {
    private final JPanel filaEstado = new JPanel(null);
    private final JLabel etiquetaEstado = new JLabel("Estado: —");
    private final JLabel etiquetaPasos = new JLabel("Paso: 0");
    private final JLabel etiquetaCabezal = new JLabel("Cabezal: —");
    private final JLabel etiquetaResultado = new JLabel("");

    private final JLabel tituloTransicion = new JLabel("Transición:");
    private final JLabel transicion = new JLabel("—");
    private final JLabel descripcion = new JLabel("Carga una cadena para comenzar.");

    public PanelEstado() {
        setLayout(null);
        setBackground(Color.WHITE);
        setBorder(new LineBorder(Color.BLUE, 1));

        filaEstado.setBounds(12, 8, 915, 26);
        filaEstado.setBackground(Color.WHITE);

        etiquetaEstado.setBounds(0, 0, 165, 25);
        etiquetaEstado.setFont(new Font("Tahoma", Font.BOLD, 14));
        filaEstado.add(etiquetaEstado);

        etiquetaPasos.setBounds(175, 0, 120, 25);
        etiquetaPasos.setFont(new Font("Tahoma", Font.BOLD, 14));
        filaEstado.add(etiquetaPasos);

        etiquetaCabezal.setBounds(305, 0, 145, 25);
        etiquetaCabezal.setFont(new Font("Tahoma", Font.BOLD, 14));
        filaEstado.add(etiquetaCabezal);

        etiquetaResultado.setBounds(470, 0, 430, 25);
        etiquetaResultado.setFont(new Font("Tahoma", Font.BOLD, 14));
        etiquetaResultado.getAccessibleContext().setAccessibleName("Resultado de la simulación");
        filaEstado.add(etiquetaResultado);
        add(filaEstado);

        tituloTransicion.setBounds(12, 40, 95, 28);
        tituloTransicion.setFont(new Font("Arial", Font.BOLD, 13));
        add(tituloTransicion);

        transicion.setBounds(105, 39, 535, 30);
        transicion.setFont(new Font("Monospaced", Font.BOLD, 14));
        transicion.setForeground(new Color(0, 45, 130));
        transicion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(165, 165, 165), 1),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        transicion.getAccessibleContext().setAccessibleName("Última transición ejecutada");
        add(transicion);

        descripcion.setBounds(12, 74, 900, 22);
        descripcion.setFont(new Font("Arial", Font.PLAIN, 13));
        descripcion.setForeground(Color.BLACK);
        descripcion.getAccessibleContext().setAccessibleName("Descripción del estado");
        add(descripcion);

        limpiar();
    }

    public JPanel getPanelResultado() {
        return filaEstado;
    }

    public void mostrar(Estado estado, long pasos, int posicion, String regla, String mensaje,
            String cadenaInicial) {
        etiquetaEstado.setText("Estado: " + estado);
        etiquetaPasos.setText("Paso: " + pasos);
        etiquetaCabezal.setText("Cabezal: " + posicion);

        if (pasos == 0 || regla == null || regla.isBlank()
                || regla.startsWith("Todavía no")) {
            transicion.setText("—");
            transicion.setToolTipText(null);
        } else if (regla.contains("indefinida")) {
            String corta = regla.replace(" indefinida → RECHAZAR (sin escritura ni movimiento)", " → RECHAZAR");
            corta = corta.replace(",", ", ");
            transicion.setText(corta);
            transicion.setToolTipText(regla);
        } else {
            String formateada = regla.replace(",", ", ").replace(" = ", " → ");
            transicion.setText(formateada);
            transicion.setToolTipText(formateada);
        }

        descripcion.setText(estado.getDescripcion());

        if (estado.esFinal()) {
            boolean aceptada = estado == Estado.ACEPTAR;
            etiquetaResultado.setText(aceptada ? "✓ Cadena aceptada" : "✕ Cadena rechazada");
            etiquetaResultado.setForeground(aceptada ? new Color(0, 130, 40) : new Color(190, 0, 0));
        } else {
            etiquetaResultado.setText("");
        }
    }

    public void limpiar() {
        etiquetaEstado.setText("Estado: —");
        etiquetaPasos.setText("Paso: 0");
        etiquetaCabezal.setText("Cabezal: —");
        transicion.setText("—");
        transicion.setToolTipText(null);
        descripcion.setText("Carga una cadena para comenzar.");
        etiquetaResultado.setText("");
    }
}
