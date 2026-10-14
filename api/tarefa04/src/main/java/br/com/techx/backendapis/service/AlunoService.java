package br.com.techx.backendapis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.techx.backendapis.dto.AlunoRequest;
import br.com.techx.backendapis.dto.AlunoResponse;
import br.com.techx.backendapis.model.Aluno;
import br.com.techx.backendapis.repository.AlunoRepository;

@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<AlunoResponse> listar(String nome) {
        List<Aluno> alunos;
        if (nome == null || nome.isBlank()) {
            alunos = alunoRepository.findAll();
        } else {
            alunos = alunoRepository.findByNomeContainingIgnoreCase(nome.trim());
        }

        List<AlunoResponse> respostas = new ArrayList<>();
        for (Aluno aluno : alunos) {
            respostas.add(converterParaResponse(aluno));
        }
        return respostas;
    }

    public AlunoResponse getById(Long id) {
        Optional<Aluno> optional = alunoRepository.findById(id);
        if (optional.isPresent()) {
            Aluno aluno = optional.get();
            return converterParaResponse(aluno);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado");
        }
    }

    public AlunoResponse create(AlunoRequest request) {
        validar(request);
        String ra = request.getRa().trim();
        if (alunoRepository.existsByRa(ra)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "RA já cadastrado");
        }

        Aluno aluno = new Aluno();
        aluno.setNome(request.getNome().trim());
        aluno.setIdade(request.getIdade());
        aluno.setRa(ra);
        Aluno salvo = alunoRepository.save(aluno);
        return converterParaResponse(salvo);
    }

    public void update(Long id, AlunoRequest request) {
        Aluno aluno = alunoRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        validar(request);
        String ra = request.getRa().trim();
        if (alunoRepository.existsByRaAndIdNot(ra, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "RA já cadastrado");
        }

        aluno.setNome(request.getNome().trim());
        aluno.setIdade(request.getIdade());
        aluno.setRa(ra);
        alunoRepository.save(aluno);
    }

    public void deleteById(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado");
        }
        alunoRepository.deleteById(id);
    }

    private void validar(AlunoRequest request) {
        if (request == null || request.getNome() == null || request.getNome().isBlank()
                || request.getRa() == null || request.getRa().isBlank()
                || request.getIdade() == null || request.getIdade() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, RA e idade válida são obrigatórios");
        }
    }

    private AlunoResponse converterParaResponse(Aluno aluno) {
        return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getIdade(), aluno.getRa());
    }
}
