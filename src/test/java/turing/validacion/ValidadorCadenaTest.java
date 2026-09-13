package turing.validacion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ValidadorCadenaTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"aabbc", "123", "abc", "AB", " ab", "ab ", "a b", "a\nb", "á", "□", "XY"})
    void rechazaEntradasVaciasOSimbolosFueraDelAlfabeto(String entrada) {
        assertThrows(IllegalArgumentException.class, () -> ValidadorCadena.validar(entrada));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "aabb", "aaabbb", "a", "b", "ba", "abab", "aab", "abb"})
    void soloValidaElAlfabetoSinDecidirLaPertenenciaAlLenguaje(String entrada) {
        assertDoesNotThrow(() -> ValidadorCadena.validar(entrada));
    }
}
