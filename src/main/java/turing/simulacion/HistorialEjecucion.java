package turing.simulacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class HistorialEjecucion {
    private final List<ResultadoPaso> pasos = new ArrayList<>();

    public void agregar(ResultadoPaso paso) {
        pasos.add(Objects.requireNonNull(paso, "El paso es obligatorio."));
    }

    public List<ResultadoPaso> getPasos() {
        return List.copyOf(pasos);
    }

    public void limpiar() {
        pasos.clear();
    }
}
