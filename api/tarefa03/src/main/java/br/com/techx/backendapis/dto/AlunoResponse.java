package br.com.techx.backendapis.dto;

public class AlunoResponse {
    private final Long id;
    private final String nome;
    private final Integer idade;
    private final String ra;

    public AlunoResponse(Long id, String nome, Integer idade, String ra) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.ra = ra;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public String getRa() {
        return ra;
    }
}
