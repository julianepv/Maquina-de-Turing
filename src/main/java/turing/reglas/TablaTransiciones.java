package turing.reglas;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Estado;
import turing.modelo.maquina.Transicion;

/** Función parcial y determinista, indexada por (estado, símbolo leído). */
public final class TablaTransiciones {
    private record Clave(Estado estado, Simbolo simbolo) { }

    private final Map<Clave, Transicion> transiciones = new LinkedHashMap<>();

    public void registrar(Transicion transicion) {
        Objects.requireNonNull(transicion, "La transición es obligatoria.");
        Clave clave = new Clave(transicion.estadoActual(), transicion.simboloLeido());
        if (transiciones.putIfAbsent(clave, transicion) != null) {
            throw new IllegalArgumentException("Ya existe una transición para δ("
                    + transicion.estadoActual() + "," + transicion.simboloLeido() + ").");
        }
    }

    public Optional<Transicion> buscar(Estado estado, Simbolo simbolo) {
        Objects.requireNonNull(estado, "El estado es obligatorio.");
        Objects.requireNonNull(simbolo, "El símbolo es obligatorio.");
        return Optional.ofNullable(transiciones.get(new Clave(estado, simbolo)));
    }

    public List<Transicion> getTransiciones() {
        return List.copyOf(transiciones.values());
    }
}
