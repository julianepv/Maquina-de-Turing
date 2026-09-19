package turing.modelo.maquina;

/** Desplazamiento del cabezal en una transición. */
public enum Direccion {
    L(-1),
    R(1);

    private final int desplazamiento;

    Direccion(int desplazamiento) {
        this.desplazamiento = desplazamiento;
    }

    public int getDesplazamiento() {
        return desplazamiento;
    }

    public static Direccion desdeTexto(String texto) {
        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("La dirección debe ser L o R.");
        }
    }
}
