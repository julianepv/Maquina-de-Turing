package turing.interfaz.componentes;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import turing.modelo.cinta.Simbolo;

/** Representa visualmente una celda individual de la cinta. */
public final class CeldaCinta extends JPanel {

    public static final int ANCHO = 54;
    public static final int ALTO = 54;
    public static final int ESPACIADO = 3;

    private static final Color FONDO_NORMAL = Color.WHITE;
    private static final Color FONDO_MARCADA = new Color(240, 248, 255);
    private static final Color BORDE_NORMAL = new Color(145, 145, 145);
    private static final Color BORDE_MARCADA = new Color(95, 135, 175);
    private static final Color BORDE_CABEZAL = Color.RED;

    private static final Font FUENTE_NORMAL = new Font("Arial", Font.PLAIN, 23);
    private static final Font FUENTE_MARCADA = new Font("Arial", Font.BOLD, 23);

    private final JLabel etiquetaSimbolo = new JLabel(
        "",
        SwingConstants.CENTER
    );

    public CeldaCinta() {
        setLayout(null);
        setBackground(FONDO_NORMAL);
        setBorder(BorderFactory.createLineBorder(BORDE_NORMAL, 1));
        setSize(ANCHO, ALTO);

        etiquetaSimbolo.setBounds(0, 0, ANCHO, ALTO);
        etiquetaSimbolo.setFont(FUENTE_NORMAL);
        etiquetaSimbolo.setForeground(Color.BLACK);
        add(etiquetaSimbolo);
    }

    public void actualizar(Simbolo simbolo, boolean cabezal) {
        String texto =
            simbolo == null || simbolo == Simbolo.BLANCO
                ? "B"
                : simbolo.toString();
        etiquetaSimbolo.setText(texto);

        boolean marcada = simbolo == Simbolo.X || simbolo == Simbolo.Y;
        setBackground(marcada ? FONDO_MARCADA : FONDO_NORMAL);
        etiquetaSimbolo.setFont(marcada ? FUENTE_MARCADA : FUENTE_NORMAL);

        if (cabezal) {
            setBorder(BorderFactory.createLineBorder(BORDE_CABEZAL, 3));
        } else if (marcada) {
            setBorder(BorderFactory.createLineBorder(BORDE_MARCADA, 1));
        } else {
            setBorder(BorderFactory.createLineBorder(BORDE_NORMAL, 1));
        }
    }
}
