package turing.simulacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Direccion;
import turing.modelo.maquina.Estado;

class ResultadoPasoTest {

    @Test
    void expresaLaReglaEnNotacionFormal() {
        ResultadoPaso paso = new ResultadoPaso(2, Estado.Q1, Simbolo.B, Simbolo.Y,
                Direccion.L, Estado.Q2, 1, 0);

        assertEquals("δ(q1,b) = (q2,Y,L)", paso.notacion());
    }

    @Test
    void identificaLaReglaIndefinidaSinInventarUnaDireccion() {
        ResultadoPaso paso = new ResultadoPaso(1, Estado.Q0, Simbolo.B, Simbolo.B,
                null, Estado.RECHAZAR, 0, 0);

        assertTrue(paso.notacion().contains("δ(q0,b)"));
        assertTrue(paso.notacion().contains("indefinida"));
        assertTrue(paso.notacion().contains("RECHAZAR"));
    }
}
