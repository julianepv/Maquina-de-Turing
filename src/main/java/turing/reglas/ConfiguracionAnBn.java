package turing.reglas;

import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Direccion;
import turing.modelo.maquina.Estado;
import turing.modelo.maquina.Transicion;

/** Reglas de L = {aⁿbⁿ | n ≥ 1}; las combinaciones sin regla rechazan. */
public final class ConfiguracionAnBn {
    private ConfiguracionAnBn() { }

    public static TablaTransiciones crearTabla() {
        TablaTransiciones tabla = new TablaTransiciones();

        // Marcar una a y buscar su pareja b a la derecha.
        tabla.registrar(new Transicion(Estado.Q0, Simbolo.A, Estado.Q1, Simbolo.X, Direccion.R));
        tabla.registrar(new Transicion(Estado.Q0, Simbolo.Y, Estado.Q3, Simbolo.Y, Direccion.R));
        tabla.registrar(new Transicion(Estado.Q1, Simbolo.A, Estado.Q1, Simbolo.A, Direccion.R));
        tabla.registrar(new Transicion(Estado.Q1, Simbolo.Y, Estado.Q1, Simbolo.Y, Direccion.R));
        tabla.registrar(new Transicion(Estado.Q1, Simbolo.B, Estado.Q2, Simbolo.Y, Direccion.L));

        // Regresar hasta la X más cercana y empezar una nueva pareja.
        tabla.registrar(new Transicion(Estado.Q2, Simbolo.A, Estado.Q2, Simbolo.A, Direccion.L));
        tabla.registrar(new Transicion(Estado.Q2, Simbolo.Y, Estado.Q2, Simbolo.Y, Direccion.L));
        tabla.registrar(new Transicion(Estado.Q2, Simbolo.X, Estado.Q0, Simbolo.X, Direccion.R));

        // Tras agotar las a, solo pueden quedar Y antes del blanco final.
        tabla.registrar(new Transicion(Estado.Q3, Simbolo.Y, Estado.Q3, Simbolo.Y, Direccion.R));
        tabla.registrar(new Transicion(Estado.Q3, Simbolo.BLANCO,
                Estado.ACEPTAR, Simbolo.BLANCO, Direccion.R));
        return tabla;
    }
}
