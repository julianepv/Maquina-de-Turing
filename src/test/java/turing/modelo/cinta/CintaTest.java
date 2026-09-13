package turing.modelo.cinta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import turing.modelo.maquina.Direccion;

class CintaTest {

    @Test
    void colocaLaEntradaEnLaCintaYElCabezalSobreElPrimerSimbolo() {
        Cinta cinta = new Cinta("aabb");

        assertEquals(0, cinta.getPosicionCabezal());
        assertEquals(Simbolo.A, cinta.leer());
        assertEquals(Simbolo.A, cinta.leer(1));
        assertEquals(Simbolo.B, cinta.leer(2));
        assertEquals(Simbolo.B, cinta.leer(3));
        assertEquals(Simbolo.BLANCO, cinta.leer(-1));
        assertEquals(Simbolo.BLANCO, cinta.leer(4));
        assertEquals(0, cinta.getPosicionCabezal(), "Consultar otra celda no mueve el cabezal");
    }

    @Test
    void permiteMoverseYEscribirEnIndicesNegativos() {
        Cinta cinta = new Cinta("ab");

        cinta.mover(Direccion.L);
        cinta.mover(Direccion.L);
        assertEquals(-2, cinta.getPosicionCabezal());
        assertEquals(Simbolo.BLANCO, cinta.leer());

        cinta.escribir(Simbolo.X);
        assertEquals(Simbolo.X, cinta.leer(-2));
        assertTrue(cinta.getLimiteIzquierdo() <= -2);

        cinta.mover(Direccion.R);
        cinta.mover(Direccion.R);
        assertEquals(0, cinta.getPosicionCabezal());
        assertEquals(Simbolo.A, cinta.leer());
        assertEquals(Simbolo.X, cinta.leer(-2), "Mover el cabezal conserva lo escrito");
    }

    @Test
    void creceHaciaLaDerechaYPermiteEscribirUnBlanco() {
        Cinta cinta = new Cinta("ab");
        for (int i = 0; i < 5; i++) {
            cinta.mover(Direccion.R);
        }

        assertEquals(5, cinta.getPosicionCabezal());
        assertEquals(Simbolo.BLANCO, cinta.leer());
        cinta.escribir(Simbolo.Y);
        assertEquals(Simbolo.Y, cinta.leer(5));
        cinta.escribir(Simbolo.BLANCO);
        assertEquals(Simbolo.BLANCO, cinta.leer());
        assertTrue(cinta.getLimiteDerecho() >= 5);
        assertEquals(Simbolo.A, cinta.leer(0));
        assertEquals(Simbolo.B, cinta.leer(1));
    }
}
