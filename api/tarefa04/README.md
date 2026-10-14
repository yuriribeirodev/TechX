# Desenvolvimento de APIs e Backend — Semana 04

Continuação da [Semana 03](../tarefa03/README.md). Esta entrega revisa o CRUD de alunos com DTOs, JPA e Lombok. A API continua usando PostgreSQL na execução normal; os testes automatizados usam H2 em memória.

## Mudanças desta semana

- `AlunoService.update` busca o aluno com `findById(id).orElseThrow(...)`. Se o ID existe, valida `AlunoRequest`, atualiza a Entity e chama `save()`. Se não existe, responde 404.
- `AlunoService.deleteById` consulta `existsById(id)`. Um `if (!exists)` lança 404; somente depois o Service chama `deleteById(id)`. Não há lambda no DELETE.
- O Controller mantém `@PutMapping("/{id}")` e `@DeleteMapping("/{id}")`, com `@PathVariable`, `@RequestBody` no PUT e `ResponseEntity.noContent().build()` para 204 sem corpo.
- `Aluno`, `Curso`, `AlunoRequest` e `AlunoResponse` usam `@Getter`, `@Setter`, `@NoArgsConstructor` e `@AllArgsConstructor` do Lombok. A API recebe `AlunoRequest` e devolve `AlunoResponse`, sem expor `Aluno` nos métodos do Controller.

## Endpoints

| Método | Caminho | Resultado |
| --- | --- | --- |
| POST | `/aluno` | 201 com `AlunoResponse` |
| GET | `/aluno/{id}` | 200 com `AlunoResponse`, ou 404 |
| GET | `/aluno` | 200 com lista de `AlunoResponse`; aceita `?nome=` |
| PUT | `/aluno/{id}` | 204 sem corpo, ou 404 para ID inexistente |
| DELETE | `/aluno/{id}` | 204 sem corpo, ou 404 para ID inexistente |

RA duplicado retorna 409; dados inválidos retornam 400. O endpoint `/api/saudacao/ra` continua disponível com o nome e RA configurados na Semana 02.

Exemplo de atualização:

```bash
curl -i -X PUT http://localhost:8080/aluno/1 \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Yuri Ribeiro Moraes","idade":25,"ra":"027348"}'
```

Exemplo de remoção:

```bash
curl -i -X DELETE http://localhost:8080/aluno/1
```

## Execução e verificação

Configure `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` para um banco PostgreSQL `techx_api` e execute `./mvnw spring-boot:run`. O `application.properties` mantém `spring.jpa.hibernate.ddl-auto=update` e `spring.jpa.show-sql=true`.

Em 13/10/2026, `./mvnw test` compilou o projeto e executou o teste de integração com MockMvc e H2. O fluxo de criação, busca, atualização e exclusão passou: 201 no POST, 200 no GET, 204 sem corpo no PUT/DELETE, 404 para IDs ausentes, 400 para dados inválidos e 409 para RA duplicado. Não foi executado um teste com servidor PostgreSQL nesta semana.
