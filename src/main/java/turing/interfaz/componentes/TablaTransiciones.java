package turing.interfaz.componentes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/** Tabla de solo lectura con el estilo compartido de las transiciones. */
public final class TablaTransiciones extends JTable {
    /** Los anchos son preferidos; cada titulo y su margen fijan el ancho minimo. */
    public TablaTransiciones(TableModel modelo, int... anchos) {
        super(modelo);
        Font fuente = getFont().deriveFont(Font.PLAIN, 13f);
        setFont(fuente);
        setRowHeight(Math.max(30, getFontMetrics(fuente).getHeight() + 12));
        setFillsViewportHeight(true);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(AUTO_RESIZE_OFF);

        Color fondo = getBackground();
        Color texto = getForeground();
        setSelectionBackground(new Color(
                (fondo.getRed() * 7 + texto.getRed()) / 8,
                (fondo.getGreen() * 7 + texto.getGreen()) / 8,
                (fondo.getBlue() * 7 + texto.getBlue()) / 8));
        setSelectionForeground(texto);

        Border margen = BorderFactory.createEmptyBorder(0, 8, 0, 8);
        DefaultTableCellRenderer contenido = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor,
                    boolean seleccionada, boolean tieneFoco, int fila, int columna) {
                super.getTableCellRendererComponent(tabla, valor, seleccionada, tieneFoco, fila, columna);
                setBorder(tieneFoco ? BorderFactory.createCompoundBorder(getBorder(), margen) : margen);
                return this;
            }
        };
        contenido.setHorizontalAlignment(JLabel.CENTER);

        JTableHeader encabezado = getTableHeader();
        encabezado.setFont(fuente);
        encabezado.setReorderingAllowed(false);
        TableCellRenderer nativo = encabezado.getDefaultRenderer();
        encabezado.setDefaultRenderer((tabla, valor, seleccionada, tieneFoco, fila, columna) -> {
            Component componente = nativo.getTableCellRendererComponent(
                    tabla, valor, seleccionada, tieneFoco, fila, columna);
            componente.setFont(encabezado.getFont());
            if (componente instanceof JLabel etiqueta) {
                etiqueta.setHorizontalAlignment(JLabel.CENTER);
            }
            return componente;
        });

        for (int i = 0; i < getColumnCount(); i++) {
            TableColumn columna = getColumnModel().getColumn(i);
            Component titulo = encabezado.getDefaultRenderer().getTableCellRendererComponent(
                    this, columna.getHeaderValue(), false, false, -1, i);
            int minimo = Math.max(48, titulo.getPreferredSize().width + 16);
            int preferido = Math.max(minimo, i < anchos.length ? anchos[i] : 96);
            columna.setMinWidth(minimo);
            columna.setPreferredWidth(preferido);
            columna.setWidth(preferido);
            columna.setCellRenderer(contenido);
        }
        Dimension tamanoEncabezado = encabezado.getPreferredSize();
        tamanoEncabezado.height = Math.max(tamanoEncabezado.height,
                Math.max(32, getFontMetrics(fuente).getHeight() + 12));
        encabezado.setPreferredSize(tamanoEncabezado);
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getParent() instanceof JViewport vista && vista.getWidth() >= getPreferredSize().width;
    }
}
