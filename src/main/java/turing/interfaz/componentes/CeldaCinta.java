package turing.interfaz.componentes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import turing.modelo.cinta.Simbolo;

/** Dibuja una celda reutilizable: símbolo, índice y señal del cabezal. */
public final class CeldaCinta {
    public static final int ANCHO = 58;
    public static final int ALTO = 104;
    private static final Font FUENTE_SIMBOLO = new Font(Font.MONOSPACED, Font.BOLD, 26);
    private static final Font FUENTE_INDICE = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Color AZUL = new Color(29, 78, 137);

    public void pintar(Graphics2D grafico, int x, int y, int indice, Simbolo simbolo, boolean cabezal) {
        int cajaY = y + 29;
        int cajaX = x + 3;
        int lado = ANCHO - 6;
        boolean marcada = simbolo == Simbolo.X || simbolo == Simbolo.Y;
        Color fondo = cabezal ? new Color(220, 237, 255)
                : marcada ? new Color(232, 245, 235) : Color.WHITE;

        grafico.setColor(fondo);
        grafico.fillRoundRect(cajaX, cajaY, lado, 50, 8, 8);
        grafico.setColor(cabezal ? AZUL : new Color(167, 177, 189));
        grafico.drawRoundRect(cajaX, cajaY, lado, 50, 8, 8);
        if (cabezal) {
            grafico.drawRoundRect(cajaX + 1, cajaY + 1, lado - 2, 48, 8, 8);
            int centroX = x + ANCHO / 2;
            grafico.fillRect(centroX - 2, y + 2, 4, 13);
            grafico.fillPolygon(new Polygon(
                    new int[] {centroX - 9, centroX + 9, centroX},
                    new int[] {y + 13, y + 13, y + 25}, 3));
        }

        grafico.setFont(FUENTE_SIMBOLO);
        grafico.setColor(cabezal ? AZUL : new Color(30, 41, 59));
        dibujarCentrado(grafico, simbolo.toString(), x + ANCHO / 2, cajaY + 34);
        grafico.setFont(FUENTE_INDICE);
        grafico.setColor(cabezal ? AZUL : new Color(97, 109, 126));
        dibujarCentrado(grafico, Integer.toString(indice), x + ANCHO / 2, cajaY + 70);
    }

    private static void dibujarCentrado(Graphics2D grafico, String texto, int centroX, int lineaBase) {
        FontMetrics medidas = grafico.getFontMetrics();
        grafico.drawString(texto, centroX - medidas.stringWidth(texto) / 2, lineaBase);
    }
}
