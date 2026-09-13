package turing.interfaz.paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import turing.modelo.maquina.Estado;

/** Presenta la configuración actual y la última regla aplicada, sin ejecutar lógica. */
public final class PanelEstado extends JPanel {
    private final JLabel etiquetaEstado = new JLabel();
    private final JLabel etiquetaPasos = new JLabel();
    private final JLabel etiquetaCabezal = new JLabel();
    private final JTextArea descripcion = new JTextArea(1, 40);
    private final JTextField transicion = new JTextField();
    private final JTextArea resultado = new JTextArea(2, 40);
    private static final Color TEXTO = new Color(30, 41, 59);

    public PanelEstado() {
        super(new BorderLayout(10, 5));
        setBorder(BorderFactory.createTitledBorder("Estado y transición"));

        JPanel datos = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        etiquetaEstado.setFont(etiquetaEstado.getFont().deriveFont(Font.BOLD, 19f));
        datos.add(etiquetaEstado);
        datos.add(etiquetaPasos);
        datos.add(etiquetaCabezal);
        add(datos, BorderLayout.NORTH);

        JPanel detalle = new JPanel();
        detalle.setLayout(new BoxLayout(detalle, BoxLayout.Y_AXIS));
        detalle.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        prepararTexto(descripcion);
        prepararTexto(resultado);
        descripcion.getAccessibleContext().setAccessibleName("Descripción del estado");
        resultado.getAccessibleContext().setAccessibleName("Resultado de la simulación");
        descripcion.setAlignmentX(LEFT_ALIGNMENT);
        detalle.add(descripcion);

        JPanel regla = new JPanel(new BorderLayout(8, 0));
        regla.setAlignmentX(LEFT_ALIGNMENT);
        regla.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        regla.add(new JLabel("Última transición:"), BorderLayout.WEST);
        transicion.setEditable(false);
        transicion.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        transicion.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));
        transicion.getAccessibleContext().setAccessibleName("Última transición ejecutada");
        regla.add(transicion, BorderLayout.CENTER);
        detalle.add(regla);
        resultado.setFont(resultado.getFont().deriveFont(Font.BOLD, 16f));
        resultado.setAlignmentX(LEFT_ALIGNMENT);
        JScrollPane mensaje = new JScrollPane(resultado,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mensaje.setBorder(BorderFactory.createEmptyBorder());
        mensaje.setOpaque(false);
        mensaje.getViewport().setOpaque(false);
        mensaje.setMinimumSize(new Dimension(100, 20));
        mensaje.setAlignmentX(LEFT_ALIGNMENT);
        detalle.add(mensaje);
        add(detalle, BorderLayout.CENTER);
        limpiar();
    }

    private static void prepararTexto(JTextArea campo) {
        campo.setEditable(false);
        campo.setLineWrap(true);
        campo.setWrapStyleWord(true);
        campo.setOpaque(false);
        campo.setFont(UIManager.getFont("Label.font"));
        campo.setBorder(BorderFactory.createEmptyBorder());
        // Sin mínimo explícito, el ajuste de línea puede pedir miles de píxeles
        // de alto antes de conocer el ancho disponible en el primer layout.
        campo.setMinimumSize(new Dimension(0, campo.getFontMetrics(campo.getFont()).getHeight()));
    }

    public void mostrar(Estado estado, long pasos, int posicion, String regla, String mensaje) {
        etiquetaEstado.setText("Estado actual: " + estado);
        etiquetaPasos.setText("Pasos: " + pasos);
        etiquetaCabezal.setText("Cabezal: celda " + posicion);
        descripcion.setText(estado.getDescripcion());
        transicion.setText(regla == null || regla.isBlank() ? "—" : regla);
        transicion.setCaretPosition(0);
        if (estado == Estado.ACEPTAR) {
            resultado.setText("Cadena aceptada" + detalleMensaje(mensaje, "Cadena aceptada"));
            resultado.setForeground(new Color(22, 112, 55));
        } else if (estado == Estado.RECHAZAR) {
            resultado.setText("Cadena rechazada" + detalleMensaje(mensaje, "Cadena rechazada"));
            resultado.setForeground(new Color(169, 36, 36));
        } else {
            resultado.setText(mensaje == null || mensaje.isBlank() ? "Simulación preparada." : mensaje);
            resultado.setForeground(TEXTO);
        }
        resultado.setCaretPosition(0);
        resultado.setToolTipText(resultado.getText());
    }

    private static String detalleMensaje(String mensaje, String prefijo) {
        if (mensaje == null || mensaje.isBlank() || mensaje.equalsIgnoreCase(prefijo)) {
            return "";
        }
        if (mensaje.regionMatches(true, 0, prefijo, 0, prefijo.length())) {
            return mensaje.substring(prefijo.length());
        }
        return " · " + mensaje;
    }

    public void limpiar() {
        etiquetaEstado.setText("Estado actual: —");
        etiquetaPasos.setText("Pasos: 0");
        etiquetaCabezal.setText("Cabezal: —");
        descripcion.setText("Carga una cadena para preparar el estado inicial q0.");
        transicion.setText("—");
        resultado.setText("Esperando una cadena.");
        resultado.setForeground(TEXTO);
        resultado.setToolTipText(null);
    }
}
