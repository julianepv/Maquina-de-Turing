package turing.simulacion;

import turing.validacion.ValidadorCadena;
import turing.reglas.ConfiguracionMaquina;

/** Coordina carga, reinicio e historial sin depender de la interfaz Swing. */
public final class MotorSimulacion {
    private final HistorialEjecucion historial = new HistorialEjecucion();
    private MaquinaTuring maquina;
    private String cadenaInicial = "";
    private ConfiguracionMaquina configuracion;

    public void cargar(String cadena) {
        // Una entrada inválida conserva intacta la simulación que estaba cargada.
        ValidadorCadena.validar(cadena);
        MaquinaTuring nuevaMaquina = new MaquinaTuring(cadena);
        maquina = nuevaMaquina;
        cadenaInicial = cadena;
        historial.limpiar();
    }

    /** Carga una cinta junto con las reglas definidas en la interfaz. */
    public void cargar(String cadena, ConfiguracionMaquina nuevaConfiguracion) {
        ValidadorCadena.validar(cadena);
        if (nuevaConfiguracion == null) {
            throw new IllegalArgumentException("La configuración de transiciones es obligatoria.");
        }
        MaquinaTuring nuevaMaquina = new MaquinaTuring(cadena, nuevaConfiguracion.tabla(),
                nuevaConfiguracion.estadoInicial());
        maquina = nuevaMaquina;
        configuracion = nuevaConfiguracion;
        cadenaInicial = cadena;
        historial.limpiar();
    }

    public ResultadoPaso paso() {
        ResultadoPaso resultado = getMaquina().paso();
        historial.agregar(resultado);
        return resultado;
    }

    public void reiniciar() {
        getMaquina();
        if (configuracion == null) {
            cargar(cadenaInicial);
        } else {
            cargar(cadenaInicial, configuracion);
        }
    }

    public void limpiar() {
        maquina = null;
        cadenaInicial = "";
        configuracion = null;
        historial.limpiar();
    }

    public boolean hayCadenaCargada() {
        return maquina != null;
    }

    public MaquinaTuring getMaquina() {
        if (!hayCadenaCargada()) {
            throw new IllegalStateException("Primero ingresa una cadena y pulsa Cargar cadena.");
        }
        return maquina;
    }

    public HistorialEjecucion getHistorial() {
        return historial;
    }

    public String getCadenaInicial() {
        return cadenaInicial;
    }
}
