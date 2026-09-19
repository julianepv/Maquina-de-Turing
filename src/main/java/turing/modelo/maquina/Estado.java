package turing.modelo.maquina;

import java.util.Objects;

/** Estado de una máquina de Turing definido por el programa que se va a ejecutar. */
public final class Estado {
    /* Se conservan para la configuración predeterminada y para el rechazo por δ indefinida. */
    public static final Estado Q0 = normal("q0");
    public static final Estado Q1 = normal("q1");
    public static final Estado Q2 = normal("q2");
    public static final Estado Q3 = normal("q3");
    public static final Estado ACEPTAR = new Estado("ACEPTAR", true, true);
    public static final Estado RECHAZAR = new Estado("RECHAZAR", true, false);

    private final String nombre;
    private final boolean finalizado;
    private final boolean aceptar;

    private Estado(String nombre, boolean finalizado, boolean aceptar) {
        this.nombre = validarNombre(nombre);
        this.finalizado = finalizado;
        this.aceptar = aceptar;
    }

    public static Estado normal(String nombre) {
        return new Estado(nombre, false, false);
    }

    public static Estado deAceptacion(String nombre) {
        return new Estado(nombre, true, true);
    }

    private static String validarNombre(String nombre) {
        Objects.requireNonNull(nombre, "El nombre del estado es obligatorio.");
        String limpio = nombre.trim();
        if (!limpio.matches("[A-Za-z][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException(
                    "El estado '" + nombre + "' debe usar letras, números o _ y comenzar con una letra.");
        }
        return limpio;
    }

    public boolean esFinal() {
        return finalizado;
    }

    public boolean esAceptar() {
        return aceptar;
    }

    public String getDescripcion() {
        if (aceptar) {
            return "La máquina llegó a un estado de aceptación.";
        }
        if (finalizado) {
            return "La máquina se detuvo sin aceptar la cadena.";
        }
        return "Estado definido por las transiciones ingresadas.";
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object objeto) {
        return objeto instanceof Estado otro && nombre.equals(otro.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}
