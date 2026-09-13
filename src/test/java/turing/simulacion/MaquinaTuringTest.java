package turing.simulacion;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Direccion;
import turing.modelo.maquina.Estado;
import turing.modelo.maquina.Transicion;
import turing.reglas.TablaTransiciones;

class MaquinaTuringTest {

    @ParameterizedTest
    @ValueSource(strings = {"ab", "aabb", "aaabbb", "aaaabbbb", "aaaaabbbbb"})
    void aceptaLosEjemplosDelLenguaje(String entrada) {
        MaquinaTuring maquina = ejecutarHastaFinalizar(entrada);

        assertEquals(Estado.ACEPTAR, maquina.getEstadoActual());
        int mitad = entrada.length() / 2;
        for (int i = 0; i < entrada.length(); i++) {
            assertEquals(i < mitad ? Simbolo.X : Simbolo.Y, maquina.getCinta().leer(i));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"aab", "abb", "abab", "ba", "aaabb", "a", "b", "aa", "bb", "bbaa", "aababb"})
    void rechazaDesbalancesYOrdenesIncorrectos(String entrada) {
        assertEquals(Estado.RECHAZAR, ejecutarHastaFinalizar(entrada).getEstadoActual());
    }

    @Test
    void unPasoAplicaExactamenteUnaReglaDeLecturaEscrituraEstadoYMovimiento() {
        TablaTransiciones tabla = new TablaTransiciones();
        tabla.registrar(new Transicion(Estado.Q0, Simbolo.A, Estado.Q1, Simbolo.X, Direccion.L));
        tabla.registrar(new Transicion(Estado.Q1, Simbolo.BLANCO, Estado.Q2, Simbolo.Y, Direccion.R));
        MaquinaTuring maquina = new MaquinaTuring("ab", tabla);

        ResultadoPaso paso = maquina.paso();

        assertAll(
                () -> assertEquals(1L, paso.numero()),
                () -> assertEquals(Estado.Q0, paso.estadoAnterior()),
                () -> assertEquals(Simbolo.A, paso.simboloLeido()),
                () -> assertEquals(Simbolo.X, paso.simboloEscrito()),
                () -> assertEquals(Direccion.L, paso.direccion()),
                () -> assertEquals(Estado.Q1, paso.estadoNuevo()),
                () -> assertEquals(0, paso.posicionAnterior()),
                () -> assertEquals(-1, paso.posicionNueva()),
                () -> assertEquals(Estado.Q1, maquina.getEstadoActual()),
                () -> assertEquals(1L, maquina.getNumeroPasos()),
                () -> assertEquals(-1, maquina.getCinta().getPosicionCabezal()),
                () -> assertEquals(Simbolo.X, maquina.getCinta().leer(0)),
                () -> assertEquals(Simbolo.BLANCO, maquina.getCinta().leer(-1)));

        ResultadoPaso segundoPaso = maquina.paso();
        assertEquals(2L, segundoPaso.numero());
        assertEquals(Estado.Q2, maquina.getEstadoActual());
        assertEquals(0, maquina.getCinta().getPosicionCabezal());
        assertEquals(Simbolo.Y, maquina.getCinta().leer(-1));
    }

    @Test
    void unaReglaInexistenteRechazaSinEscribirNiMoverElCabezal() {
        MaquinaTuring maquina = new MaquinaTuring("ab", new TablaTransiciones());

        ResultadoPaso paso = maquina.paso();

        assertEquals(Estado.RECHAZAR, maquina.getEstadoActual());
        assertTrue(maquina.haTerminado());
        assertEquals(1L, maquina.getNumeroPasos());
        assertEquals(Estado.Q0, paso.estadoAnterior());
        assertEquals(Estado.RECHAZAR, paso.estadoNuevo());
        assertEquals(Simbolo.A, paso.simboloLeido());
        assertEquals(Simbolo.A, paso.simboloEscrito());
        assertNull(paso.direccion());
        assertEquals(paso.posicionAnterior(), paso.posicionNueva());
        assertEquals(0, maquina.getCinta().getPosicionCabezal());
        assertEquals(Simbolo.A, maquina.getCinta().leer());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "aab"})
    void unaMaquinaFinalizadaNoEjecutaPasosAdicionales(String entrada) {
        MaquinaTuring maquina = ejecutarHastaFinalizar(entrada);
        long numeroPasos = maquina.getNumeroPasos();
        Estado estadoFinal = maquina.getEstadoActual();
        int posicionFinal = maquina.getCinta().getPosicionCabezal();

        assertThrows(IllegalStateException.class, maquina::paso);

        assertEquals(numeroPasos, maquina.getNumeroPasos());
        assertEquals(estadoFinal, maquina.getEstadoActual());
        assertEquals(posicionFinal, maquina.getCinta().getPosicionCabezal());
    }

    @Test
    void coincideConLaDefinicionDelLenguajeParaTodasLasCadenasHastaLongitudDiez() {
        // El oráculo se usa solo en las pruebas: el programa debe decidir mediante transiciones.
        for (int longitud = 1; longitud <= 10; longitud++) {
            for (int combinacion = 0; combinacion < (1 << longitud); combinacion++) {
                StringBuilder entrada = new StringBuilder(longitud);
                for (int posicion = 0; posicion < longitud; posicion++) {
                    entrada.append((combinacion & (1 << posicion)) == 0 ? 'a' : 'b');
                }
                String cadena = entrada.toString();
                int mitad = longitud / 2;
                boolean pertenece = longitud % 2 == 0
                        && cadena.equals("a".repeat(mitad) + "b".repeat(mitad));

                Estado esperado = pertenece ? Estado.ACEPTAR : Estado.RECHAZAR;
                assertEquals(esperado, ejecutarHastaFinalizar(cadena).getEstadoActual(), cadena);
            }
        }
    }

    private static MaquinaTuring ejecutarHastaFinalizar(String entrada) {
        MaquinaTuring maquina = new MaquinaTuring(entrada);
        long limitePasos = 4L * entrada.length() * entrada.length() + 10;
        while (!maquina.haTerminado() && maquina.getNumeroPasos() < limitePasos) {
            maquina.paso();
        }
        assertTrue(maquina.haTerminado(), () -> "La máquina no se detuvo para: " + entrada);
        return maquina;
    }
}
