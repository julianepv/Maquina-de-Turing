package turing.modelo.maquina;

public enum Estado {
    Q0("q0", "Buscar la siguiente a sin marcar."),
    Q1("q1", "Buscar una b que corresponda a la a marcada."),
    Q2("q2", "Regresar hacia la última a marcada como X."),
    Q3("q3", "Comprobar que únicamente quedan marcas Y."),
    ACEPTAR("ACEPTAR", "Todas las parejas a-b se procesaron correctamente."),
    RECHAZAR("RECHAZAR", "La cadena no pertenece al lenguaje aⁿbⁿ con n ≥ 1.");

    private final String nombre;
    private final String descripcion;

    Estado(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public boolean esFinal() {
        return this == ACEPTAR || this == RECHAZAR;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
