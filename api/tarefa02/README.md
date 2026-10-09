# Desenvolvimento de APIs e Backend — Semana 02

API REST de alunos em Spring Boot com persistência real no PostgreSQL. A implementação parte do exemplo local de aula em `backend-apis/`, cujo [repositório de origem](https://github.com/Adriano047/aula-sb) contém a estrutura inicial. Esta pasta é a entrega integrada ao TechX, sem o repositório Git aninhado do exemplo.

## Arquitetura

- `controller`: recebe HTTP e devolve as respostas REST.
- `service`: valida os dados e coordena as operações.
- `repository`: `AlunoRepository extends JpaRepository<Aluno, Long>` executa o acesso ao banco.
- `model`: entidades JPA `Aluno` e `Curso`, com `@Entity`, `@Id` e `@GeneratedValue`.

O fluxo de um cadastro é **cliente → AlunoController → AlunoService → AlunoRepository → PostgreSQL**. Controller e Service recebem suas dependências por construtor, gerenciadas pelo Spring.

## Configuração e execução

Requer Java 21 ou superior, Maven Wrapper e PostgreSQL. Crie o banco `techx_api` antes de iniciar; o Hibernate cria e atualiza as tabelas automaticamente com `spring.jpa.hibernate.ddl-auto=update`. O SQL gerado aparece no console com `spring.jpa.show-sql=true`. `pom.xml` contém `spring-boot-starter-data-jpa` e o driver `postgresql`.

Exemplo de PostgreSQL local via Docker:

```bash
docker run --name techx-api-pg -e POSTGRES_PASSWORD=senha_local -e POSTGRES_DB=techx_api -p 5432:5432 -d postgres:16-alpine
```

No diretório desta atividade:

```bash
export DB_URL='jdbc:postgresql://localhost:5432/techx_api'
export DB_USERNAME='postgres'
export DB_PASSWORD='senha_local'
./mvnw spring-boot:run
```

`application.properties` usa essas variáveis para a conexão. A senha não é versionada. O endpoint do desafio usa as propriedades `app.aluno.nome=Yuri Ribeiro Moraes` e `app.aluno.ra=027348`; o RA é texto para preservar o zero inicial.

## Endpoints

| Método | Caminho | Resultado |
| --- | --- | --- |
| GET | `/aluno` | Lista todos os alunos. |
| GET | `/aluno?nome=Yuri` | Filtra pelo nome, sem diferenciar maiúsculas de minúsculas. |
| GET | `/aluno/{id}` | Busca por ID; retorna 404 quando não existe. |
| POST | `/aluno` | Cria aluno a partir de JSON; retorna 201. |
| PUT | `/aluno/{id}` | Atualiza nome, idade e RA. |
| DELETE | `/aluno/{id}` | Remove aluno; retorna 204. |
| GET | `/api/saudacao/ra` | Retorna `O RA do aluno Yuri Ribeiro Moraes é 027348`. |
| GET | `/api/saudacao/ola` | Mantém a saudação do exemplo inicial. |

Exemplo de cadastro:

```bash
curl -X POST http://localhost:8080/aluno \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Aluno Exemplo","idade":20,"ra":"000123"}'
```

Nome e RA não podem ser vazios, idade não pode ser negativa e RA é único. Dados inválidos retornam 400; RA já cadastrado retorna 409.

## Verificação realizada

Em 09/10/2026, a aplicação compilou com o Maven Wrapper e iniciou conectada a PostgreSQL 16. O console do Hibernate mostrou `create table aluno` e `create table curso`. Uma consulta direta ao `information_schema.tables` confirmou as duas tabelas; outra consulta à tabela `aluno` confirmou a persistência do registro criado via `POST` com RA `027348`. Também foram verificados `GET /aluno`, `GET /aluno?nome=Yuri`, `GET /aluno/1`, `PUT /aluno/1`, `DELETE /aluno/1` e o `404` após a remoção. `GET /api/saudacao/ra` retornou a frase exigida.
