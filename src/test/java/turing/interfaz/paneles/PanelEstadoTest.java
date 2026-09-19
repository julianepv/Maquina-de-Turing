package turing.interfaz.paneles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import turing.modelo.maquina.Estado;

class PanelEstadoTest {

    @Test
    void soloMuestraLaFranjaDeResultadoCuandoLaMaquinaTermina() {
        PanelEstado panel = new PanelEstado();

        assertFalse(panel.getPanelResultado().isVisible());

        panel.mostrar(Estado.normal("q2"), 14, 3,
                "δ(q1,b) = (q2,Y,L)", "Paso completado.");
        assertFalse(panel.getPanelResultado().isVisible());

        panel.mostrar(Estado.deAceptacion("qf"), 25, 6,
                "δ(q3,B) = (qf,B,R)", "Cadena aceptada.");
        assertTrue(panel.getPanelResultado().isVisible());
    }
}
