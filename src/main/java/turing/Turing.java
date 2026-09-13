package turing;

import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import turing.controlador.ControladorSimulacion;
import turing.interfaz.VentanaPrincipal;

/** Punto de entrada: prepara Swing y conecta la ventana con el controlador. */
public final class Turing {
    private Turing() {
    }

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("La simulación requiere un entorno gráfico para abrir Java Swing.");
            System.err.println("Ejecuta java -jar target/turing.jar desde una sesión de escritorio.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ex) {
                // El aspecto predeterminado de Swing permite continuar normalmente.
                System.err.println("Se utilizará el aspecto predeterminado de Swing: " + ex.getMessage());
            }

            VentanaPrincipal ventana = new VentanaPrincipal();
            new ControladorSimulacion(ventana);
            ventana.setVisible(true);
            ventana.getPanelEntrada().enfocarEntrada();
        });
    }
}
