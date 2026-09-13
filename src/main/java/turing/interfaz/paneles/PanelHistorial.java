package turing.interfaz.paneles;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import turing.simulacion.ResultadoPaso;

/** Añade un registro por paso a una tabla de solo lectura. */
public final class PanelHistorial extends JPanel {
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Paso", "Estado anterior", "Leído", "Escrito", "Movimiento", "Nuevo estado", "Cabezal"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel resumen = new JLabel("0 pasos registrados");

    public PanelHistorial() {
        super(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(27);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        tabla.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(JLabel.CENTER);
        tabla.setDefaultRenderer(Object.class, centrado);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(125);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(125);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(105);
        tabla.getAccessibleContext().setAccessibleName("Historial de transiciones");
        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setColumnHeaderView(tabla.getTableHeader());
        add(desplazamiento, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout());
        pie.add(resumen, BorderLayout.WEST);
        pie.add(new JLabel("L: izquierda · R: derecha · —: sin regla definida"), BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);
    }

    public void agregar(ResultadoPaso paso) {
        modelo.addRow(new Object[] {
                paso.numero(), paso.estadoAnterior(), paso.simboloLeido(), paso.simboloEscrito(),
                paso.direccion() == null ? "—" : paso.direccion(), paso.estadoNuevo(),
                paso.posicionAnterior() + " → " + paso.posicionNueva()
        });
        int total = modelo.getRowCount();
        resumen.setText(total + (total == 1 ? " paso registrado" : " pasos registrados"));
        SwingUtilities.invokeLater(() -> {
            int ultimaFila = modelo.getRowCount() - 1;
            if (ultimaFila >= 0) {
                tabla.scrollRectToVisible(tabla.getCellRect(ultimaFila, 0, true));
            }
        });
    }

    public void limpiar() {
        modelo.setRowCount(0);
        resumen.setText("0 pasos registrados");
    }
}
