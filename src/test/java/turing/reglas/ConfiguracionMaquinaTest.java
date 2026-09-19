package turing.reglas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import turing.simulacion.MaquinaTuring;

class ConfiguracionMaquinaTest {
    @TempDir
    Path directorioTemporal;

    @Test
    void ejecutaLasTransicionesIngresadasPorLaPersonaUsuaria() {
        String reglas = """
                δ(q1, a) = (q1, a, R)
                δ(q1, b) = (q2, a, R)
                δ(q2, a) = (q2, a, R)
                δ(q2, b) = (q2, b, R)
                δ(q2, B) = (q3, B, L)
                """;
        ConfiguracionMaquina configuracion = ConfiguracionMaquina.desde("q1", "q3", reglas);
        MaquinaTuring maquina = new MaquinaTuring("aaabb", configuracion.tabla(),
                configuracion.estadoInicial());

        while (!maquina.haTerminado()) {
            maquina.paso();
        }

        assertTrue(maquina.getEstadoActual().esAceptar());
        assertEquals("q3", maquina.getEstadoActual().toString());
    }

    @Test
    void aceptaAaabbbConElProgramaAnBnYLosEstadosQ0YQf() {
        String reglas = """
                (q0, a)=(q1, X, R)
                (q0, Y)=(q3, Y, R)
                (q1, a)=(q1, a, R)
                (q1, Y)=(q1, Y, R)
                (q1, b)=(q2, Y, L)
                (q2, a)=(q2, a, L)
                (q2, Y)=(q2, Y, L)
                (q2, X)=(q0, X, R)
                (q3, Y)=(q3, Y, R)
                (q3, B)=(qf, B, R)
                """;
        ConfiguracionMaquina configuracion = ConfiguracionMaquina.desde("q0", "qf", reglas);
        MaquinaTuring maquina = new MaquinaTuring("aaabbb", configuracion.tabla(),
                configuracion.estadoInicial());

        while (!maquina.haTerminado()) {
            maquina.paso();
        }

        assertTrue(maquina.getEstadoActual().esAceptar());
        assertEquals("qf", maquina.getEstadoActual().toString());
    }

    @Test
    void leeArchivoDeTextoConUnaTransicionPorLinea() throws IOException {
        Path archivo = directorioTemporal.resolve("transiciones.txt");
        Files.writeString(archivo, """
                (q0, a)=(q1, X, R)

                (q1, B)=(qf, B, L)
                """);

        assertEquals(List.of("(q0, a)=(q1, X, R)", "(q1, B)=(qf, B, L)"),
                LectorArchivoTransiciones.leer(archivo));
    }
}
