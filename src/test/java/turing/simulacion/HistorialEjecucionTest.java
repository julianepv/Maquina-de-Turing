package turing.simulacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Direccion;
import turing.modelo.maquina.Estado;

class HistorialEjecucionTest {

    @Test
    void conservaPasosEnOrdenYProtegeLaColeccion() {
        HistorialEjecucion historial = new HistorialEjecucion();
        ResultadoPaso primero = new ResultadoPaso(1, Estado.Q0, Simbolo.A, Simbolo.X,
                Direccion.R, Estado.Q1, 0, 1);
        ResultadoPaso segundo = new ResultadoPaso(2, Estado.Q1, Simbolo.B, Simbolo.Y,
                Direccion.L, Estado.Q2, 1, 0);

        historial.agregar(primero);
        historial.agregar(segundo);

        assertEquals(2, historial.getPasos().size());
        assertEquals(primero, historial.getPasos().get(0));
        assertEquals(segundo, historial.getPasos().get(1));
        assertThrows(UnsupportedOperationException.class, () -> historial.getPasos().clear());

        historial.limpiar();
        assertTrue(historial.getPasos().isEmpty());
    }
}
