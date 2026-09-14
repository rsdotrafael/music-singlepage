package br.com.geradorescalas.web;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EscalaControllerTest {

    private final EscalaController controller = new EscalaController();

    @Test
    void deveGerarFaSustenidoMaior() {
        EscalaController.EscalaResponse resposta =
            controller.gerarEscalaMaior("F#");

        assertEquals("F#", resposta.tonica());
        assertEquals(
            "F# - G# - A# - B - C# - D# - E# - F#",
            formatarNotas(resposta)
        );
    }

    @Test
    void deveGerarSolBemolMaior() {
        EscalaController.EscalaResponse resposta =
            controller.gerarEscalaMaior("Gb");

        assertEquals(
            "Gb - Ab - Bb - Cb - Db - Eb - F - Gb",
            formatarNotas(resposta)
        );
    }

    @Test
    void deveGerarSolSustenidoMaiorComSustenidoDuplo() {
        EscalaController.EscalaResponse resposta =
            controller.gerarEscalaMaior("G#");

        assertEquals(
            "G# - A# - B# - C# - D# - E# - F## - G#",
            formatarNotas(resposta)
        );
    }

    @Test
    void deveGerarLaMenorNatural() {
        EscalaController.EscalaResponse resposta =
            controller.gerarEscalaMenorNatural("A");

        assertEquals("A", resposta.tonica());
        assertEquals(
            "A - B - C - D - E - F - G - A",
            formatarNotas(resposta)
        );
        assertEquals(69, resposta.notas().getFirst().midi());
        assertEquals(81, resposta.notas().getLast().midi());
    }

    @Test
    void deveGerarSiBemolMenorNatural() {
        EscalaController.EscalaResponse resposta =
            controller.gerarEscalaMenorNatural("Bb");

        assertEquals(
            "Bb - C - Db - Eb - F - Gb - Ab - Bb",
            formatarNotas(resposta)
        );
    }

    @Test
    void deveGerarLaMenorMelodica() {
        EscalaController.EscalaResponse resposta =
            controller.gerarEscalaMenorMelodica("A");

        assertEquals("A", resposta.tonica());
        assertEquals(
            "A - B - C - D - E - F# - G# - A",
            formatarNotas(resposta)
        );
        assertEquals(69, resposta.notas().getFirst().midi());
        assertEquals(81, resposta.notas().getLast().midi());
    }

    @Test
    void deveGerarLaMenorHarmonica() {
        EscalaController.EscalaResponse resposta =
            controller.gerarEscalaMenorHarmonica("A");

        assertEquals("A", resposta.tonica());
        assertEquals(
            "A - B - C - D - E - F - G# - A",
            formatarNotas(resposta)
        );
        assertEquals(69, resposta.notas().getFirst().midi());
        assertEquals(81, resposta.notas().getLast().midi());
    }

    @Test
    void deveFornecerAlturaSonoraParaReproducao() {
        EscalaController.EscalaResponse resposta =
            controller.gerarEscalaMaior("F#");

        assertEquals(66, resposta.notas().getFirst().midi());
        assertEquals(78, resposta.notas().getLast().midi());
        assertEquals(4, resposta.notas().getFirst().oitava());
        assertEquals(5, resposta.notas().getLast().oitava());
        assertEquals(
            369.994,
            resposta.notas().getFirst().frequencia(),
            0.001
        );
    }

    @Test
    void deveAceitarSimbolosMusicaisDeAcidente() {
        assertEquals("G#", controller.gerarEscalaMaior("G♯").tonica());
        assertEquals("Gb", controller.gerarEscalaMaior("G♭").tonica());
    }

    @Test
    void deveGerarTodasAsVinteEUmaTonalidades() {
        List<String> tonicas = List.of(
            "C", "C#", "Cb", "D", "D#", "Db", "E", "E#", "Eb",
            "F", "F#", "Fb", "G", "G#", "Gb", "A", "A#", "Ab",
            "B", "B#", "Bb"
        );

        for (String tonica : tonicas) {
            assertEquals(tonica, controller.gerarEscalaMaior(tonica).tonica());
        }
    }

    @Test
    void deveRejeitarTonicaInvalida() {
        ResponseStatusException erro = assertThrows(
            ResponseStatusException.class,
            () -> controller.gerarEscalaMaior("X")
        );

        assertEquals(400, erro.getStatusCode().value());
    }

    @Test
    void deveGerarEscalaPentatonicaPeloEndpointGenerico() {
        EscalaController.EscalaResponse resposta = controller.gerarEscala(
            "pentatonica-maior",
            "C"
        );

        assertEquals("C - D - E - G - A - C", formatarNotas(resposta));
        assertEquals(6, resposta.notas().size());
    }

    @Test
    void deveListarTiposDisponiveis() {
        assertEquals(24, controller.listarTipos().size());
        assertEquals("maior", controller.listarTipos().getFirst().id());
    }

    @Test
    void deveRejeitarTipoDeEscalaInvalido() {
        ResponseStatusException erro = assertThrows(
            ResponseStatusException.class,
            () -> controller.gerarEscala("inexistente", "C")
        );

        assertEquals(404, erro.getStatusCode().value());
    }

    private String formatarNotas(EscalaController.EscalaResponse resposta) {
        return resposta.notas().stream()
            .map(nota -> Objects.requireNonNull(nota).nome())
            .reduce((primeira, segunda) -> primeira + " - " + segunda)
            .orElse("");
    }
}
