package turing.interfaz.paneles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import turing.interfaz.componentes.CeldaCinta;
import turing.modelo.cinta.Cinta;

class PanelCintaTest {

    @Test
    void muestraSoloUnBlancoDeMargenACadaLadoDeLaCadena() {
        PanelCinta panel = new PanelCinta();
        panel.mostrar(new Cinta("ab"));

        long celdasVisibles = java.util.Arrays.stream(panel.getComponents())
                .filter(CeldaCinta.class::isInstance)
                .filter(java.awt.Component::isVisible)
                .count();

        assertEquals(4, celdasVisibles); // B, a, b, B
    }
}
