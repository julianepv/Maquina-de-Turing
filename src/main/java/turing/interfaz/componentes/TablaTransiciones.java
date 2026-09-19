package turing.interfaz.componentes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/** JTable reutilizable con estilo clásico, limpio y legible. */
public final class TablaTransiciones extends JTable {

    private static final Color COLOR_ENCABEZADO = new Color(200, 220, 240);
    private static final Color COLOR_FILA_PAR = new Color(240, 248, 255);
    private static final Color COLOR_FILA_IMPAR = Color.WHITE;
    private static final Color COLOR_SELECCION = new Color(205, 225, 245);
    private static final Color COLOR_GRID = new Color(165, 185, 205);
    private static final Color COLOR_TEXTO = new Color(25, 25, 25);

    private static final Font FUENTE_TABLA = new Font("Arial", Font.PLAIN, 13);
    private static final Font FUENTE_ENCABEZADO = new Font(
        "Tahoma",
        Font.BOLD,
        13
    );

    public TablaTransiciones(TableModel modelo, int... anchosPreferidos) {
        super(modelo);

        configurarTabla();
        configurarEncabezado();
        configurarCeldas();
        configurarColumnas(anchosPreferidos);
    }

    private void configurarTabla() {
        setFont(FUENTE_TABLA);
        setForeground(COLOR_TEXTO);
        setBackground(Color.WHITE);
        setRowHeight(29);
        setFillsViewportHeight(true);

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(false);
        setSelectionBackground(COLOR_SELECCION);
        setSelectionForeground(Color.BLACK);

        setShowGrid(true);
        setShowHorizontalLines(true);
        setShowVerticalLines(true);
        setGridColor(COLOR_GRID);
        setIntercellSpacing(new Dimension(1, 1));

        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setPreferredScrollableViewportSize(new Dimension(1000, 180));
    }

    private void configurarEncabezado() {
        JTableHeader encabezado = getTableHeader();
        encabezado.setFont(FUENTE_ENCABEZADO);
        encabezado.setBackground(COLOR_ENCABEZADO);
        encabezado.setForeground(new Color(35, 35, 35));
        encabezado.setReorderingAllowed(false);
        encabezado.setResizingAllowed(true);
        encabezado.setPreferredSize(
            new Dimension(encabezado.getPreferredSize().width, 31)
        );

        DefaultTableCellRenderer rendererEncabezado =
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
                ) {
                    JLabel etiqueta =
                        (JLabel) super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                        );

                    etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
                    etiqueta.setFont(FUENTE_ENCABEZADO);
                    etiqueta.setBackground(COLOR_ENCABEZADO);
                    etiqueta.setForeground(new Color(35, 35, 35));
                    etiqueta.setOpaque(true);
                    etiqueta.setBorder(
                        BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                1,
                                COLOR_GRID
                            ),
                            BorderFactory.createEmptyBorder(3, 6, 3, 6)
                        )
                    );

                    return etiqueta;
                }
            };

        encabezado.setDefaultRenderer(rendererEncabezado);
    }

    private void configurarCeldas() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
            ) {
                JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
                );

                etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
                etiqueta.setFont(FUENTE_TABLA);
                etiqueta.setForeground(COLOR_TEXTO);
                etiqueta.setOpaque(true);

                if (isSelected) {
                    etiqueta.setBackground(COLOR_SELECCION);
                } else {
                    etiqueta.setBackground(
                        row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR
                    );
                }

                etiqueta.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
                return etiqueta;
            }
        };

        setDefaultRenderer(Object.class, renderer);
    }

    private void configurarColumnas(int... anchosPreferidos) {
        for (int i = 0; i < getColumnCount(); i++) {
            TableColumn columna = getColumnModel().getColumn(i);
            if (i < anchosPreferidos.length) {
                columna.setPreferredWidth(anchosPreferidos[i]);
            }
        }
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }
}
