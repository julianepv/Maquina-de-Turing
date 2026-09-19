package turing.interfaz.paneles;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import turing.interfaz.componentes.TablaTransiciones;
import turing.simulacion.ResultadoPaso;

/** Tabla única que muestra las transiciones ejecutadas por la máquina. */
public final class PanelTransiciones extends JPanel {

    private final DefaultTableModel modelo = new DefaultTableModel(
        new String[] {
            "Paso",
            "Estado actual",
            "Lee",
            "Escribe",
            "Movimiento",
            "Nuevo estado",
            "Cabezal",
        },
        0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final TablaTransiciones tabla = new TablaTransiciones(
        modelo,
        10,
        135,
        75,
        90,
        75,
        135,
        125
    );

    private final JScrollPane desplazamiento = new JScrollPane(tabla);

    public PanelTransiciones() {
        setLayout(null);
        setBackground(Color.WHITE);

        tabla
            .getAccessibleContext()
            .setAccessibleName("Transiciones ejecutadas");

        desplazamiento.setBounds(0, 0, 970, 178);
        desplazamiento.setBackground(Color.WHITE);
        desplazamiento.getViewport().setBackground(Color.WHITE);
        desplazamiento.setBorder(new LineBorder(new Color(80, 125, 175), 1));
        add(desplazamiento);
    }

    public void agregar(ResultadoPaso paso) {
        Object movimiento = paso.direccion() == null ? "—" : paso.direccion();
        Object cabezal =
            paso.direccion() == null
                ? String.valueOf(paso.posicionAnterior())
                : paso.posicionAnterior() + " → " + paso.posicionNueva();

        modelo.addRow(new Object[] {
            paso.numero(),
            paso.estadoAnterior(),
            paso.simboloLeido(),
            paso.simboloEscrito(),
            movimiento,
            paso.estadoNuevo(),
            cabezal,
        });

        SwingUtilities.invokeLater(() -> {
            int ultimaFila = modelo.getRowCount() - 1;
            if (ultimaFila >= 0) {
                tabla.setRowSelectionInterval(ultimaFila, ultimaFila);
                tabla.scrollRectToVisible(
                    tabla.getCellRect(ultimaFila, 0, true)
                );
            }
        });
    }

    public void limpiar() {
        modelo.setRowCount(0);
        tabla.clearSelection();
    }
}
