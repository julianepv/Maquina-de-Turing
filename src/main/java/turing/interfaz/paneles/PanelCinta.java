package turing.interfaz.paneles;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import turing.interfaz.componentes.CeldaCinta;
import turing.modelo.cinta.Cinta;
import turing.modelo.cinta.Simbolo;

/** Visualiza la cinta sin crear un componente por símbolo ni copiarla en cada paso. */
public final class PanelCinta extends JPanel {
    private final VistaCinta vista = new VistaCinta();
    private final JScrollPane desplazamiento = new JScrollPane(vista,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private Cinta cinta;

    public PanelCinta() {
        super(new BorderLayout(0, 8));
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getViewport().setBackground(getBackground());
        desplazamiento.getHorizontalScrollBar().setUnitIncrement(CeldaCinta.ANCHO);
        int alto = CeldaCinta.ALTO + 24 + desplazamiento.getHorizontalScrollBar().getPreferredSize().height;
        desplazamiento.setPreferredSize(new Dimension(760, alto));
        add(desplazamiento, BorderLayout.CENTER);

        JLabel leyenda = new JLabel("X = a procesada    Y = b procesada    □ = blanco", JLabel.CENTER);
        leyenda.setFont(leyenda.getFont().deriveFont(Font.PLAIN, 12f));
        add(leyenda, BorderLayout.SOUTH);
        setMinimumSize(new Dimension(200, alto + 8 + leyenda.getPreferredSize().height));
        limpiar();
    }

    public void mostrar(Cinta nuevaCinta) {
        cinta = nuevaCinta;
        vista.getAccessibleContext().setAccessibleDescription(
                "Cabezal en la celda " + cinta.getPosicionCabezal()
                + ", símbolo leído: " + cinta.leer(cinta.getPosicionCabezal()));
        vista.revalidate();
        vista.repaint();
        // El desplazamiento se calcula después de actualizar el tamaño de la vista.
        SwingUtilities.invokeLater(() -> {
            if (cinta != null) {
                int x = vista.margenHorizontal()
                        + (cinta.getPosicionCabezal() - cinta.getLimiteIzquierdo()) * CeldaCinta.ANCHO;
                vista.scrollRectToVisible(new Rectangle(Math.max(0, x - CeldaCinta.ANCHO),
                        0, CeldaCinta.ANCHO * 3, CeldaCinta.ALTO));
            }
        });
    }

    public void limpiar() {
        cinta = null;
        vista.getAccessibleContext().setAccessibleDescription("No hay una cadena cargada.");
        vista.revalidate();
        vista.repaint();
        desplazamiento.getHorizontalScrollBar().setValue(0);
    }

    private final class VistaCinta extends JPanel implements Scrollable {
        private final CeldaCinta celda = new CeldaCinta();

        private VistaCinta() {
            getAccessibleContext().setAccessibleName("Cinta y posición del cabezal");
        }

        private int anchoCeldas() {
            long celdas = cinta == null ? 9L
                    : (long) cinta.getLimiteDerecho() - cinta.getLimiteIzquierdo() + 1;
            return (int) Math.min(Integer.MAX_VALUE - 32L, celdas * CeldaCinta.ANCHO);
        }

        private int margenHorizontal() {
            return Math.max(16, (getWidth() - anchoCeldas()) / 2);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(anchoCeldas() + 32, CeldaCinta.ALTO + 24);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visible, int orientacion, int direccion) {
            return CeldaCinta.ANCHO;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visible, int orientacion, int direccion) {
            return Math.max(CeldaCinta.ANCHO, visible.width - CeldaCinta.ANCHO);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return getParent() != null && getParent().getWidth() >= getPreferredSize().width;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return true;
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            super.paintComponent(grafico);
            Graphics2D dibujo = (Graphics2D) grafico.create();
            try {
                dibujo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                dibujo.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Rectangle visible = grafico.getClipBounds();
                int margen = margenHorizontal();
                int inicio = Math.max(0, (visible.x - margen) / CeldaCinta.ANCHO);
                int fin = (visible.x + visible.width - margen) / CeldaCinta.ANCHO;
                int izquierda = cinta == null ? -1 : cinta.getLimiteIzquierdo();
                int ultima = cinta == null ? 8 : cinta.getLimiteDerecho() - izquierda;
                int y = Math.max(8, (getHeight() - CeldaCinta.ALTO) / 2);
                for (int celdaVisible = inicio; celdaVisible <= Math.min(fin, ultima); celdaVisible++) {
                    int indice = izquierda + celdaVisible;
                    celda.pintar(dibujo, margen + celdaVisible * CeldaCinta.ANCHO, y, indice,
                            cinta == null ? Simbolo.BLANCO : cinta.leer(indice),
                            cinta != null && indice == cinta.getPosicionCabezal());
                }
            } finally {
                dibujo.dispose();
            }
        }
    }
}
