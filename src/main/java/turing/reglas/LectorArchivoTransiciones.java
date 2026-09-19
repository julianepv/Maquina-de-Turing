package turing.reglas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Lee un archivo de texto con una ecuación δ por línea. */
public final class LectorArchivoTransiciones {
    private LectorArchivoTransiciones() { }

    public static List<String> leer(Path archivo) throws IOException {
        List<String> reglas = new ArrayList<>();
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        for (int indice = 0; indice < lineas.size(); indice++) {
            String linea = lineas.get(indice).trim();
            if (linea.isEmpty()) {
                continue;
            }
            if (linea.chars().filter(caracter -> caracter == '=').count() != 1) {
                throw new IllegalArgumentException("Error en la línea " + (indice + 1)
                        + ": usa el formato (q0, a)=(q1, X, R).");
            }
            reglas.add(linea);
        }
        if (reglas.isEmpty()) {
            throw new IllegalArgumentException("El archivo no contiene transiciones.");
        }
        return List.copyOf(reglas);
    }
}
