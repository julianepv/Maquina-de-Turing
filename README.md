# Laboratorio 3: Máquina de Turing

Simulador de escritorio con **Java 25, Swing y Maven**. La persona usuaria define
el programa de la máquina mediante su tabla de transiciones y la aplicación lo
ejecuta sobre una cinta de lectura/escritura.

## Compilar y ejecutar

Desde la carpeta que contiene `pom.xml`, también en la terminal de Zed:

```sh
mvn test
mvn package
java -jar target/turing.jar
```

O compilar y abrir la aplicación directamente:

```sh
mvn compile exec:java
```

Se necesita un JDK 25 y una sesión gráfica para abrir Swing. Las pruebas pueden
ejecutarse sin escritorio. Maven descarga sus complementos y JUnit la primera
vez; la aplicación empaquetada no requiere bibliotecas externas.

## Uso

1. Escribe una cadena, por ejemplo `aaabb`.
2. Escribe el **estado inicial** y el **estado final** que definen tu máquina. El alfabeto `{a, b, X, Y}`
   y la cinta `{a, b, X, Y, B}` aparecen solo como información del laboratorio.
3. Escribe una transición en las dos celdas de δ: `(q0, a)` y `(q1, X, R)`.
   Pulsa **Agregar transición**; la regla se añade inmediatamente a la matriz
   inferior, en la fila `q0` y la columna `a`. Las direcciones son `L` y `R`;
   `B` (o `□`) representa el blanco de la cinta.
   La matriz usa el encabezado `Estados | a | b | X | Y | B`.
4. Para cargar muchas reglas, pulsa **Cargar .txt** y selecciona un archivo UTF-8
   con una ecuación por línea, por ejemplo `(q0, a)=(q1, X, R)`. Las líneas vacías
   se ignoran. El archivo se valida completo antes de reemplazar la matriz actual.
5. Pulsa **Cargar** o Enter para validar y cargar el programa y la cadena.
6. Pulsa **Paso** para realizar una transición. Observa la escritura, el
   movimiento del cabezal, el estado y la celda de la matriz δ que se usó.
7. Pulsa **Ejecutar** para continuar automáticamente. La simulación utiliza
   un intervalo predeterminado de 700 ms por transición para permitir apreciar
   claramente cada operación del autómata.
8. Usa **Pausar** para detener la ejecución y continuar después, incluso paso a paso.
9. Al finalizar se muestra **Cadena aceptada** o **Cadena rechazada**.
10. **Reiniciar** restaura la cadena y el programa cargados, el cabezal, el estado
   inicial y el historial vacío.
   Para probar otra entrada, escríbela y vuelve a pulsar **Cargar**.

La cinta muestra `B` para los blancos. Los símbolos permitidos en las reglas son
`a`, `b`, `X`, `Y` y `B`; la cadena inicial conserva el alfabeto de entrada `a/b`.
La celda del cabezal tiene una flecha y los índices permiten seguir su posición.
La matriz δ se sincroniza con la configuración actual: la celda verde corresponde
al estado y símbolo bajo el cabezal antes del siguiente paso; rojo indica una
transición ausente. Los guiones representan transiciones no definidas.

| Entrada | Resultado esperado |
| --- | --- |
| Sin regla para el par `(estado, símbolo)` actual | Rechazada por la máquina |
| Dos reglas para el mismo par `(estado, símbolo)` | Error al cargar: la función debe ser determinista |
| `aabbc`, `123`, `abc`, `AB`, `a b` | Entrada inválida antes de simular |
| Cadena vacía | Error de entrada |

La validación no recorta espacios ni transforma mayúsculas. Si una carga es
inválida, se muestra el error y se conserva la simulación anterior, pausada.

## Organización

| Módulo | Responsabilidad |
| --- | --- |
| `turing.Turing` | Inicia Swing en su hilo de eventos y conecta ventana y controlador. |
| `modelo.cinta` | Símbolos, celdas de la cinta y posición del cabezal. |
| `modelo.maquina` | Estados, direcciones `L/R` y datos de una transición. |
| `reglas` | Tabla determinista de reglas y conversión de las transiciones escritas por la persona usuaria. |
| `simulacion.MaquinaTuring` | Consulta y aplica una regla por paso; detecta la detención. |
| `simulacion.MotorSimulacion` | Coordina carga, reinicio e historial. |
| `simulacion.ResultadoPaso` / `HistorialEjecucion` | Conservan la evidencia de ejecución. |
| `validacion` | Comprueba que la entrada sea no vacía y contenga solamente `a/b`. |
| `controlador` | Conecta botones, temporizador, motor y actualización de los paneles. |
| `interfaz` | Ventana, paneles y dibujo de las celdas; presenta los datos del motor. |

El motor y el modelo no importan Swing. La ejecución manual y automática usan
el mismo método `paso()`. La automática llama a ese método desde un
`javax.swing.Timer`, sin bloquear la interfaz con un ciclo o con `Thread.sleep`.

Consulta [la guía de exposición](docs/guia-exposicion.md) para la definición formal,
las diez reglas, una traza completa y el análisis de complejidad.

## Verificación

Las pruebas en `src/test/java` comprueban lectura de transiciones, movimientos,
escritura, rechazo por reglas ausentes, validación, historial y reinicio. El
programa obtiene el resultado exclusivamente de las transiciones cargadas.

Las pruebas de integración de Swing comprueban carga por Enter, disponibilidad
de botones, sincronización con el historial, pausa, continuación y reinicio
durante la ejecución automática. Se ejecutan en el hilo de eventos y esperan
eventos del temporizador, sin abrir una ventana.
