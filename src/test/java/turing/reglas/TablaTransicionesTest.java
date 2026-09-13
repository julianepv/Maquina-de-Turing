package turing.reglas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Direccion;
import turing.modelo.maquina.Estado;
import turing.modelo.maquina.Transicion;

class TablaTransicionesTest {

    @Test
    void buscaLasReglasPorEstadoYSimboloLeido() {
        TablaTransiciones tabla = new TablaTransiciones();
        Transicion regla = new Transicion(Estado.Q0, Simbolo.A, Estado.Q1, Simbolo.X, Direccion.R);
        tabla.registrar(regla);

        assertEquals(regla, tabla.buscar(Estado.Q0, Simbolo.A).orElseThrow());
        assertTrue(tabla.buscar(Estado.Q0, Simbolo.B).isEmpty());
        assertTrue(tabla.buscar(Estado.Q1, Simbolo.A).isEmpty());
    }

    @Test
    void impideModificarLaListaDeReglasDesdeFuera() {
        TablaTransiciones tabla = ConfiguracionAnBn.crearTabla();

        assertFalse(tabla.getTransiciones().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> tabla.getTransiciones().clear());
    }
}
