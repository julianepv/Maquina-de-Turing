package turing.reglas;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import turing.modelo.cinta.Simbolo;
import turing.modelo.maquina.Direccion;
import turing.modelo.maquina.Estado;
import turing.modelo.maquina.Transicion;

/** Convierte el texto escrito por la persona usuaria en una tabla determinista. */
public record ConfiguracionMaquina(TablaTransiciones tabla, Estado estadoInicial) {
    private static final Set<String> SIMBOLOS_VALIDOS = Set.of("a", "b", "X", "Y", "B", "□");

    public static ConfiguracionMaquina desde(String textoInicial, String textoAceptacion,
            String textoTransiciones) {
        if (textoTransiciones == null || textoTransiciones.isBlank()) {
            throw new IllegalArgumentException("Ingresa al menos una transición.");
        }

        Set<String> aceptacion = nombres(textoAceptacion, "estado de aceptación");
        if (aceptacion.isEmpty()) {
            throw new IllegalArgumentException("Indica al menos un estado de aceptación.");
        }

        Map<String, Estado> estados = new LinkedHashMap<>();
        for (String nombre : aceptacion) {
            estados.put(nombre, Estado.deAceptacion(nombre));
        }

        String inicial = validarNombre(textoInicial, "estado inicial");
        Estado estadoInicial = obtenerEstado(estados, inicial, aceptacion);
        TablaTransiciones tabla = new TablaTransiciones();
        String[] lineas = textoTransiciones.split("\\R", -1);
        for (int indice = 0; indice < lineas.length; indice++) {
            String linea = lineas[indice].trim();
            if (linea.isEmpty()) {
                continue;
            }
            registrarLinea(tabla, estados, aceptacion, linea, indice + 1);
        }
        if (tabla.getTransiciones().isEmpty()) {
            throw new IllegalArgumentException("Ingresa al menos una transición.");
        }
        return new ConfiguracionMaquina(tabla, estadoInicial);
    }

    private static void registrarLinea(TablaTransiciones tabla, Map<String, Estado> estados,
            Set<String> aceptacion, String linea, int numeroLinea) {
        if (!linea.contains("->")) {
            registrarEcuacion(tabla, estados, aceptacion, linea, numeroLinea);
            return;
        }
        String[] partes = linea.split("->", -1);
        if (partes.length != 2) {
            throw errorLinea(numeroLinea, "usa el formato q1, a -> q2, b, R.");
        }
        String[] izquierda = partes[0].split(",", -1);
        String[] derecha = partes[1].split(",", -1);
        if (izquierda.length != 2 || derecha.length != 3) {
            throw errorLinea(numeroLinea, "usa el formato q1, a -> q2, b, R.");
        }
        String origen = validarNombre(izquierda[0], "estado actual");
        String destino = validarNombre(derecha[0], "nuevo estado");
        Simbolo leido = simbolo(izquierda[1], numeroLinea);
        Simbolo escrito = simbolo(derecha[1], numeroLinea);
        Direccion direccion;
        try {
            direccion = Direccion.desdeTexto(derecha[2]);
        } catch (IllegalArgumentException ex) {
            throw errorLinea(numeroLinea, ex.getMessage());
        }
        try {
            tabla.registrar(new Transicion(obtenerEstado(estados, origen, aceptacion), leido,
                    obtenerEstado(estados, destino, aceptacion), escrito, direccion));
        } catch (IllegalArgumentException ex) {
            throw errorLinea(numeroLinea, ex.getMessage());
        }
    }

    /** Lee δ(q, símbolo) = (q', símbolo', dirección), con δ opcional. */
    private static void registrarEcuacion(TablaTransiciones tabla, Map<String, Estado> estados,
            Set<String> aceptacion, String linea, int numeroLinea) {
        String sinDelta = linea.replaceFirst("^\\s*δ\\s*", "");
        String[] partes = sinDelta.split("=", -1);
        if (partes.length != 2) {
            throw errorLinea(numeroLinea, "usa el formato δ(q0, a) = (q1, X, R).");
        }
        String[] izquierda = elementosTupla(partes[0], 2, numeroLinea);
        String[] derecha = elementosTupla(partes[1], 3, numeroLinea);
        String origen = validarNombre(izquierda[0], "estado actual");
        String destino = validarNombre(derecha[0], "nuevo estado");
        Simbolo leido = simbolo(izquierda[1], numeroLinea);
        Simbolo escrito = simbolo(derecha[1], numeroLinea);
        Direccion direccion;
        try {
            direccion = Direccion.desdeTexto(derecha[2]);
        } catch (IllegalArgumentException ex) {
            throw errorLinea(numeroLinea, ex.getMessage());
        }
        try {
            tabla.registrar(new Transicion(obtenerEstado(estados, origen, aceptacion), leido,
                    obtenerEstado(estados, destino, aceptacion), escrito, direccion));
        } catch (IllegalArgumentException ex) {
            throw errorLinea(numeroLinea, ex.getMessage());
        }
    }

    private static String[] elementosTupla(String texto, int cantidad, int numeroLinea) {
        String limpia = texto.trim();
        if (!limpia.startsWith("(") || !limpia.endsWith(")")) {
            throw errorLinea(numeroLinea, "usa el formato δ(q0, a) = (q1, X, R).");
        }
        String[] elementos = limpia.substring(1, limpia.length() - 1).split(",", -1);
        if (elementos.length != cantidad) {
            throw errorLinea(numeroLinea, "usa el formato δ(q0, a) = (q1, X, R).");
        }
        return elementos;
    }

    private static Estado obtenerEstado(Map<String, Estado> estados, String nombre,
            Set<String> aceptacion) {
        return estados.computeIfAbsent(nombre, clave -> aceptacion.contains(clave)
                ? Estado.deAceptacion(clave) : Estado.normal(clave));
    }

    private static Simbolo simbolo(String texto, int linea) {
        String limpio = texto.trim();
        if (!SIMBOLOS_VALIDOS.contains(limpio)) {
            throw errorLinea(linea, "el símbolo debe ser a, b, X, Y o B (blanco).");
        }
        return switch (limpio) {
            case "a" -> Simbolo.A;
            case "b" -> Simbolo.B;
            case "X" -> Simbolo.X;
            case "Y" -> Simbolo.Y;
            case "B", "□" -> Simbolo.BLANCO;
            default -> throw new IllegalStateException("Símbolo no reconocido.");
        };
    }

    private static String validarNombre(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Indica el " + campo + ".");
        }
        try {
            return Estado.normal(texto).toString();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("El " + campo + " no es válido: " + ex.getMessage());
        }
    }

    private static Set<String> nombres(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            return Set.of();
        }
        try {
            return Arrays.stream(texto.split(","))
                    .map(nombre -> validarNombre(nombre, campo))
                    .collect(Collectors.toUnmodifiableSet());
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }

    private static IllegalArgumentException errorLinea(int numero, String detalle) {
        return new IllegalArgumentException("Error en la transición de la línea " + numero + ": " + detalle);
    }
}
