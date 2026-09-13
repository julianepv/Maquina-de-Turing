# Guía para explicar el laboratorio

## 1. Qué problema resuelve

El programa decide si una cadena pertenece a `L = {aⁿbⁿ | n ≥ 1}`: primero deben
aparecer todas las `a` y después todas las `b`, en igual cantidad. `aabb` pertenece
al lenguaje; `abab` no, aunque tenga la misma cantidad de ambos símbolos.

Esta implementación toma `n ≥ 1`, por eso una entrada vacía produce un mensaje
de validación. Los caracteres distintos de `a` y `b` también son errores de
entrada. En cambio, una entrada como `aab` sí se carga: la máquina debe procesarla
y llegar por sí misma a rechazo.

## 2. Modelo formal

La máquina tiene los siguientes elementos:

| Elemento | Valor |
| --- | --- |
| Estados `Q` | `{q0, q1, q2, q3, ACEPTAR, RECHAZAR}` |
| Alfabeto de entrada `Σ` | `{a, b}` |
| Alfabeto de cinta `Γ` | `{a, b, X, Y, □}` |
| Estado inicial | `q0` |
| Símbolo blanco | `□` |
| Estados de detención | `ACEPTAR`, `RECHAZAR` |
| Movimientos | `L`: izquierda; `R`: derecha |
| Función de transición | `δ(q, s) = (q′, s′, D)` |

Una regla consulta el estado y la celda bajo el cabezal, escribe un símbolo en
esa celda, cambia de estado y desplaza el cabezal una posición. La tabla es
determinista: cada pareja `(estado, símbolo)` puede tener como máximo una regla.

La función `δ` es parcial. Cuando falta una regla, la implementación entra en
`RECHAZAR`, conservando la cinta y la posición. El historial registra ese intento
final con movimiento `—`; no se inventa una transición `L/R`. Los estados finales
no ejecutan más pasos.

## 3. Idea del algoritmo y estados

Se emparejan símbolos marcándolos sobre la cinta:

1. `q0` encuentra la siguiente `a` sin procesar, escribe `X` y va a `q1`.
2. `q1` busca hacia la derecha una `b` sin procesar, pasando por `a` y `Y`.
   Cuando la encuentra, escribe `Y` y vuelve a la izquierda en `q2`.
3. `q2` regresa hasta la `X` más cercana. Desde allí avanza una celda y vuelve
   a `q0`, listo para procesar otra pareja.
4. Si `q0` encuentra una `Y`, ya terminó el bloque de `a` y pasa a `q3`.
5. `q3` verifica que solo queden `Y`. Acepta al llegar al blanco; cualquier
   otro símbolo sin regla causa rechazo.

Para una entrada correcta, al comenzar cada nueva pareja la cinta tiene la forma
`Xᵏ aⁿ⁻ᵏ Yᵏ bⁿ⁻ᵏ`. Una vuelta completa aumenta en uno ambas cantidades de marcas.
Al terminar queda `XⁿYⁿ`. El programa no calcula `k` ni `n`: estas expresiones
describen el comportamiento de las transiciones.

## 4. Tabla de transiciones

Las reglas están concentradas en `ConfiguracionAnBn.crearTabla()`:

| Estado | Lee | Escribe | Mueve | Nuevo estado |
| --- | --- | --- | --- | --- |
| `q0` | `a` | `X` | `R` | `q1` |
| `q0` | `Y` | `Y` | `R` | `q3` |
| `q1` | `a` | `a` | `R` | `q1` |
| `q1` | `Y` | `Y` | `R` | `q1` |
| `q1` | `b` | `Y` | `L` | `q2` |
| `q2` | `a` | `a` | `L` | `q2` |
| `q2` | `Y` | `Y` | `L` | `q2` |
| `q2` | `X` | `X` | `R` | `q0` |
| `q3` | `Y` | `Y` | `R` | `q3` |
| `q3` | `□` | `□` | `R` | `ACEPTAR` |

Todas las demás combinaciones en estados no finales provocan rechazo. Por ejemplo:

- `q0` leyendo `b`: hay una `b` donde se esperaba el inicio de una pareja.
- `q1` leyendo `□`: no se encontró la `b` que corresponde a la `a` marcada.
- `q3` leyendo `a` o `b`: quedan símbolos sin procesar o en un orden incorrecto.

## 5. Traza completa de `ab`

Los corchetes indican la celda del cabezal. La entrada empieza en el índice `0`.

| Paso | Regla aplicada | Estado después | Cinta después |
| --- | --- | --- | --- |
| 0 | Carga inicial | `q0` | `□ [a] b □` |
| 1 | `δ(q0,a) = (q1,X,R)` | `q1` | `□ X [b] □` |
| 2 | `δ(q1,b) = (q2,Y,L)` | `q2` | `□ [X] Y □` |
| 3 | `δ(q2,X) = (q0,X,R)` | `q0` | `□ X [Y] □` |
| 4 | `δ(q0,Y) = (q3,Y,R)` | `q3` | `□ X Y [□] □` |
| 5 | `δ(q3,□) = (ACEPTAR,□,R)` | `ACEPTAR` | `□ X Y □ [□] □` |

La transición de aceptación también mueve el cabezal: se aplica la regla
completa antes de detener la máquina.

## 6. Dónde mostrar cada concepto en el código

Empieza por `ConfiguracionAnBn` para explicar el algoritmo. Después abre
`MaquinaTuring.paso()`: allí se ve la consulta a la tabla, escritura, cambio de
estado y movimiento. Finalmente muestra `Cinta` y el controlador.

`Cinta` guarda símbolos en un `Map<Integer, Simbolo>`. Una posición no almacenada
se interpreta como un blanco; escribir un blanco elimina la entrada del mapa.
Puede moverse a índices negativos y expandirse hacia ambos lados según se necesite.
Es una representación finita de la parte usada de la cinta idealmente infinita;
su capacidad práctica depende de la memoria y de los índices enteros disponibles.

La cadena `String` solo se usa para validar, cargar las celdas y permitir el
reinicio. Durante la simulación, la máquina lee y modifica la cinta. Los bucles
de carga, validación o dibujo no deciden si la cadena pertenece al lenguaje.

`ResultadoPaso` es un `record`: conserva los datos de un paso sin modificarlos.
`HistorialEjecucion` reúne esos resultados; la tabla de Swing presenta una fila
por paso. El controlador usa el mismo motor tanto con Paso como con Ejecutar.

## 7. Complejidad

Para una cadena aceptada con `n` parejas hay `n` viajes de ida y vuelta. Cada
viaje aplica `2n + 1` transiciones y la comprobación final aplica `n + 1`.
Por tanto, esta tabla concreta realiza `2n² + 2n + 1` transiciones: 5 para `ab`,
13 para `aabb` y 25 para `aaabbb`.

Si `m = 2n` es la longitud de entrada, el tiempo del algoritmo es `Θ(m²)` para
las cadenas aceptadas y el peor caso entre entradas de longitud `m` es cuadrático.
La cinta usa `O(m)` espacio. Conservar todo el historial requiere además `O(m²)`
registros en esos casos. El intervalo visual añade tiempo de espera entre pasos,
pero no cambia el número de transiciones.

Para la demostración conviene usar cadenas cortas: el historial completo y el
retraso visual están pensados para observar y explicar cada paso.

## 8. Recorrido sugerido para la demostración

1. Carga `ab` y reproduce los cinco pasos de la tabla anterior.
2. Carga `aaabbb`, ejecuta automáticamente, cambia la velocidad y pausa para
   señalar el estado, el cabezal y la transición en el historial.
3. Ejecuta `aab`: la máquina acaba buscando una `b` que no existe.
4. Ejecuta `abab`: la cantidad coincide, pero el orden causa rechazo en `q3`.
5. Intenta cargar `aabbc` y luego una entrada vacía para mostrar la validación.
6. Reinicia una simulación y comprueba que vuelve a la cinta original, `q0`,
   cabezal `0` y cero pasos.
