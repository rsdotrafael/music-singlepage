package br.com.geradorescalas.dominio;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeradorEscalaGenericaTest {

    @Test
    void deveGerarPentatonicaMaiorComGrafiaCorreta() {
        GeradorEscala gerador = new GeradorEscala(
            TipoEscala.PENTATONICA_MAIOR.getDefinicao()
        );

        assertEquals(
            List.of("C", "D", "E", "G", "A", "C"),
            gerador.gerar(new Nota('C', Acidente.NATURAL)).stream()
                .map(nota -> nota.toString())
                .toList()
        );
    }

    @Test
    void deveCalcularOitavasPeloIntervaloAbsoluto() {
        GeradorEscala gerador = new GeradorEscala(
            new DefinicaoEscala(
                "duas-oitavas",
                "Duas oitavas",
                List.of(0, 12, 24),
                List.of(0, 7, 14)
            )
        );

        List<NotaComOitava> notas = gerador.gerarComOitavas(
            new Nota('B', Acidente.SUSTENIDO),
            4
        );

        assertEquals(List.of(72, 84, 96), notas.stream()
            .map(nota -> nota.getNumeroMidi())
            .toList());
    }

    @Test
    void deveGerarOsSeteModosDiatonicos() {
        assertEscala("modal-jonio", "C - D - E - F - G - A - B - C");
        assertEscala("modal-dorico", "C - D - Eb - F - G - A - Bb - C");
        assertEscala("modal-frigio", "C - Db - Eb - F - G - Ab - Bb - C");
        assertEscala("modal-lidio", "C - D - E - F# - G - A - B - C");
        assertEscala("modal-mixolidio", "C - D - E - F - G - A - Bb - C");
        assertEscala("modal-eolio", "C - D - Eb - F - G - Ab - Bb - C");
        assertEscala("modal-locrio", "C - Db - Eb - F - Gb - Ab - Bb - C");
    }

    @Test
    void deveGerarAsEscalasDiminutasAlternadas() {
        assertEscala("dom-dim", "C - Db - Eb - E - F# - G - A - Bb - C");
        assertEscala("dim-dom", "C - D - Eb - F - Gb - Ab - A - B - C");
    }

    @Test
    void deveGerarAsEscalasBlues() {
        assertEscala("blues-menor", "C - Eb - F - Gb - G - Bb - C");
        assertEscala("blues-maior", "C - D - Eb - E - G - A - C");
    }

    @Test
    void deveGerarAsEscalasDominantesModernas() {
        assertEscala("frigio-dominante", "C - Db - E - F - G - Ab - Bb - C");
        assertEscala("lidio-dominante", "C - D - E - F# - G - A - Bb - C");
        assertEscala("alterada", "C - Db - Eb - E - Gb - Ab - Bb - C");
        assertEscala("bebop-dominante", "C - D - E - F - G - A - Bb - B - C");
    }

    @Test
    void deveGerarAsEscalasCromaticaEAumentada() {
        assertEscala(
            "cromatica",
            "C - C# - D - D# - E - F - F# - G - G# - A - A# - B - C"
        );
        assertEscala("aumentada", "C - Eb - E - G - Ab - B - C");
    }

    private void assertEscala(String tipo, String esperado) {
        GeradorEscala gerador = new GeradorEscala(
            TipoEscala.buscar(tipo).orElseThrow().getDefinicao()
        );

        assertEquals(
            esperado,
            gerador.gerar(new Nota('C', Acidente.NATURAL)).stream()
                .map(nota -> nota.toString())
                .reduce((primeira, segunda) -> primeira + " - " + segunda)
                .orElse("")
        );
    }
}
