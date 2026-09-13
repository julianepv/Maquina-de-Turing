package turing.simulacion;

import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Direccion;
import turing.modelo.maquina.Estado;

/** Evidencia inmutable de un paso; dirección nula significa regla indefinida. */
public record ResultadoPaso(
        long numero,
        Estado estadoAnterior,
        Simbolo simboloLeido,
        Simbolo simboloEscrito,
        Direccion direccion,
        Estado estadoNuevo,
        int posicionAnterior,
        int posicionNueva) {

    public String notacion() {
        String origen = "δ(" + estadoAnterior + "," + simboloLeido + ")";
        if (direccion == null) {
            return origen + " indefinida → RECHAZAR (sin escritura ni movimiento)";
        }
        return origen + " = (" + estadoNuevo + "," + simboloEscrito + "," + direccion + ")";
    }
}
