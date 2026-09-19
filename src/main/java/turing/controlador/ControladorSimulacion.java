package turing.controlador;

import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.Timer;
import turing.interfaz.VentanaPrincipal;
import turing.interfaz.paneles.PanelCinta;
import turing.interfaz.paneles.PanelControles;
import turing.interfaz.paneles.PanelEntrada;
import turing.interfaz.paneles.PanelEstado;
import turing.interfaz.paneles.PanelTransiciones;
import turing.modelo.maquina.Estado;
import turing.simulacion.MaquinaTuring;
import turing.simulacion.MotorSimulacion;
import turing.simulacion.ResultadoPaso;

/** Traduce los eventos de Swing en operaciones del motor y actualiza la vista. */
public final class ControladorSimulacion {
    private static final int RETARDO_SIMULACION_MS = 700;

    private final MotorSimulacion motor;
    private final PanelEntrada entrada;
    private final PanelControles controles;
    private final PanelCinta cinta;
    private final PanelEstado estado;
    private final PanelTransiciones transiciones;
    private final Consumer<String> mostrarError;
    private final Timer temporizador;
    private String ultimaTransicion = "Todavía no se ha ejecutado una transición.";

    public ControladorSimulacion(VentanaPrincipal ventana) {
        this(new MotorSimulacion(), ventana.getPanelEntrada(), ventana.getPanelControles(),
                ventana.getPanelCinta(), ventana.getPanelEstado(), ventana.getPanelTransiciones(),
                ventana::mostrarError);
        ventana.alCerrar(this::detener);
    }

    /** Recibe los paneles por separado para poder probar la interacción sin abrir una ventana. */
    public ControladorSimulacion(MotorSimulacion motor, PanelEntrada entrada,
            PanelControles controles, PanelCinta cinta, PanelEstado estado,
            PanelTransiciones transiciones, Consumer<String> mostrarError) {
        this(motor, entrada, controles, cinta, estado, transiciones, mostrarError, RETARDO_SIMULACION_MS);
    }

    ControladorSimulacion(MotorSimulacion motor, PanelEntrada entrada,
            PanelControles controles, PanelCinta cinta, PanelEstado estado,
            PanelTransiciones transiciones, Consumer<String> mostrarError, int retardoMilisegundos) {
        this.motor = Objects.requireNonNull(motor);
        this.entrada = Objects.requireNonNull(entrada);
        this.controles = Objects.requireNonNull(controles);
        this.cinta = Objects.requireNonNull(cinta);
        this.estado = Objects.requireNonNull(estado);
        this.transiciones = Objects.requireNonNull(transiciones);
        this.mostrarError = Objects.requireNonNull(mostrarError);

        // Cada tick ejecuta una sola transición en el hilo de eventos de Swing.
        // El Timer deja que la ventana responda entre pasos, sin Thread.sleep ni bucles bloqueantes.
        temporizador = new Timer(retardoMilisegundos, evento -> {
            if (temporizadorEnMarcha()) {
                realizarAccion(this::avanzar);
            }
        });
        temporizador.setCoalesce(true);
        conectarEventos();
        actualizarVista("Escribe una cadena y pulsa Cargar.");
    }

    private void conectarEventos() {
        entrada.alCargar(evento -> realizarAccion(this::cargar));
        controles.alPaso(evento -> realizarAccion(() -> {
            comprobarPuedeAvanzar();
            if (!temporizador.isRunning()) {
                avanzar();
            }
        }));
        controles.alEjecutar(evento -> realizarAccion(this::ejecutar));
        controles.alPausar(evento -> {
            detener();
            actualizarVista("Simulación pausada. Puedes avanzar un paso o continuar con Ejecutar.");
        });
        controles.alReiniciar(evento -> realizarAccion(this::reiniciar));
    }

    private void cargar() {
        detener();
        // El motor valida antes de sustituir una simulación que ya estuviera cargada.
        motor.cargar(entrada.getCadena());
        prepararInicio("Cadena cargada. Pulsa Paso o Ejecutar.");
    }

    private void ejecutar() {
        comprobarPuedeAvanzar();
        if (!temporizador.isRunning()) {
            temporizador.start();
            actualizarVista("Ejecución automática en curso.");
        }
    }

    private void avanzar() {
        ResultadoPaso paso = motor.paso();
        ultimaTransicion = paso.notacion();
        transiciones.agregar(paso);

        String mensaje;
        if (paso.estadoNuevo() == Estado.ACEPTAR) {
            detener();
            mensaje = "Cadena aceptada: pertenece a L = {aⁿbⁿ | n ≥ 1}.";
        } else if (paso.estadoNuevo() == Estado.RECHAZAR) {
            detener();
            mensaje = "Cadena rechazada: no existe una transición para δ("
                    + paso.estadoAnterior() + ", " + paso.simboloLeido() + ").";
        } else {
            mensaje = temporizador.isRunning()
                    ? "Ejecución automática en curso."
                    : "Paso completado. Puedes avanzar otra transición o pulsar Ejecutar.";
        }
        actualizarVista(mensaje);
    }

    private void reiniciar() {
        detener();
        motor.reiniciar();
        entrada.setCadena(motor.getCadenaInicial());
        prepararInicio("Simulación reiniciada con la cadena original. La tabla de transiciones está vacía.");
    }

    private void prepararInicio(String mensaje) {
        ultimaTransicion = "Todavía no se ha ejecutado una transición.";
        transiciones.limpiar();
        actualizarVista(mensaje);
        entrada.enfocarEntrada();
    }

    private void comprobarPuedeAvanzar() {
        if (!motor.hayCadenaCargada()) {
            throw new IllegalStateException("Primero escribe una cadena y pulsa Cargar.");
        }
        if (motor.getMaquina().haTerminado()) {
            throw new IllegalStateException("La simulación terminó. Pulsa Reiniciar o carga otra cadena.");
        }
    }

    private void actualizarVista(String mensaje) {
        boolean cargada = motor.hayCadenaCargada();
        boolean terminada = cargada && motor.getMaquina().haTerminado();
        entrada.setEdicionHabilitada(!temporizador.isRunning());
        controles.actualizarDisponibilidad(cargada, temporizador.isRunning(), terminada);

        if (!cargada) {
            cinta.limpiar();
            estado.limpiar();
            return;
        }

        MaquinaTuring maquina = motor.getMaquina();
        cinta.mostrar(maquina.getCinta());
        estado.mostrar(maquina.getEstadoActual(), maquina.getNumeroPasos(),
                maquina.getCinta().getPosicionCabezal(), ultimaTransicion, mensaje, motor.getCadenaInicial());
    }

    private void realizarAccion(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            detener();
            actualizarVista(ex.getMessage());
            mostrarError.accept(ex.getMessage());
        } catch (RuntimeException ex) {
            detener();
            String mensaje = "No se pudo completar la operación. Reinicia la simulación o carga otra cadena.";
            actualizarVista(mensaje);
            mostrarError.accept(mensaje);
            ex.printStackTrace();
        }
    }

    private boolean temporizadorEnMarcha() {
        return temporizador.isRunning();
    }

    /** Detiene también los ticks pendientes al cerrar la ventana. Se llama desde el hilo de Swing. */
    public void detener() {
        temporizador.stop();
    }

    int getRetardoMilisegundos() {
        return temporizador.getDelay();
    }
}
