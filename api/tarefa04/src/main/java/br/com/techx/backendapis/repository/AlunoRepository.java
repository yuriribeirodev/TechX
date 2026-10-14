package br.com.techx.backendapis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.techx.backendapis.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    List<Aluno> findByNomeContainingIgnoreCase(String nome);
    boolean existsByRa(String ra);
    boolean existsByRaAndIdNot(String ra, Long id);
}
