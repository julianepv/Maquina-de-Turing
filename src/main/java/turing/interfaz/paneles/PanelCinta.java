package turing.interfaz.paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import turing.interfaz.componentes.CeldaCinta;
import turing.modelo.cinta.Cinta;

/** Visualiza la cinta sin crear un componente por símbolo ni copiarla en cada paso. */
public final class PanelCinta extends JPanel {
    private final VistaCinta vista = new VistaCinta();
    private final JScrollPane desplazamiento = new JScrollPane(vista,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private Cinta cinta;

    public PanelCinta() {
        super(new BorderLayout(0, 6));
        setBorder(BorderFactory.createTitledBorder("Cinta de la máquina"));
        setMinimumSize(new Dimension(300, 171));
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getHorizontalScrollBar().setUnitIncrement(CeldaCinta.ANCHO);
        desplazamiento.setPreferredSize(new Dimension(700, CeldaCinta.ALTO + 26));
        add(desplazamiento, BorderLayout.CENTER);
        JLabel leyenda = new JLabel("▼ Cabezal    □ Blanco    X = a procesada    Y = b procesada    ·    Índices debajo de cada celda");
        leyenda.setForeground(new Color(75, 85, 99));
        add(leyenda, BorderLayout.SOUTH);
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
                int x = (cinta.getPosicionCabezal() - cinta.getLimiteIzquierdo()) * CeldaCinta.ANCHO;
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

    private final class VistaCinta extends JPanel {
        private final CeldaCinta celda = new CeldaCinta();

        private VistaCinta() {
            setOpaque(true);
            setBackground(new Color(247, 249, 252));
            getAccessibleContext().setAccessibleName("Cinta y posición del cabezal");
        }

        @Override
        public Dimension getPreferredSize() {
            long celdas = cinta == null ? 11L
                    : (long) cinta.getLimiteDerecho() - cinta.getLimiteIzquierdo() + 1;
            int ancho = (int) Math.min(Integer.MAX_VALUE, Math.max(11L, celdas) * CeldaCinta.ANCHO);
            return new Dimension(ancho, CeldaCinta.ALTO + 10);
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            super.paintComponent(grafico);
            Graphics2D dibujo = (Graphics2D) grafico.create();
            try {
                dibujo.setColor(getBackground());
                dibujo.fillRect(0, 0, getWidth(), getHeight());
                dibujo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (cinta == null) {
                    dibujo.setFont(getFont());
                    dibujo.setColor(new Color(97, 109, 126));
                    dibujo.drawString("Carga una cadena para visualizar la cinta y el cabezal.", 20, 59);
                    return;
                }

                Rectangle visible = grafico.getClipBounds();
                int inicio = Math.max(0, visible.x / CeldaCinta.ANCHO);
                int fin = (visible.x + visible.width) / CeldaCinta.ANCHO;
                int izquierda = cinta.getLimiteIzquierdo();
                int ultima = cinta.getLimiteDerecho() - izquierda;
                for (int celdaVisible = inicio; celdaVisible <= Math.min(fin, ultima); celdaVisible++) {
                    int indice = izquierda + celdaVisible;
                    celda.pintar(dibujo, celdaVisible * CeldaCinta.ANCHO, 4, indice,
                            cinta.leer(indice), indice == cinta.getPosicionCabezal());
                }
            } finally {
                dibujo.dispose();
            }
        }
    }
}
