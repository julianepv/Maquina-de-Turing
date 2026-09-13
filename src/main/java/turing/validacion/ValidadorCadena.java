package turing.validacion;

/** Valida únicamente la entrada; la pertenencia al lenguaje la decide la máquina. */
public final class ValidadorCadena {
    private ValidadorCadena() { }

    public static void validar(String cadena) {
        if (cadena == null || cadena.isEmpty()) {
            throw new IllegalArgumentException("Ingresa una cadena no vacía con los símbolos a y b (n ≥ 1).");
        }
        for (int posicion = 0; posicion < cadena.length(); posicion++) {
            char simbolo = cadena.charAt(posicion);
            if (simbolo != 'a' && simbolo != 'b') {
                throw new IllegalArgumentException("Entrada inválida en la posición "
                        + (posicion + 1) + ": solo se permiten a y b minúsculas, sin espacios.");
            }
        }
    }
}
