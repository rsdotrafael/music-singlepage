package br.com.geradorescalas.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeradorEscala {

    private static final String LETRAS = "ABCDEFG";

    private final DefinicaoEscala definicao;

    public GeradorEscala(DefinicaoEscala definicao) {
        this.definicao = Objects.requireNonNull(
            definicao,
            "A definição da escala não pode ser nula"
        );
    }

    public List<Nota> gerar(Nota tonica) {
        Objects.requireNonNull(tonica, "A tônica não pode ser nula");

        List<Nota> escala = new ArrayList<>();

        for (int grau = 0; grau < definicao.intervalos().size(); grau++) {
            int alturaEsperada = Math.floorMod(
                tonica.getAltura() + definicao.intervalos().get(grau),
                12
            );
            char letra = calcularLetra(
                tonica.getLetra(),
                definicao.deslocamentosDiatonicos().get(grau)
            );
            Acidente acidente = escolherAcidente(letra, alturaEsperada);

            escala.add(new Nota(letra, acidente));
        }

        return List.copyOf(escala);
    }

    public List<NotaComOitava> gerarComOitavas(
        Nota tonica,
        int oitavaInicial
    ) {
        List<Nota> notas = gerar(tonica);
        List<NotaComOitava> escala = new ArrayList<>();
        int midiDaTonica = new NotaComOitava(tonica, oitavaInicial)
            .getNumeroMidi();

        for (int grau = 0; grau < notas.size(); grau++) {
            Nota nota = notas.get(grau);
            int midiEsperado = midiDaTonica
                + definicao.intervalos().get(grau);
            escala.add(new NotaComOitava(
                nota,
                calcularOitava(nota, midiEsperado)
            ));
        }

        return List.copyOf(escala);
    }

    private char calcularLetra(char letraDaTonica, int deslocamento) {
        int indiceInicial = LETRAS.indexOf(letraDaTonica);
        int indiceDoGrau = Math.floorMod(
            indiceInicial + deslocamento,
            LETRAS.length()
        );

        return LETRAS.charAt(indiceDoGrau);
    }

    private int calcularOitava(Nota nota, int midiEsperado) {
        int alturaEscrita = alturaNatural(nota.getLetra())
            + nota.getAcidente().getAlteracao();
        return Math.floorDiv(midiEsperado - alturaEscrita, 12) - 1;
    }

    private int alturaNatural(char letra) {
        return switch (letra) {
            case 'C' -> 0;
            case 'D' -> 2;
            case 'E' -> 4;
            case 'F' -> 5;
            case 'G' -> 7;
            case 'A' -> 9;
            case 'B' -> 11;
            default -> throw new IllegalStateException("Letra inválida: " + letra);
        };
    }

    private Acidente escolherAcidente(char letra, int alturaEsperada) {
        Nota notaNatural = new Nota(letra, Acidente.NATURAL);
        int diferenca = Math.floorMod(
            alturaEsperada - notaNatural.getAltura(),
            12
        );

        return switch (diferenca) {
            case 0 -> Acidente.NATURAL;
            case 1 -> Acidente.SUSTENIDO;
            case 2 -> Acidente.SUSTENIDO_DUPLO;
            case 10 -> Acidente.BEMOL_DUPLO;
            case 11 -> Acidente.BEMOL;
            default -> throw new IllegalArgumentException(
                "A escala exige um acidente ainda não suportado"
            );
        };
    }
}
