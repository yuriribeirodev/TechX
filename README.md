# TechX

Repositório das atividades da disciplina, organizado por tecnologia e tarefa.

## Banco de dados

- `sql/tarefa01` a `sql/tarefa03`: atividades sobre a rede de hotéis; a tarefa 3 usa MySQL 8.0+.
- `sql/tarefa04`: exemplo de biblioteca em PostgreSQL.
- `sql/tarefa05`: exemplo de sistema acadêmico em PostgreSQL.
- `sql/tarefa06`: continuação do sistema acadêmico com backup, restauração e MongoDB.

As tarefas 4 e 5 foram copiadas de [anaoliveiravv/techx](https://github.com/anaoliveiravv/techx), dos diretórios `bancodedados/entregavel4` e `bancodedados/entregavel5`. São exemplos independentes do modelo de hotéis das tarefas anteriores; não execute seus scripts sobre o mesmo esquema esperando continuidade entre as tarefas.

## APIs e backend

- `api/tarefa02`: API REST de alunos em Spring Boot com JPA e PostgreSQL.
- `api/tarefa03`: CRUD de alunos com DTOs e tratamento explícito de registros não encontrados.
- `api/tarefa04`: revisão do PUT e DELETE com Lombok, DTOs e respostas HTTP 204.

## Convenção de commits

Usamos o padrão **Conventional Commits**:

```text
tipo(escopo): descrição curta em minúsculas
```

Exemplos:

- `feat(sql): adiciona modelo lógico normalizado da rede de hotéis`
- `fix(java): corrige validação de placa do carro`
- `docs(sql): explica normalização da tarefa03`

Os tipos permitidos são: `feat`, `fix`, `docs`, `refactor`, `test` e `chore`.
