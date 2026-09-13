# Laboratorio 3: Máquina de Turing

Simulador de escritorio con **Java 25, Swing y Maven** para el lenguaje
`L = {aⁿbⁿ | n ≥ 1}`. Conserva la estructura modular original del proyecto.
La máquina decide mediante una tabla de transiciones y una cinta de lectura/escritura.

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

1. Escribe una cadena, por ejemplo `aabb`, y pulsa **Cargar** o Enter.
2. Pulsa **Paso** para realizar una transición. Observa la escritura, el
   movimiento del cabezal, el estado y la fila nueva del historial.
3. Pulsa **Ejecutar** para continuar automáticamente. El control de intervalo
   establece los milisegundos entre pasos; un valor menor significa más velocidad.
4. Usa **Pausar** para detener la ejecución y continuar después, incluso paso a paso.
5. Al finalizar se muestra **Cadena aceptada** o **Cadena rechazada**.
6. **Reiniciar** restaura la cadena original, el cabezal, `q0` y el historial vacío.
   Para probar otra entrada, escríbela y vuelve a pulsar **Cargar**.

La cinta muestra `□` para los blancos, `X` para las `a` procesadas y `Y` para las
`b` procesadas. La celda del cabezal tiene una flecha y los índices permiten
seguir su posición. El historial y la tabla de reglas sirven de apoyo para explicar
la función `δ`.

| Entrada | Resultado esperado |
| --- | --- |
| `ab`, `aabb`, `aaabbb`, `aaaabbbb` | Aceptada |
| `aab`, `abb`, `abab`, `ba`, `aaabb`, `a`, `b` | Rechazada por la máquina |
| `aabbc`, `123`, `abc`, `AB`, `a b` | Entrada inválida antes de simular |
| Vacía | Error de entrada: esta versión trabaja con `n ≥ 1` |

La validación no recorta espacios ni transforma mayúsculas. Si una carga es
inválida, se muestra el error y se conserva la simulación anterior, pausada.

## Organización

| Módulo | Responsabilidad |
| --- | --- |
| `turing.Turing` | Inicia Swing en su hilo de eventos y conecta ventana y controlador. |
| `modelo.cinta` | Símbolos, celdas de la cinta y posición del cabezal. |
| `modelo.maquina` | Estados, direcciones `L/R` y datos de una transición. |
| `reglas` | Tabla determinista de reglas y configuración específica de `aⁿbⁿ`. |
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

Las pruebas en `src/test/java` comprueban reconocimiento del lenguaje, movimientos
y escritura, rechazo por reglas ausentes, validación, historial y reinicio.
También comparan exhaustivamente las cadenas cortas del alfabeto `{a,b}` con una
definición independiente del lenguaje. Esa comparación se usa solo en las pruebas:
el programa obtiene el resultado exclusivamente de las transiciones.

Las pruebas de integración de Swing comprueban carga por Enter, disponibilidad
de botones, sincronización con el historial, pausa, continuación y reinicio
durante la ejecución automática. Se ejecutan en el hilo de eventos y esperan
eventos del temporizador, sin abrir una ventana.
