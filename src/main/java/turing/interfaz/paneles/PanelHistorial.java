package turing.interfaz.paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import turing.interfaz.componentes.TablaTransiciones;
import turing.simulacion.ResultadoPaso;

/** Añade un registro por paso a una tabla de solo lectura. */
public final class PanelHistorial extends JPanel {
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Paso", "Estado", "Lee", "Escribe", "Movimiento", "Nuevo estado", "Cabezal"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final TablaTransiciones tabla = new TablaTransiciones(modelo, 64, 100, 64, 76, 120, 132, 132);
    private final JLabel resumen = new JLabel("0 pasos");

    public PanelHistorial() {
        super(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tabla.setPreferredScrollableViewportSize(new Dimension(
                tabla.getPreferredSize().width, tabla.getRowHeight() * 5));
        tabla.getAccessibleContext().setAccessibleName("Historial de transiciones");
        tabla.setToolTipText("L: izquierda · R: derecha · —: sin regla definida");

        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.setViewportBorder(null);
        add(desplazamiento, BorderLayout.CENTER);

        resumen.setFont(resumen.getFont().deriveFont(Font.PLAIN, 12f));
        Color fondo = getBackground();
        Color texto = resumen.getForeground();
        resumen.setForeground(new Color(
                (texto.getRed() * 3 + fondo.getRed()) / 4,
                (texto.getGreen() * 3 + fondo.getGreen()) / 4,
                (texto.getBlue() * 3 + fondo.getBlue()) / 4));
        add(resumen, BorderLayout.SOUTH);
    }

    public void agregar(ResultadoPaso paso) {
        modelo.addRow(new Object[] {
                paso.numero(), paso.estadoAnterior(), paso.simboloLeido(), paso.simboloEscrito(),
                paso.direccion() == null ? "—" : paso.direccion(), paso.estadoNuevo(),
                paso.posicionAnterior() + " → " + paso.posicionNueva()
        });
        int total = modelo.getRowCount();
        resumen.setText(total + (total == 1 ? " paso" : " pasos"));
        SwingUtilities.invokeLater(() -> {
            int ultimaFila = modelo.getRowCount() - 1;
            if (ultimaFila >= 0) {
                tabla.scrollRectToVisible(tabla.getCellRect(ultimaFila, 0, true));
            }
        });
    }

    public void limpiar() {
        modelo.setRowCount(0);
        resumen.setText("0 pasos");
    }
}
