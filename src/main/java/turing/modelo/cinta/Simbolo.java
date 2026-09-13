package turing.modelo.cinta;

/** Alfabeto de la cinta: entrada, marcas de trabajo y espacio en blanco. */
public enum Simbolo {
    A('a'),
    B('b'),
    X('X'),
    Y('Y'),
    BLANCO('□');

    private final char caracter;

    Simbolo(char caracter) {
        this.caracter = caracter;
    }

    public char getCaracter() {
        return caracter;
    }

    @Override
    public String toString() {
        return String.valueOf(caracter);
    }
}
