package br.com.techx.backendapis.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.techx.backendapis.model.Aluno;
import br.com.techx.backendapis.repository.AlunoRepository;

@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> listar(String nome) {
        return nome == null || nome.isBlank()
                ? alunoRepository.findAll()
                : alunoRepository.findByNomeContainingIgnoreCase(nome.trim());
    }

    public Aluno buscar(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
    }

    public Aluno criar(Aluno aluno) {
        validar(aluno);
        if (alunoRepository.existsByRa(aluno.getRa())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "RA já cadastrado");
        }
        aluno.setId(null);
        return alunoRepository.save(aluno);
    }

    public Aluno atualizar(Long id, Aluno dados) {
        validar(dados);
        Aluno aluno = buscar(id);
        if (alunoRepository.existsByRaAndIdNot(dados.getRa(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "RA já cadastrado");
        }
        aluno.setNome(dados.getNome().trim());
        aluno.setIdade(dados.getIdade());
        aluno.setRa(dados.getRa().trim());
        return alunoRepository.save(aluno);
    }

    public void excluir(Long id) {
        Aluno aluno = buscar(id);
        alunoRepository.delete(aluno);
    }

    private void validar(Aluno aluno) {
        if (aluno.getNome() == null || aluno.getNome().isBlank()
                || aluno.getRa() == null || aluno.getRa().isBlank()
                || aluno.getIdade() == null || aluno.getIdade() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, RA e idade válida são obrigatórios");
        }
        aluno.setNome(aluno.getNome().trim());
        aluno.setRa(aluno.getRa().trim());
    }
}
