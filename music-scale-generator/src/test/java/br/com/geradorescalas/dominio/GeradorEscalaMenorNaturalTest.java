package br.com.geradorescalas.dominio;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeradorEscalaMenorNaturalTest {

    private final GeradorEscalaMenorNatural gerador =
        new GeradorEscalaMenorNatural();

    @Test
    void deveGerarEscalaDeLaMenorNatural() {
        assertEquals(
            "A - B - C - D - E - F - G - A",
            formatar(gerador.gerar(new Nota('A', Acidente.NATURAL)))
        );
    }

    @Test
    void deveGerarEscalaDeMiMenorNatural() {
        assertEquals(
            "E - F# - G - A - B - C - D - E",
            formatar(gerador.gerar(new Nota('E', Acidente.NATURAL)))
        );
    }

    @Test
    void deveGerarEscalaDeSiBemolMenorNatural() {
        assertEquals(
            "Bb - C - Db - Eb - F - Gb - Ab - Bb",
            formatar(gerador.gerar(new Nota('B', Acidente.BEMOL)))
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
