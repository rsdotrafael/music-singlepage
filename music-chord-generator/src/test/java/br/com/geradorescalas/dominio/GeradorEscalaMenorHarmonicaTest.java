package br.com.geradorescalas.dominio;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeradorEscalaMenorHarmonicaTest {

    private final GeradorEscalaMenorHarmonica gerador =
        new GeradorEscalaMenorHarmonica();

    @Test
    void deveGerarEscalaDeLaMenorHarmonica() {
        assertEquals(
            "A - B - C - D - E - F - G# - A",
            formatar(gerador.gerar(new Nota('A', Acidente.NATURAL)))
        );
    }

    @Test
    void deveGerarEscalaDeDoMenorHarmonica() {
        assertEquals(
            "C - D - Eb - F - G - Ab - B - C",
            formatar(gerador.gerar(new Nota('C', Acidente.NATURAL)))
        );
    }

    @Test
    void deveGerarEscalaDeSolSustenidoMenorHarmonica() {
        assertEquals(
            "G# - A# - B - C# - D# - E - F## - G#",
            formatar(gerador.gerar(new Nota('G', Acidente.SUSTENIDO)))
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
            .map(nota -> Objects.requireNonNull(nota).toString())
            .collect(Collectors.joining(" - "));
    }
}
