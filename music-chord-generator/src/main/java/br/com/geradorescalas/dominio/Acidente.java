package br.com.geradorescalas.dominio;

public enum Acidente {
    BEMOL_DUPLO("bb", -2),
    BEMOL("b", -1),
    NATURAL("", 0),
    SUSTENIDO("#", 1),
    SUSTENIDO_DUPLO("##", 2);

    private final String simbolo;
    private final int alteracao;

    Acidente(String simbolo, int alteracao) {
        this.simbolo = simbolo;
        this.alteracao = alteracao;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public int getAlteracao() {
        return alteracao;
    }
}
