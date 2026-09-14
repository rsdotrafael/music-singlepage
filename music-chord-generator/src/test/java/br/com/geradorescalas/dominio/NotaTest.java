package br.com.geradorescalas.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotaTest {

    @Test
    void deveRepresentarNotaNatural() {
        Nota nota = new Nota('F', Acidente.NATURAL);

        assertEquals("F", nota.toString());
    }

    @Test
    void deveRepresentarNotaComSustenido() {
        Nota nota = new Nota('F', Acidente.SUSTENIDO);

        assertEquals("F#", nota.toString());
    }

    @Test
    void deveRepresentarNotaComBemol() {
        Nota nota = new Nota('B', Acidente.BEMOL);

        assertEquals("Bb", nota.toString());
    }

    @Test
    void deveRepresentarAcidentesDuplos() {
        assertEquals(
            "F##",
            new Nota('F', Acidente.SUSTENIDO_DUPLO).toString()
        );
        assertEquals(
            "Bbb",
            new Nota('B', Acidente.BEMOL_DUPLO).toString()
        );
    }

    @Test
    void deveConverterLetraMinusculaParaMaiuscula() {
        Nota nota = new Nota('f', Acidente.NATURAL);

        assertEquals('F', nota.getLetra());
        assertEquals("F", nota.toString());
    }

    @Test
    void deveRejeitarLetraInvalida() {
        IllegalArgumentException erro = assertThrows(
            IllegalArgumentException.class,
            () -> new Nota('X', Acidente.NATURAL)
        );

        assertEquals(
            "A letra da nota deve estar entre A e G",
            erro.getMessage()
        );
    }

    @Test
    void deveRejeitarAcidenteNulo() {
        NullPointerException erro = assertThrows(
            NullPointerException.class,
            () -> new Nota('F', null)
        );

        assertEquals(
            "O acidente não pode ser nulo",
            erro.getMessage()
        );
    }

    @Test
    void deveCalcularAlturaDeNotaNatural() {
        Nota nota = new Nota('F', Acidente.NATURAL);

        assertEquals(5, nota.getAltura());
    }

    @Test
    void deveCalcularAlturaComSustenido() {
        Nota nota = new Nota('F', Acidente.SUSTENIDO);

        assertEquals(6, nota.getAltura());
    }

    @Test
    void deveCalcularAlturaComBemol() {
        Nota nota = new Nota('G', Acidente.BEMOL);

        assertEquals(6, nota.getAltura());
    }

    @Test
    void deveDarMesmaAlturaParaNotasEnarmonicas() {
        Nota faSustenido = new Nota('F', Acidente.SUSTENIDO);
        Nota solBemol = new Nota('G', Acidente.BEMOL);

        assertEquals(faSustenido.getAltura(), solBemol.getAltura());
    }

    @Test
    void deveVoltarAoInicioDepoisDeB() {
        Nota nota = new Nota('B', Acidente.SUSTENIDO);

        assertEquals(0, nota.getAltura());
    }

    @Test
    void deveVoltarAoFinalAntesDeC() {
        Nota nota = new Nota('C', Acidente.BEMOL);

        assertEquals(11, nota.getAltura());
    }
}
