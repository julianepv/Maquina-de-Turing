package turing.simulacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Estado;

class MotorSimulacionTest {

    @Test
    void informaCuandoSeIntentaUsarLaSimulacionSinCadena() {
        MotorSimulacion motor = new MotorSimulacion();

        assertFalse(motor.hayCadenaCargada());
        assertEquals("", motor.getCadenaInicial());
        assertTrue(motor.getHistorial().getPasos().isEmpty());
        assertThrows(IllegalStateException.class, motor::getMaquina);
        assertThrows(IllegalStateException.class, motor::paso);
    }

    @Test
    void cadaPasoSeGuardaUnaVezEnElHistorial() {
        MotorSimulacion motor = new MotorSimulacion();
        motor.cargar("aabb");

        ResultadoPaso primerPaso = motor.paso();
        ResultadoPaso segundoPaso = motor.paso();

        assertEquals(2L, motor.getMaquina().getNumeroPasos());
        assertEquals(2, motor.getHistorial().getPasos().size());
        assertEquals(primerPaso, motor.getHistorial().getPasos().get(0));
        assertEquals(segundoPaso, motor.getHistorial().getPasos().get(1));
        assertEquals(1L, primerPaso.numero());
        assertEquals(2L, segundoPaso.numero());
    }

    @Test
    void reiniciarRestauraLaCadenaOriginalElCabezalYElEstado() {
        MotorSimulacion motor = new MotorSimulacion();
        motor.cargar("aabb");
        motor.paso();
        motor.paso();

        motor.reiniciar();

        assertTrue(motor.hayCadenaCargada());
        assertEquals("aabb", motor.getCadenaInicial());
        assertEquals(Estado.Q0, motor.getMaquina().getEstadoActual());
        assertEquals(0L, motor.getMaquina().getNumeroPasos());
        assertEquals(0, motor.getMaquina().getCinta().getPosicionCabezal());
        assertEquals(Simbolo.A, motor.getMaquina().getCinta().leer(0));
        assertEquals(Simbolo.A, motor.getMaquina().getCinta().leer(1));
        assertEquals(Simbolo.B, motor.getMaquina().getCinta().leer(2));
        assertEquals(Simbolo.B, motor.getMaquina().getCinta().leer(3));
        assertTrue(motor.getHistorial().getPasos().isEmpty());
    }

    @Test
    void cargarUnaEntradaInvalidaConservaLaSimulacionAnterior() {
        MotorSimulacion motor = new MotorSimulacion();
        motor.cargar("ab");
        ResultadoPaso paso = motor.paso();
        MaquinaTuring maquina = motor.getMaquina();

        assertThrows(IllegalArgumentException.class, () -> motor.cargar("abc"));

        assertSame(maquina, motor.getMaquina());
        assertEquals("ab", motor.getCadenaInicial());
        assertEquals(1L, maquina.getNumeroPasos());
        assertEquals(1, motor.getHistorial().getPasos().size());
        assertEquals(paso, motor.getHistorial().getPasos().get(0));
    }

    @Test
    void permiteCargarOtraCadenaDespuesDeFinalizar() {
        MotorSimulacion motor = new MotorSimulacion();
        motor.cargar("ab");
        for (int i = 0; i < 30 && !motor.getMaquina().haTerminado(); i++) {
            motor.paso();
        }
        assertEquals(Estado.ACEPTAR, motor.getMaquina().getEstadoActual());

        motor.cargar("ba");

        assertEquals("ba", motor.getCadenaInicial());
        assertEquals(Estado.Q0, motor.getMaquina().getEstadoActual());
        assertEquals(0L, motor.getMaquina().getNumeroPasos());
        assertTrue(motor.getHistorial().getPasos().isEmpty());
        assertEquals(Simbolo.B, motor.getMaquina().getCinta().leer());
    }

    @Test
    void limpiarEliminaLaCadenaCargadaYElHistorial() {
        MotorSimulacion motor = new MotorSimulacion();
        motor.cargar("ab");
        motor.paso();

        motor.limpiar();

        assertFalse(motor.hayCadenaCargada());
        assertEquals("", motor.getCadenaInicial());
        assertTrue(motor.getHistorial().getPasos().isEmpty());
        assertThrows(IllegalStateException.class, motor::getMaquina);
        assertThrows(IllegalStateException.class, motor::paso);
    }
}
