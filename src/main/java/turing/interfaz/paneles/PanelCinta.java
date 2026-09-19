package turing.interfaz.paneles;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import turing.interfaz.componentes.CeldaCinta;
import turing.modelo.cinta.Cinta;
import turing.modelo.cinta.Simbolo;

/** Muestra la cinta, sus índices y la posición actual del cabezal. */
public final class PanelCinta extends JPanel {

    private static final int MAX_CELDAS = 15;
    private static final int ANCHO_PANEL = 945;
    private static final int Y_FLECHA = 27;
    private static final int Y_CELDA = 48;
    private static final int Y_INDICE = 104;

    private final JLabel titulo = new JLabel("Cinta");
    private final JLabel flechaCabezal = new JLabel("▼", SwingConstants.CENTER);
    private final CeldaCinta[] celdas = new CeldaCinta[MAX_CELDAS];
    private final JLabel[] indices = new JLabel[MAX_CELDAS];
    private final JLabel leyenda = new JLabel(
        "X = a procesada      Y = b procesada      B = blanco",
        SwingConstants.CENTER
    );

    public PanelCinta() {
        setLayout(null);
        setBackground(Color.WHITE);
        setBorder(new LineBorder(Color.BLUE, 1));
        getAccessibleContext().setAccessibleName(
            "Cinta y posición del cabezal"
        );

        titulo.setBounds(10, 5, 120, 22);
        titulo.setFont(new Font("Tahoma", Font.BOLD, 14));
        titulo.setForeground(Color.BLACK);
        add(titulo);

        flechaCabezal.setFont(new Font("Arial", Font.BOLD, 18));
        flechaCabezal.setForeground(Color.GREEN);
        flechaCabezal.setVisible(false);
        add(flechaCabezal);

        Font fuenteIndice = new Font("Arial", Font.PLAIN, 11);
        for (int i = 0; i < MAX_CELDAS; i++) {
            CeldaCinta celda = new CeldaCinta();
            celdas[i] = celda;
            add(celda);

            JLabel indice = new JLabel("", SwingConstants.CENTER);
            indice.setFont(fuenteIndice);
            indice.setForeground(new Color(75, 75, 75));
            indices[i] = indice;
            add(indice);
        }

        leyenda.setBounds(0, 132, ANCHO_PANEL, 22);
        leyenda.setFont(new Font("Arial", Font.PLAIN, 12));
        leyenda.setForeground(new Color(55, 55, 55));
        add(leyenda);

        limpiar();
    }

    public void mostrar(Cinta cinta) {
        if (cinta == null) {
            limpiar();
            return;
        }

        getAccessibleContext().setAccessibleDescription(
            "Cabezal en la celda " +
                cinta.getPosicionCabezal() +
                ", símbolo leído: " +
                cinta.leer(cinta.getPosicionCabezal())
        );

        int limiteIzq = cinta.getLimiteIzquierdo();
        int limiteDer = cinta.getLimiteDerecho();
        int minIdx = Math.min(-1, limiteIzq - 1);
        int maxIdx = Math.max(7, limiteDer + 1);
        int totalNecesarias = maxIdx - minIdx + 1;

        int numCeldas;
        int inicioIndice;

        if (totalNecesarias <= MAX_CELDAS) {
            numCeldas = Math.max(9, totalNecesarias);
            inicioIndice = minIdx;
        } else {
            numCeldas = MAX_CELDAS;
            int cabezal = cinta.getPosicionCabezal();
            inicioIndice = cabezal - 7;
            if (inicioIndice < minIdx) {
                inicioIndice = minIdx;
            }
            if (inicioIndice + MAX_CELDAS - 1 > maxIdx) {
                inicioIndice = maxIdx - MAX_CELDAS + 1;
            }
        }

        dibujar(cinta, inicioIndice, numCeldas);
    }

    public void limpiar() {
        getAccessibleContext().setAccessibleDescription(
            "No hay una cadena cargada."
        );
        flechaCabezal.setVisible(false);

        int numCeldas = 9;
        int inicioIndice = -1;
        int anchoTotal = anchoTotal(numCeldas);
        int startX = (ANCHO_PANEL - anchoTotal) / 2;

        for (int i = 0; i < MAX_CELDAS; i++) {
            if (i < numCeldas) {
                int indiceCinta = inicioIndice + i;
                int x = startX + i * (CeldaCinta.ANCHO + CeldaCinta.ESPACIADO);
                posicionarCelda(i, x, indiceCinta, Simbolo.BLANCO, false);
            } else {
                celdas[i].setVisible(false);
                indices[i].setVisible(false);
            }
        }
        repaint();
    }

    private void dibujar(Cinta cinta, int inicioIndice, int numCeldas) {
        int startX = (ANCHO_PANEL - anchoTotal(numCeldas)) / 2;
        boolean flechaPosicionada = false;

        for (int i = 0; i < MAX_CELDAS; i++) {
            if (i < numCeldas) {
                int indiceCinta = inicioIndice + i;
                int x = startX + i * (CeldaCinta.ANCHO + CeldaCinta.ESPACIADO);
                boolean esCabezal = indiceCinta == cinta.getPosicionCabezal();
                posicionarCelda(
                    i,
                    x,
                    indiceCinta,
                    cinta.leer(indiceCinta),
                    esCabezal
                );

                if (esCabezal) {
                    flechaCabezal.setBounds(x + 12, Y_FLECHA, 30, 20);
                    flechaCabezal.setVisible(true);
                    flechaPosicionada = true;
                }
            } else {
                celdas[i].setVisible(false);
                indices[i].setVisible(false);
            }
        }

        if (!flechaPosicionada) {
            flechaCabezal.setVisible(false);
        }
        repaint();
    }

    private void posicionarCelda(
        int indiceVisual,
        int x,
        int indiceCinta,
        Simbolo simbolo,
        boolean cabezal
    ) {
        celdas[indiceVisual].setBounds(
            x,
            Y_CELDA,
            CeldaCinta.ANCHO,
            CeldaCinta.ALTO
        );
        celdas[indiceVisual].actualizar(simbolo, cabezal);
        celdas[indiceVisual].setVisible(true);

        indices[indiceVisual].setBounds(
            x - 3,
            Y_INDICE,
            CeldaCinta.ANCHO + 6,
            18
        );
        indices[indiceVisual].setText(String.valueOf(indiceCinta));
        indices[indiceVisual].setVisible(true);
    }

    private int anchoTotal(int numCeldas) {
        return (
            numCeldas * CeldaCinta.ANCHO +
            (numCeldas - 1) * CeldaCinta.ESPACIADO
        );
    }
}
