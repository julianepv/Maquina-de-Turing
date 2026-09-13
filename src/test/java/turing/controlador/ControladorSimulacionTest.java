package turing.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import turing.interfaz.paneles.PanelCinta;
import turing.interfaz.paneles.PanelControles;
import turing.interfaz.paneles.PanelEntrada;
import turing.interfaz.paneles.PanelEstado;
import turing.interfaz.paneles.PanelHistorial;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Estado;
import turing.simulacion.MaquinaTuring;
import turing.simulacion.MotorSimulacion;

/** Integra Swing y el motor en el EDT, sin crear JFrame ni necesitar un escritorio. */
class ControladorSimulacionTest {
    private MotorSimulacion motor;
    private PanelEntrada entrada;
    private PanelControles controles;
    private PanelEstado estado;
    private JTable tabla;
    private ControladorSimulacion controlador;
    private List<String> errores;

    @BeforeEach
    void prepararPanelesEnElHiloDeSwing() throws Exception {
        enSwing(() -> {
            motor = new MotorSimulacion();
            entrada = new PanelEntrada();
            controles = new PanelControles();
            estado = new PanelEstado();
            PanelHistorial historial = new PanelHistorial();
            tabla = unico(historial, JTable.class);
            errores = new ArrayList<>();
            controlador = new ControladorSimulacion(motor, entrada, controles,
                    new PanelCinta(), estado, historial, errores::add);
        });
    }

    @AfterEach
    void detenerElTemporizadorEnElHiloDeSwing() throws Exception {
        enSwing(() -> {
            if (controlador != null) {
                controlador.detener();
            }
        });
    }

    @Test
    void alIniciarSoloPermiteCargarUnaCadena() throws Exception {
        enSwing(() -> {
            assertTrue(boton(entrada, "Cargar cadena").isEnabled());
            assertTrue(unico(entrada, JTextField.class).isEditable());
            assertFalse(boton(controles, "Paso").isEnabled());
            assertFalse(boton(controles, "Ejecutar").isEnabled());
            assertFalse(boton(controles, "Pausar").isEnabled());
            assertFalse(boton(controles, "Reiniciar").isEnabled());
            assertFalse(motor.hayCadenaCargada());
            assertEquals(0, tabla.getRowCount());

            boton(entrada, "Cargar cadena").doClick(0);
            assertEquals(1, errores.size());
            assertFalse(errores.get(0).isBlank());
            assertFalse(motor.hayCadenaCargada());
        });
    }

    @Test
    void enterCargaLaCadenaYPasoActualizaElMotorYUnaFilaDelHistorial() throws Exception {
        enSwing(() -> {
            entrada.setCadena("aabb");
            unico(entrada, JTextField.class).postActionEvent();

            assertTrue(motor.hayCadenaCargada());
            assertEquals("aabb", motor.getCadenaInicial());
            assertTrue(boton(controles, "Paso").isEnabled());
            assertTrue(boton(controles, "Ejecutar").isEnabled());
            assertFalse(boton(controles, "Pausar").isEnabled());
            assertEquals(0, tabla.getRowCount());

            boton(controles, "Paso").doClick(0);

            assertEquals(1L, motor.getMaquina().getNumeroPasos());
            assertEquals(Estado.Q1, motor.getMaquina().getEstadoActual());
            assertEquals(Simbolo.X, motor.getMaquina().getCinta().leer(0));
            assertEquals(1, motor.getMaquina().getCinta().getPosicionCabezal());
            assertEquals(1, tabla.getRowCount());
            assertEquals(1L, tabla.getValueAt(0, tabla.getColumn("Paso").getModelIndex()));
            assertFalse(tabla.isCellEditable(0, 0));
            assertEquals(motor.getHistorial().getPasos().get(0).notacion(),
                    unico(estado, JTextField.class).getText());
            assertTrue(componentes(estado, JLabel.class).stream()
                    .anyMatch(etiqueta -> etiqueta.getText().equals("Estado actual: q1")));
            assertTrue(errores.isEmpty());
        });
    }

    @Test
    void cargarCaracteresInvalidosMuestraUnErrorYConservaLaEjecucion() throws Exception {
        enSwing(() -> {
            cargar("ab");
            boton(controles, "Paso").doClick(0);
            MaquinaTuring maquinaAnterior = motor.getMaquina();

            entrada.setCadena("aabbc");
            boton(entrada, "Cargar cadena").doClick(0);

            assertEquals(1, errores.size());
            assertFalse(errores.get(0).isBlank());
            assertSame(maquinaAnterior, motor.getMaquina());
            assertEquals("ab", motor.getCadenaInicial());
            assertEquals(1L, motor.getMaquina().getNumeroPasos());
            assertEquals(1, tabla.getRowCount());
            assertEquals(1, motor.getHistorial().getPasos().size());
            assertTrue(boton(controles, "Paso").isEnabled());
            assertTrue(unico(entrada, JTextField.class).isEditable());
        });
    }

    @Test
    void reiniciarRestauraLaEntradaOriginalYVacíaElHistorialVisual() throws Exception {
        enSwing(() -> {
            cargar("aabb");
            boton(controles, "Paso").doClick(0);
            boton(controles, "Paso").doClick(0);
            entrada.setCadena("otra entrada sin cargar");

            boton(controles, "Reiniciar").doClick(0);

            comprobarReinicio("aabb");
            assertTrue(errores.isEmpty());
        });
    }

    @Test
    void ejecutarAvanzaConElTimerYSeDetieneAlAceptar() throws Exception {
        CountDownLatch terminada = new CountDownLatch(1);
        enSwing(() -> {
            cargar("aabb");
            observarFinalizacion(terminada);
            unico(controles, JSlider.class).setValue(50);
            assertEquals(50, controles.getRetardoMilisegundos());

            boton(controles, "Ejecutar").doClick(0);

            assertTrue(boton(controles, "Pausar").isEnabled());
            assertFalse(boton(controles, "Paso").isEnabled());
            assertFalse(boton(controles, "Ejecutar").isEnabled());
            assertFalse(boton(entrada, "Cargar cadena").isEnabled());
            assertFalse(unico(entrada, JTextField.class).isEditable());

            MaquinaTuring maquinaEnEjecucion = motor.getMaquina();
            unico(entrada, JTextField.class).postActionEvent();
            assertSame(maquinaEnEjecucion, motor.getMaquina(), "Enter no debe recargar durante la ejecución");
            assertTrue(boton(controles, "Pausar").isEnabled());
        });

        // Se espera una señal del modelo, sin bloquear el EDT ni dormir un tiempo fijo.
        assertTrue(terminada.await(10, TimeUnit.SECONDS), "El Timer no completó la simulación");
        enSwing(() -> {
            assertEquals(Estado.ACEPTAR, motor.getMaquina().getEstadoActual());
            assertTrue(motor.getMaquina().getNumeroPasos() > 1);
            assertEquals(motor.getMaquina().getNumeroPasos(), tabla.getRowCount());
            assertFalse(boton(controles, "Paso").isEnabled());
            assertFalse(boton(controles, "Ejecutar").isEnabled());
            assertFalse(boton(controles, "Pausar").isEnabled());
            assertTrue(boton(controles, "Reiniciar").isEnabled());
            assertTrue(unico(entrada, JTextField.class).isEditable());
            assertTrue(componentes(estado, JTextComponent.class).stream()
                    .filter(campo -> "Resultado de la simulación".equals(
                            campo.getAccessibleContext().getAccessibleName()))
                    .anyMatch(campo -> campo.getText().contains("Cadena aceptada")));
            assertTrue(errores.isEmpty());
        });
    }

    @Test
    void pausarConservaElPasoActualYPermiteContinuarHastaAceptar() throws Exception {
        CountDownLatch pausada = new CountDownLatch(1);
        CountDownLatch terminada = new CountDownLatch(1);
        long[] pasosAntesDePausar = new long[1];
        Estado[] estadoAntesDePausar = new Estado[1];
        enSwing(() -> {
            cargar("aaabbb");
            observarFinalizacion(terminada);
            despuesDelPrimerPaso(() -> {
                pasosAntesDePausar[0] = motor.getMaquina().getNumeroPasos();
                estadoAntesDePausar[0] = motor.getMaquina().getEstadoActual();
                boton(controles, "Pausar").doClick(0);
                pausada.countDown();
            });
            unico(controles, JSlider.class).setValue(50);
            boton(controles, "Ejecutar").doClick(0);
        });

        assertTrue(pausada.await(10, TimeUnit.SECONDS), "No se recibió el primer paso automático");
        enSwing(() -> {
            assertTrue(pasosAntesDePausar[0] > 0);
            assertEquals(pasosAntesDePausar[0], motor.getMaquina().getNumeroPasos());
            assertEquals(estadoAntesDePausar[0], motor.getMaquina().getEstadoActual());
            assertEquals(pasosAntesDePausar[0], tabla.getRowCount());
            assertFalse(boton(controles, "Pausar").isEnabled());
            assertTrue(boton(controles, "Paso").isEnabled());
            assertTrue(boton(controles, "Ejecutar").isEnabled());

            boton(controles, "Paso").doClick(0);
            assertEquals(pasosAntesDePausar[0] + 1, motor.getMaquina().getNumeroPasos());
            boton(controles, "Ejecutar").doClick(0);
        });

        assertTrue(terminada.await(10, TimeUnit.SECONDS), "No continuó la ejecución después de pausar");
        enSwing(() -> {
            assertEquals(Estado.ACEPTAR, motor.getMaquina().getEstadoActual());
            assertEquals(motor.getMaquina().getNumeroPasos(), tabla.getRowCount());
            assertTrue(errores.isEmpty());
        });
    }

    @Test
    void reiniciarDuranteLaEjecucionAutomaticaDetieneElTimerYRestableceLaVista() throws Exception {
        CountDownLatch reiniciada = new CountDownLatch(1);
        enSwing(() -> {
            cargar("aaabbb");
            despuesDelPrimerPaso(() -> {
                boton(controles, "Reiniciar").doClick(0);
                reiniciada.countDown();
            });
            unico(controles, JSlider.class).setValue(50);
            boton(controles, "Ejecutar").doClick(0);
        });

        assertTrue(reiniciada.await(10, TimeUnit.SECONDS), "No se pudo reiniciar durante la ejecución");
        enSwing(() -> {
            comprobarReinicio("aaabbb");
            assertTrue(errores.isEmpty());
        });
    }

    private void cargar(String cadena) {
        entrada.setCadena(cadena);
        boton(entrada, "Cargar cadena").doClick(0);
    }

    private void comprobarReinicio(String cadena) {
        assertEquals(cadena, entrada.getCadena());
        assertEquals(cadena, motor.getCadenaInicial());
        assertEquals(Estado.Q0, motor.getMaquina().getEstadoActual());
        assertEquals(0L, motor.getMaquina().getNumeroPasos());
        assertEquals(0, motor.getMaquina().getCinta().getPosicionCabezal());
        assertEquals(Simbolo.A, motor.getMaquina().getCinta().leer());
        assertEquals(0, tabla.getRowCount());
        assertTrue(motor.getHistorial().getPasos().isEmpty());
        assertFalse(boton(controles, "Pausar").isEnabled());
        assertTrue(boton(controles, "Paso").isEnabled());
        assertTrue(boton(controles, "Ejecutar").isEnabled());
        assertTrue(unico(entrada, JTextField.class).isEditable());
    }

    private void observarFinalizacion(CountDownLatch terminada) {
        tabla.getModel().addTableModelListener(evento -> {
            if (motor.hayCadenaCargada() && motor.getMaquina().haTerminado()) {
                terminada.countDown();
            }
        });
    }

    private void despuesDelPrimerPaso(Runnable accion) {
        AtomicBoolean pendiente = new AtomicBoolean(true);
        tabla.getModel().addTableModelListener(evento -> {
            if (evento.getType() == TableModelEvent.INSERT && pendiente.compareAndSet(true, false)) {
                // Ejecutar la acción después de que el controlador haya terminado de actualizar la vista.
                SwingUtilities.invokeLater(accion);
            }
        });
    }

    private static JButton boton(Container raiz, String texto) {
        return componentes(raiz, JButton.class).stream()
                .filter(boton -> texto.equals(boton.getText()))
                .findFirst().orElseThrow(() -> new AssertionError("No se encontró el botón: " + texto));
    }

    private static <T extends Component> T unico(Container raiz, Class<T> tipo) {
        List<T> encontrados = componentes(raiz, tipo);
        assertEquals(1, encontrados.size(), "Se esperaba un componente de tipo " + tipo.getSimpleName());
        return encontrados.get(0);
    }

    private static <T extends Component> List<T> componentes(Container raiz, Class<T> tipo) {
        List<T> encontrados = new ArrayList<>();
        for (Component componente : raiz.getComponents()) {
            if (tipo.isInstance(componente)) {
                encontrados.add(tipo.cast(componente));
            }
            if (componente instanceof Container contenedor) {
                encontrados.addAll(componentes(contenedor, tipo));
            }
        }
        return encontrados;
    }

    private static void enSwing(Runnable accion) throws Exception {
        try {
            SwingUtilities.invokeAndWait(accion);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof Error error) {
                throw error;
            }
            if (ex.getCause() instanceof Exception excepcion) {
                throw excepcion;
            }
            throw ex;
        }
    }
}
