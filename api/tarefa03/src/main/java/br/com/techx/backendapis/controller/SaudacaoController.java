package br.com.techx.backendapis.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saudacao")
public class SaudacaoController {
    private final String nomeAluno;
    private final String raAluno;

    public SaudacaoController(
            @Value("${app.aluno.nome}") String nomeAluno,
            @Value("${app.aluno.ra}") String raAluno) {
        this.nomeAluno = nomeAluno;
        this.raAluno = raAluno;
    }

    @GetMapping("/ola")
    public String ola() {
        return "ola mundo";
    }

    @GetMapping(value = "/ra", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ra() {
        return "O RA do aluno " + nomeAluno + " é " + raAluno;
    }
}
