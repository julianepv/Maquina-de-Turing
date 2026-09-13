package turing.interfaz.componentes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.swing.UIManager;
import turing.modelo.cinta.Simbolo;

/** Dibuja una celda reutilizable: símbolo, índice y señal del cabezal. */
public final class CeldaCinta {
    public static final int ANCHO = 64;
    public static final int ALTO = 112;
    private final Font fuenteSimbolo = UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 28f);
    private final Font fuenteIndice = UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 12f);
    private final Color texto = UIManager.getColor("Label.foreground");
    private final Color fondo = UIManager.getColor("TextField.background");
    private final Color fondoMarcada = new Color(
            (fondo.getRed() * 9 + texto.getRed()) / 10,
            (fondo.getGreen() * 9 + texto.getGreen()) / 10,
            (fondo.getBlue() * 9 + texto.getBlue()) / 10);
    private final Color borde = new Color(
            (fondo.getRed() * 2 + texto.getRed()) / 3,
            (fondo.getGreen() * 2 + texto.getGreen()) / 3,
            (fondo.getBlue() * 2 + texto.getBlue()) / 3);

    public void pintar(Graphics2D grafico, int x, int y, int indice, Simbolo simbolo, boolean cabezal) {
        int cajaY = y + 30;
        int cajaX = x + 4;
        int lado = ANCHO - 8;
        boolean marcada = simbolo == Simbolo.X || simbolo == Simbolo.Y;
        grafico.setColor(marcada ? fondoMarcada : fondo);
        grafico.fillRect(cajaX, cajaY, lado, lado);
        grafico.setColor(cabezal ? texto : borde);
        grafico.drawRect(cajaX, cajaY, lado, lado);

        // Flecha y doble trazo: el cabezal se reconoce también sin color.
        if (cabezal) {
            grafico.drawRect(cajaX + 1, cajaY + 1, lado - 2, lado - 2);
            int centroX = x + ANCHO / 2;
            grafico.fillRect(centroX - 1, y + 2, 2, 12);
            grafico.fillPolygon(new Polygon(
                    new int[] {centroX - 9, centroX + 9, centroX},
                    new int[] {y + 13, y + 13, y + 25}, 3));
        }

        grafico.setFont(fuenteSimbolo);
        grafico.setColor(texto);
        FontMetrics medidas = grafico.getFontMetrics();
        dibujarCentrado(grafico, simbolo.toString(), x + ANCHO / 2,
                cajaY + (lado - medidas.getHeight()) / 2 + medidas.getAscent());

        grafico.setFont(fuenteIndice);
        dibujarCentrado(grafico, Integer.toString(indice), x + ANCHO / 2, cajaY + lado + 20);
    }

    private static void dibujarCentrado(Graphics2D grafico, String texto, int centroX, int lineaBase) {
        FontMetrics medidas = grafico.getFontMetrics();
        grafico.drawString(texto, centroX - medidas.stringWidth(texto) / 2, lineaBase);
    }
}
