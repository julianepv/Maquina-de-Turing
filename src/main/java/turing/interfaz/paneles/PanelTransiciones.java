package turing.interfaz.paneles;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import turing.interfaz.componentes.TablaTransiciones;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Estado;
import turing.simulacion.ResultadoPaso;

/** Matriz visual de δ, organizada por estado y símbolo leído. */
public final class PanelTransiciones extends JPanel {
    private static final Color RESALTADO_DERECHA = new Color(195, 235, 205);
    private static final Color RESALTADO_ERROR = new Color(255, 195, 195);
    private static final String TRANSICION_AUSENTE = "-";
    private static final Pattern IZQUIERDA = Pattern.compile(
            "(?:δ\\s*)?\\(\\s*([A-Za-z][A-Za-z0-9_]*)\\s*,\\s*([abXYB□])\\s*\\)");
    private static final Pattern DERECHA = Pattern.compile(
            "\\(\\s*([A-Za-z][A-Za-z0-9_]*)\\s*,\\s*([abXYB□])\\s*,\\s*([LRlr])\\s*\\)");
    private static final Pattern ESTADO_NUMERADO = Pattern.compile("^(.*?)(\\d+)$");

    private final DefaultTableModel modelo = new DefaultTableModel(
        new String[] { "Estados", "a", "b", "X", "Y", "B" }, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final TablaTransiciones tabla = new TablaTransiciones(
        modelo, 135, 165, 165, 165, 165, 165
    );
    private final JScrollPane desplazamiento = new JScrollPane(tabla);
    private final Map<String, Integer> filasPorEstado = new LinkedHashMap<>();
    private int filaResaltada = -1;
    private int columnaResaltada = -1;
    private Color colorResaltado;

    public PanelTransiciones() {
        setLayout(null);
        setBackground(Color.WHITE);
        tabla.getAccessibleContext().setAccessibleName("Tabla de transiciones de la máquina");
        configurarRenderer();

        desplazamiento.setBounds(0, 0, 970, 245);
        desplazamiento.setBackground(Color.WHITE);
        desplazamiento.getViewport().setBackground(Color.WHITE);
        desplazamiento.setBorder(new LineBorder(new Color(80, 125, 175), 1));
        add(desplazamiento);
    }

    /** Añade una regla escrita en las dos celdas de entrada a la matriz δ. */
    public void agregarRegla(String ladoIzquierdo, String ladoDerecho) {
        Matcher izquierda = IZQUIERDA.matcher(ladoIzquierdo.trim());
        Matcher derecha = DERECHA.matcher(ladoDerecho.trim());
        if (!izquierda.matches() || !derecha.matches()) {
            throw new IllegalArgumentException(
                    "Usa (q0, a) = (q1, X, R) para agregar una transición.");
        }

        String estado = izquierda.group(1);
        int columna = columnaDe(izquierda.group(2));
        int fila = filaDe(estado);
        if (!TRANSICION_AUSENTE.equals(modelo.getValueAt(fila, columna))) {
            throw new IllegalArgumentException("Ya existe una transición para δ(" + estado + ", "
                    + izquierda.group(2) + ").");
        }

        String simboloEscrito = normalizarSimbolo(derecha.group(2));
        String movimiento = derecha.group(3).toUpperCase();
        modelo.setValueAt("(" + derecha.group(1) + ", " + simboloEscrito + ", " + movimiento + ")",
                fila, columna);
        filaDe(derecha.group(1));
        ordenarFilas();
    }

    /** Muestra estados sin transiciones salientes, incluidos los estados finales. */
    public void asegurarFilas(Collection<Estado> estados) {
        for (Estado estado : estados) {
            filaDe(estado.toString());
        }
        ordenarFilas();
    }

    /** Sustituye la matriz por las reglas importadas de un archivo validado. */
    public void reemplazarReglas(List<String> reglas) {
        limpiarReglas();
        for (String regla : reglas) {
            String[] partes = regla.split("=", -1);
            if (partes.length != 2) {
                throw new IllegalArgumentException("La transición importada no tiene un único signo =.");
            }
            agregarRegla(partes[0], partes[1]);
        }
    }

    public void limpiarReglas() {
        limpiarResaltado();
        modelo.setRowCount(0);
        filasPorEstado.clear();
    }

    /** Resalta la regla ejecutada en el mismo instante que se actualiza la cinta. */
    public void resaltar(ResultadoPaso paso) {
        limpiarResaltado();
        Integer fila = filasPorEstado.get(paso.estadoAnterior().toString());
        if (fila == null) {
            return;
        }
        filaResaltada = fila;
        columnaResaltada = columnaDe(paso.simboloLeido());
        colorResaltado = paso.direccion() == null ? RESALTADO_ERROR : RESALTADO_DERECHA;
        modelo.fireTableCellUpdated(filaResaltada, columnaResaltada);
        desplazarHasta(filaResaltada, columnaResaltada);
    }

    /** Resalta la transición que corresponde a la configuración actual de la máquina. */
    public void resaltarConfiguracion(Estado estado, Simbolo simbolo) {
        limpiarResaltado();
        Integer fila = filasPorEstado.get(estado.toString());
        if (fila == null) {
            return;
        }
        filaResaltada = fila;
        columnaResaltada = columnaDe(simbolo);
        colorResaltado = RESALTADO_DERECHA;
        modelo.fireTableCellUpdated(filaResaltada, columnaResaltada);
        desplazarHasta(filaResaltada, columnaResaltada);
    }

    private void desplazarHasta(int fila, int columna) {
        SwingUtilities.invokeLater(() -> tabla.scrollRectToVisible(
                tabla.getCellRect(fila, columna, true)));
    }

    public void limpiarResaltado() {
        int filaAnterior = filaResaltada;
        int columnaAnterior = columnaResaltada;
        filaResaltada = -1;
        columnaResaltada = -1;
        colorResaltado = null;
        tabla.clearSelection();
        if (filaAnterior >= 0) {
            modelo.fireTableCellUpdated(filaAnterior, columnaAnterior);
        }
    }

    private int filaDe(String estado) {
        Integer existente = filasPorEstado.get(estado);
        if (existente != null) {
            return existente;
        }
        int nuevaFila = modelo.getRowCount();
        modelo.addRow(new Object[] { estado, TRANSICION_AUSENTE, TRANSICION_AUSENTE,
                TRANSICION_AUSENTE, TRANSICION_AUSENTE, TRANSICION_AUSENTE });
        filasPorEstado.put(estado, nuevaFila);
        return nuevaFila;
    }

    /** Ordena q0, q1, q2... antes de los nombres no numéricos como qf. */
    private void ordenarFilas() {
        if (modelo.getRowCount() < 2) {
            return;
        }
        limpiarResaltado();
        Object[][] filas = new Object[modelo.getRowCount()][modelo.getColumnCount()];
        for (int fila = 0; fila < modelo.getRowCount(); fila++) {
            for (int columna = 0; columna < modelo.getColumnCount(); columna++) {
                filas[fila][columna] = modelo.getValueAt(fila, columna);
            }
        }
        Arrays.sort(filas, Comparator.comparing(fila -> fila[0].toString(),
                this::compararEstados));

        modelo.setRowCount(0);
        filasPorEstado.clear();
        for (Object[] fila : filas) {
            int indice = modelo.getRowCount();
            modelo.addRow(fila);
            filasPorEstado.put(fila[0].toString(), indice);
        }
    }

    private int compararEstados(String primero, String segundo) {
        Matcher primeroNumerado = ESTADO_NUMERADO.matcher(primero);
        Matcher segundoNumerado = ESTADO_NUMERADO.matcher(segundo);
        if (primeroNumerado.matches() && segundoNumerado.matches()
                && primeroNumerado.group(1).equals(segundoNumerado.group(1))) {
            int comparacionNumerica = compararNumeros(primeroNumerado.group(2),
                    segundoNumerado.group(2));
            return comparacionNumerica != 0 ? comparacionNumerica : primero.compareTo(segundo);
        }
        return primero.compareTo(segundo);
    }

    private int compararNumeros(String primero, String segundo) {
        String primeroSinCeros = primero.replaceFirst("^0+(?!$)", "");
        String segundoSinCeros = segundo.replaceFirst("^0+(?!$)", "");
        int comparacionLongitud = Integer.compare(primeroSinCeros.length(), segundoSinCeros.length());
        return comparacionLongitud != 0
                ? comparacionLongitud : primeroSinCeros.compareTo(segundoSinCeros);
    }

    private int columnaDe(String simbolo) {
        return switch (normalizarSimbolo(simbolo)) {
            case "a" -> 1;
            case "b" -> 2;
            case "X" -> 3;
            case "Y" -> 4;
            case "B" -> 5;
            default -> throw new IllegalArgumentException("Símbolo inválido en la transición.");
        };
    }

    private int columnaDe(Simbolo simbolo) {
        return columnaDe(simbolo == Simbolo.BLANCO ? "B" : simbolo.toString());
    }

    private String normalizarSimbolo(String simbolo) {
        return "□".equals(simbolo) ? "B" : simbolo;
    }

    private void configurarRenderer() {
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean seleccionada, boolean enfocada, int fila, int columna) {
                JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(table, value,
                        seleccionada, enfocada, fila, columna);
                etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
                etiqueta.setOpaque(true);
                if (fila == filaResaltada && columna == columnaResaltada) {
                    etiqueta.setBackground(colorResaltado);
                } else if (columna == 0) {
                    etiqueta.setBackground(new Color(225, 235, 245));
                } else {
                    etiqueta.setBackground(fila % 2 == 0 ? new Color(240, 248, 255) : Color.WHITE);
                }
                return etiqueta;
            }
        });
    }
}
