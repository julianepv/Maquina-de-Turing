package turing.interfaz.paneles;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import turing.modelo.maquina.Estado;

/** Presenta la configuración actual y la última regla aplicada, sin ejecutar lógica. */
public final class PanelEstado extends JPanel {
    private final JLabel etiquetaEstado = new JLabel();
    private final JLabel etiquetaPasos = new JLabel();
    private final JLabel etiquetaCabezal = new JLabel();
    private final JLabel descripcion = new JLabel();
    private final JLabel transicion = new JLabel();
    private final JLabel resultado = new JLabel();
    private final JLabel detalleResultado = new JLabel(" ");
    private final JPanel panelResultado = new JPanel(new BorderLayout(0, 4));

    public PanelEstado() {
        super(new BorderLayout(0, 16));
        JPanel datos = new JPanel();
        datos.setLayout(new BoxLayout(datos, BoxLayout.X_AXIS));
        etiquetaEstado.setFont(etiquetaEstado.getFont().deriveFont(Font.BOLD, 14f));
        etiquetaPasos.setFont(etiquetaPasos.getFont().deriveFont(Font.PLAIN, 14f));
        etiquetaCabezal.setFont(etiquetaCabezal.getFont().deriveFont(Font.PLAIN, 14f));
        datos.add(etiquetaEstado);
        datos.add(Box.createHorizontalStrut(24));
        datos.add(etiquetaPasos);
        datos.add(Box.createHorizontalStrut(24));
        datos.add(etiquetaCabezal);
        datos.add(Box.createHorizontalGlue());
        add(datos, BorderLayout.NORTH);

        JPanel regla = new JPanel();
        regla.setLayout(new BoxLayout(regla, BoxLayout.Y_AXIS));
        JLabel etiquetaTransicion = new JLabel("Transición actual");
        etiquetaTransicion.setFont(etiquetaTransicion.getFont().deriveFont(Font.PLAIN, 12f));
        transicion.setFont(transicion.getFont().deriveFont(Font.PLAIN, 17f));
        transicion.getAccessibleContext().setAccessibleName("Última transición ejecutada");
        descripcion.setFont(descripcion.getFont().deriveFont(Font.PLAIN, 13f));
        descripcion.getAccessibleContext().setAccessibleName("Descripción del estado");
        for (JLabel etiqueta : new JLabel[] {etiquetaTransicion, transicion, descripcion}) {
            etiqueta.setAlignmentX(LEFT_ALIGNMENT);
            int alto = etiqueta.getFontMetrics(etiqueta.getFont()).getHeight();
            etiqueta.setMinimumSize(new Dimension(0, alto));
            etiqueta.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
        }
        regla.add(etiquetaTransicion);
        regla.add(Box.createVerticalStrut(4));
        regla.add(transicion);
        regla.add(Box.createVerticalStrut(8));
        regla.add(descripcion);
        add(regla, BorderLayout.CENTER);

        resultado.setFont(resultado.getFont().deriveFont(Font.PLAIN, 16f));
        resultado.getAccessibleContext().setAccessibleName("Resultado de la simulación");
        detalleResultado.setFont(detalleResultado.getFont().deriveFont(Font.PLAIN, 12f));
        detalleResultado.getAccessibleContext().setAccessibleName("Pertenencia al lenguaje");
        resultado.setMinimumSize(new Dimension(0, resultado.getFontMetrics(resultado.getFont()).getHeight()));
        detalleResultado.setMinimumSize(new Dimension(0,
                detalleResultado.getFontMetrics(detalleResultado.getFont()).getHeight()));
        panelResultado.add(resultado, BorderLayout.NORTH);
        panelResultado.add(detalleResultado, BorderLayout.CENTER);

        limpiar();
    }

    public JPanel getPanelResultado() {
        return panelResultado;
    }

    public void mostrar(Estado estado, long pasos, int posicion, String regla, String mensaje,
            String cadenaInicial) {
        etiquetaEstado.setText("Estado: " + estado);
        etiquetaPasos.setText("Paso: " + pasos);
        etiquetaCabezal.setText("Cabezal: " + posicion);
        descripcion.setText(estado.getDescripcion());
        transicion.setText(pasos == 0 || regla == null || regla.isBlank()
                ? "—" : regla.replace(",", ", ").replace(" = ", " → "));
        transicion.setToolTipText(pasos == 0 ? "Todavía no se ha ejecutado una transición." : transicion.getText());
        resultado.setFont(resultado.getFont().deriveFont(estado.esFinal() ? Font.BOLD : Font.PLAIN));
        if (estado.esFinal()) {
            boolean aceptada = estado == Estado.ACEPTAR;
            resultado.setText(aceptada ? "✓ Cadena aceptada" : "✕ Cadena rechazada");
            detalleResultado.setText(cadenaInicial + (aceptada ? " pertenece" : " no pertenece")
                    + " a L = {aⁿbⁿ | n ≥ 1}");
            detalleResultado.setToolTipText(detalleResultado.getText());
        } else {
            resultado.setText(mensaje == null || mensaje.isBlank() ? "Simulación preparada." : mensaje);
            detalleResultado.setText(" ");
            detalleResultado.setToolTipText(null);
        }
        resultado.setToolTipText(resultado.getText());
    }

    public void limpiar() {
        etiquetaEstado.setText("Estado: —");
        etiquetaPasos.setText("Paso: 0");
        etiquetaCabezal.setText("Cabezal: —");
        descripcion.setText("Carga una cadena para comenzar.");
        transicion.setText("—");
        transicion.setToolTipText(null);
        resultado.setText("Esperando una cadena.");
        resultado.setFont(resultado.getFont().deriveFont(Font.PLAIN));
        resultado.setToolTipText(null);
        detalleResultado.setText(" ");
        detalleResultado.setToolTipText(null);
    }
}
