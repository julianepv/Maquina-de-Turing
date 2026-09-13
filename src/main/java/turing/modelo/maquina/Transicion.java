package turing.modelo.maquina;

import java.util.Objects;
import turing.modelo.cinta.Simbolo;

/** Una regla de la función δ: leer, escribir, cambiar de estado y mover. */
public record Transicion(
        Estado estadoActual,
        Simbolo simboloLeido,
        Estado estadoSiguiente,
        Simbolo simboloEscrito,
        Direccion direccion) {

    public Transicion {
        Objects.requireNonNull(estadoActual, "El estado actual es obligatorio.");
        Objects.requireNonNull(simboloLeido, "El símbolo leído es obligatorio.");
        Objects.requireNonNull(estadoSiguiente, "El estado siguiente es obligatorio.");
        Objects.requireNonNull(simboloEscrito, "El símbolo escrito es obligatorio.");
        Objects.requireNonNull(direccion, "La dirección es obligatoria.");
        if (estadoActual.esFinal()) {
            throw new IllegalArgumentException("Los estados finales no tienen transiciones de salida.");
        }
    }

    public String notacion() {
        return "δ(" + estadoActual + "," + simboloLeido + ") = ("
                + estadoSiguiente + "," + simboloEscrito + "," + direccion + ")";
    }
}
