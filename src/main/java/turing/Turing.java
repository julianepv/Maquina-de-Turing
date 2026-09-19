package turing;

import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import turing.controlador.ControladorSimulacion;
import turing.interfaz.VentanaPrincipal;

/**
 * Punto de entrada de la aplicación.
 */
public final class Turing {

    private Turing() {}

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println(
                "La simulación requiere un entorno gráfico para abrir Java Swing."
            );
            return;
        }

        /*
         * Utiliza el Look & Feel clásico de Java Swing.
         * Esto evita que Fedora aplique el estilo GTK del sistema.
         */
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            System.err.println(
                "No se pudo aplicar el Look & Feel de Java Swing."
            );
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();

            new ControladorSimulacion(ventana);

            ventana.setVisible(true);

            ventana.getPanelEntrada().enfocarEntrada();
        });
    }
}
