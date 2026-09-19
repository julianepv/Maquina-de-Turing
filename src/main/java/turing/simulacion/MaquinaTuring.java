package turing.simulacion;

import java.util.Objects;
import turing.modelo.cinta.Cinta;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Estado;
import turing.modelo.maquina.Transicion;
import turing.reglas.ConfiguracionAnBn;
import turing.reglas.TablaTransiciones;

/** Ejecuta δ sobre la cinta; no cuenta símbolos ni decide mediante el String. */
public final class MaquinaTuring {
    private final Cinta cinta;
    private final TablaTransiciones tabla;
    private Estado estadoActual = Estado.Q0;
    private long numeroPasos;

    public MaquinaTuring(String cadena) {
        this(cadena, ConfiguracionAnBn.crearTabla());
    }

    public MaquinaTuring(String cadena, TablaTransiciones tabla) {
        this(cadena, tabla, Estado.Q0);
    }

    public MaquinaTuring(String cadena, TablaTransiciones tabla, Estado estadoInicial) {
        this.cinta = new Cinta(cadena);
        this.tabla = Objects.requireNonNull(tabla, "La tabla de transiciones es obligatoria.");
        this.estadoActual = Objects.requireNonNull(estadoInicial, "El estado inicial es obligatorio.");
        if (estadoInicial.esFinal()) {
            throw new IllegalArgumentException("El estado inicial no puede ser final.");
        }
    }

    public ResultadoPaso paso() {
        if (haTerminado()) {
            throw new IllegalStateException("La máquina ya terminó. Reinicia o carga otra cadena.");
        }

        Estado estadoAnterior = estadoActual;
        int posicionAnterior = cinta.getPosicionCabezal();
        Simbolo simboloLeido = cinta.leer();
        Transicion transicion = tabla.buscar(estadoActual, simboloLeido).orElse(null);
        numeroPasos++;

        if (transicion == null) {
            // δ es parcial: al faltar una regla, la máquina se detiene y rechaza.
            estadoActual = Estado.RECHAZAR;
            return new ResultadoPaso(numeroPasos, estadoAnterior, simboloLeido,
                    simboloLeido, null, estadoActual, posicionAnterior, posicionAnterior);
        }

        cinta.escribir(transicion.simboloEscrito());
        estadoActual = transicion.estadoSiguiente();
        cinta.mover(transicion.direccion());
        return new ResultadoPaso(numeroPasos, estadoAnterior, simboloLeido,
                transicion.simboloEscrito(), transicion.direccion(), estadoActual,
                posicionAnterior, cinta.getPosicionCabezal());
    }

    public Cinta getCinta() {
        return cinta;
    }

    public Estado getEstadoActual() {
        return estadoActual;
    }

    public long getNumeroPasos() {
        return numeroPasos;
    }

    public boolean haTerminado() {
        return estadoActual.esFinal();
    }
}
