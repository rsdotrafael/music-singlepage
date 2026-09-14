package br.com.geradorescalas.dominio;

import java.util.List;
import java.util.Objects;

public record DefinicaoEscala(
    String id,
    String nome,
    List<Integer> intervalos,
    List<Integer> deslocamentosDiatonicos
) {
    public DefinicaoEscala {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("O identificador da escala é obrigatório");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da escala é obrigatório");
        }
        intervalos = List.copyOf(Objects.requireNonNull(intervalos));
        deslocamentosDiatonicos = List.copyOf(
            Objects.requireNonNull(deslocamentosDiatonicos)
        );
        if (intervalos.isEmpty() || intervalos.size() != deslocamentosDiatonicos.size()) {
            throw new IllegalArgumentException(
                "Intervalos e deslocamentos devem ter a mesma quantidade de itens"
            );
        }
        if (intervalos.getFirst() != 0 || intervalos.stream().anyMatch(i -> i < 0)) {
            throw new IllegalArgumentException(
                "Os intervalos devem começar em zero e não podem ser negativos"
            );
        }
    }
}
