package turing.modelo.cinta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import turing.modelo.maquina.Direccion;

/**
 * Cinta dispersa con índices positivos y negativos.
 * Las celdas no almacenadas representan blancos; el String solo carga la entrada.
 */
public final class Cinta {
    private final Map<Integer, Simbolo> celdas = new HashMap<>();
    private int posicionCabezal;
    private int extremoIzquierdo;
    private int extremoDerecho;

    public Cinta(String cadena) {
        Objects.requireNonNull(cadena, "La cadena no puede ser nula.");
        for (int posicion = 0; posicion < cadena.length(); posicion++) {
            Simbolo simbolo = switch (cadena.charAt(posicion)) {
                case 'a' -> Simbolo.A;
                case 'b' -> Simbolo.B;
                default -> throw new IllegalArgumentException(
                        "La cinta inicial solo puede contener los símbolos a y b.");
            };
            celdas.put(posicion, simbolo);
        }
        extremoDerecho = Math.max(0, cadena.length() - 1);
    }

    public Simbolo leer() {
        return leer(posicionCabezal);
    }

    public Simbolo leer(int posicion) {
        return celdas.getOrDefault(posicion, Simbolo.BLANCO);
    }

    public void escribir(Simbolo simbolo) {
        Objects.requireNonNull(simbolo, "El símbolo es obligatorio.");
        if (simbolo == Simbolo.BLANCO) {
            celdas.remove(posicionCabezal);
        } else {
            celdas.put(posicionCabezal, simbolo);
        }
    }

    public void mover(Direccion direccion) {
        Objects.requireNonNull(direccion, "La dirección es obligatoria.");
        posicionCabezal = Math.addExact(posicionCabezal, direccion.getDesplazamiento());
        extremoIzquierdo = Math.min(extremoIzquierdo, posicionCabezal);
        extremoDerecho = Math.max(extremoDerecho, posicionCabezal);
    }

    public int getPosicionCabezal() {
        return posicionCabezal;
    }

    /** Incluye un blanco de margen fuera del área cargada o visitada. */
    public int getLimiteIzquierdo() {
        return extremoIzquierdo - 1;
    }

    /** Incluye un blanco de margen fuera del área cargada o visitada. */
    public int getLimiteDerecho() {
        return extremoDerecho + 1;
    }
}
