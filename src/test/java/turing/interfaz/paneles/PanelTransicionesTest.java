package turing.interfaz.paneles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.junit.jupiter.api.Test;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Estado;

class PanelTransicionesTest {

    @Test
    void muestraEstadosFinalesYGuionesEnLasTransicionesAusentes() {
        PanelTransiciones panel = new PanelTransiciones();
        panel.agregarRegla("(inicio, a)", "(intermedio, X, R)");
        panel.asegurarFilas(List.of(Estado.deAceptacion("aceptar")));
        JTable tabla = tablaDe(panel);

        assertEquals("-", tabla.getValueAt(filaDe(tabla, "inicio"), 2));
        int filaFinal = filaDe(tabla, "aceptar");
        for (int columna = 1; columna <= 5; columna++) {
            assertEquals("-", tabla.getValueAt(filaFinal, columna));
        }
    }

    @Test
    void resaltaEnVerdeLaConfiguracionActualAntesDelPrimerPaso() {
        PanelTransiciones panel = new PanelTransiciones();
        panel.agregarRegla("(inicio, a)", "(aceptar, X, R)");
        JTable tabla = tablaDe(panel);

        panel.resaltarConfiguracion(Estado.normal("inicio"), Simbolo.A);

        int fila = filaDe(tabla, "inicio");
        Component celda = tabla.prepareRenderer(tabla.getCellRenderer(fila, 1), fila, 1);
        assertEquals(new Color(195, 235, 205), celda.getBackground());
    }

    @Test
    void ordenaNaturalmenteLosEstadosAunqueLasReglasSeAgreguenEnOtroOrden() {
        PanelTransiciones panel = new PanelTransiciones();
        panel.agregarRegla("(q0, a)", "(q3, X, R)");
        panel.agregarRegla("(q2, a)", "(qf, X, R)");
        panel.agregarRegla("(q1, a)", "(q1, X, R)");
        JTable tabla = tablaDe(panel);

        assertEquals(List.of("q0", "q1", "q2", "q3", "qf"),
                estadosDe(tabla));
    }

    private JTable tablaDe(PanelTransiciones panel) {
        JScrollPane desplazamiento = (JScrollPane) panel.getComponent(0);
        return (JTable) desplazamiento.getViewport().getView();
    }

    private int filaDe(JTable tabla, String estado) {
        for (int fila = 0; fila < tabla.getRowCount(); fila++) {
            if (estado.equals(tabla.getValueAt(fila, 0))) {
                return fila;
            }
        }
        throw new AssertionError("No se encontró el estado " + estado + ".");
    }

    private List<String> estadosDe(JTable tabla) {
        return java.util.stream.IntStream.range(0, tabla.getRowCount())
                .mapToObj(fila -> tabla.getValueAt(fila, 0).toString())
                .toList();
    }
}
