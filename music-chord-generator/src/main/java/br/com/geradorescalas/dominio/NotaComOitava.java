package br.com.geradorescalas.dominio;

import java.util.Objects;

public record NotaComOitava(Nota nota, int oitava) {

    public NotaComOitava {
        Objects.requireNonNull(nota, "A nota não pode ser nula");

        int numeroMidi = calcularNumeroMidi(nota, oitava);
        if (numeroMidi < 0 || numeroMidi > 127) {
            throw new IllegalArgumentException("A nota está fora do intervalo MIDI");
        }
    }

    public int getNumeroMidi() {
        return calcularNumeroMidi(nota, oitava);
    }

    public double getFrequencia() {
        return 440.0 * Math.pow(2.0, (getNumeroMidi() - 69) / 12.0);
    }

    private static int calcularNumeroMidi(Nota nota, int oitava) {
        int alturaNatural = switch (nota.getLetra()) {
            case 'C' -> 0;
            case 'D' -> 2;
            case 'E' -> 4;
            case 'F' -> 5;
            case 'G' -> 7;
            case 'A' -> 9;
            case 'B' -> 11;
            default -> throw new IllegalStateException("Letra inválida");
        };

        return (oitava + 1) * 12
            + alturaNatural
            + nota.getAcidente().getAlteracao();
    }
}
