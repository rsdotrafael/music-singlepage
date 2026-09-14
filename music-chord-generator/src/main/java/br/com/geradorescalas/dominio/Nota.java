package br.com.geradorescalas.dominio;

import java.util.Objects;

public final class Nota {

    private final char letra;
    private final Acidente acidente;

    public Nota(char letra, Acidente acidente) {
        char letraMaiuscula = Character.toUpperCase(letra);

        if (letraMaiuscula < 'A' || letraMaiuscula > 'G') {
            throw new IllegalArgumentException("A letra da nota deve estar entre A e G");
        }

        this.letra = letraMaiuscula;
        this.acidente = Objects.requireNonNull(acidente, "O acidente não pode ser nulo");
    }

    public char getLetra() {
        return letra;
    }

    public Acidente getAcidente() {
        return acidente;
    }

    public int getAltura() {
        int alturaNatural = switch (letra) {
            case 'C' -> 0;
            case 'D' -> 2;
            case 'E' -> 4;
            case 'F' -> 5;
            case 'G' -> 7;
            case 'A' -> 9;
            case 'B' -> 11;
            default -> throw new IllegalStateException("Letra inválida: " + letra);
        };

        return Math.floorMod(
            alturaNatural + acidente.getAlteracao(),
            12
        );
    }

    @Override
    public String toString() {
        return letra + acidente.getSimbolo();
    }
}
