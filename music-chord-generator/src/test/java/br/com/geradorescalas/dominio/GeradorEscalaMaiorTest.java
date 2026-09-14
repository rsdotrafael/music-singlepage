package br.com.geradorescalas.dominio;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeradorEscalaMaiorTest {

    private final GeradorEscalaMaior gerador = new GeradorEscalaMaior();

    @Test
    void deveGerarEscalaDeFaMaior() {
        List<Nota> escala = gerador.gerar(
            new Nota('F', Acidente.NATURAL)
        );

        assertEquals(
            "F - G - A - Bb - C - D - E - F",
            formatar(escala)
        );
    }

    @Test
    void deveGerarEscalaDeFaSustenidoMaior() {
        List<Nota> escala = gerador.gerar(
            new Nota('F', Acidente.SUSTENIDO)
        );

        assertEquals(
            "F# - G# - A# - B - C# - D# - E# - F#",
            formatar(escala)
        );
    }

    @Test
    void deveGerarEscalaDeSolBemolMaior() {
        List<Nota> escala = gerador.gerar(
            new Nota('G', Acidente.BEMOL)
        );

        assertEquals(
            "Gb - Ab - Bb - Cb - Db - Eb - F - Gb",
            formatar(escala)
        );
    }

    @Test
    void deveGerarEscalaDeSolSustenidoMaiorComSustenidoDuplo() {
        List<Nota> escala = gerador.gerar(
            new Nota('G', Acidente.SUSTENIDO)
        );

        assertEquals(
            "G# - A# - B# - C# - D# - E# - F## - G#",
            formatar(escala)
        );
    }

    @Test
    void deveGerarEscalaDeFaBemolMaiorComBemolDuplo() {
        List<Nota> escala = gerador.gerar(
            new Nota('F', Acidente.BEMOL)
        );

        assertEquals(
            "Fb - Gb - Ab - Bbb - Cb - Db - Eb - Fb",
            formatar(escala)
        );
    }

    @Test
    void deveRejeitarTonicaNula() {
        NullPointerException erro = assertThrows(
            NullPointerException.class,
            () -> gerador.gerar(null)
        );

        assertEquals("A tônica não pode ser nula", erro.getMessage());
    }

    private String formatar(List<Nota> escala) {
        return escala.stream()
            .map(nota -> nota.toString())
            .collect(Collectors.joining(" - "));
    }
}
