package br.com.geradorescalas.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotaComOitavaTest {

    @Test
    void deveCalcularNumeroMidiEFrequencia() {
        NotaComOitava laQuatro = new NotaComOitava(
            new Nota('A', Acidente.NATURAL),
            4
        );

        assertEquals(69, laQuatro.getNumeroMidi());
        assertEquals(440.0, laQuatro.getFrequencia());
    }

    @Test
    void deveConsiderarGrafiaEnarmonicaNaOitava() {
        NotaComOitava siSustenidoQuatro = new NotaComOitava(
            new Nota('B', Acidente.SUSTENIDO),
            4
        );
        NotaComOitava doCinco = new NotaComOitava(
            new Nota('C', Acidente.NATURAL),
            5
        );

        assertEquals(doCinco.getNumeroMidi(), siSustenidoQuatro.getNumeroMidi());
    }

    @Test
    void deveConsiderarAcidenteDuplo() {
        NotaComOitava faSustenidoDuploQuatro = new NotaComOitava(
            new Nota('F', Acidente.SUSTENIDO_DUPLO),
            4
        );

        assertEquals(67, faSustenidoDuploQuatro.getNumeroMidi());
    }
}
