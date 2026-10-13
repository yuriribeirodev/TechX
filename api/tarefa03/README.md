# Desenvolvimento de APIs e Backend — Semana 03

Continuação da [Semana 02](../tarefa02/README.md). Esta versão mantém Spring Boot, JPA e a configuração PostgreSQL, mas usa DTOs para que a Entity `Aluno` não seja recebida nem devolvida diretamente pelos endpoints.

## Estrutura

- `dto/AlunoRequest.java`: entrada de `POST` e `PUT` com `nome`, `idade` e `ra`, sem campo `id`.
- `dto/AlunoResponse.java`: saída com `id`, `nome`, `idade` e `ra`.
- `controller/AlunoController.java`: mapeia as requisições e usa `ResponseEntity` para definir o status.
- `service/AlunoService.java`: valida, converte DTOs, executa o CRUD e trata os registros ausentes com `Optional`, `if/else` e `ResponseStatusException`, sem expressões lambda.
- `repository/AlunoRepository.java`: acesso ao PostgreSQL por `JpaRepository`.
- `model/Aluno.java`: Entity JPA persistida pelo Hibernate, sem exposição direta na API.

## Endpoints

| Método | Caminho | Sucesso | Ausência ou erro |
| --- | --- | --- | --- |
| POST | `/aluno` | 201 com `AlunoResponse` | 400 para dados inválidos; 409 para RA duplicado |
| GET | `/aluno/{id}` | 200 com `AlunoResponse` | 404 se não existir |
| PUT | `/aluno/{id}` | 204 sem corpo | 404 se não existir; 400/409 para dados inválidos |
| DELETE | `/aluno/{id}` | 204 sem corpo | 404 se não existir |
| GET | `/aluno` ou `/aluno?nome=Yuri` | 200 com lista de `AlunoResponse` | Lista vazia quando não houver resultados |

O endpoint anterior `/api/saudacao/ra` continua disponível e retorna `O RA do aluno Yuri Ribeiro Moraes é 027348`. O RA é `String` para preservar o zero inicial.

Exemplo de cadastro:

```bash
curl -i -X POST http://localhost:8080/aluno \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Aluno Exemplo","idade":20,"ra":"000123"}'
```

## Execução

Crie o banco PostgreSQL `techx_api` e configure as variáveis de ambiente. A senha fica fora do repositório. O Hibernate usa `ddl-auto=update` e mostra o SQL no console.

```bash
export DB_URL='jdbc:postgresql://localhost:5432/techx_api'
export DB_USERNAME='postgres'
export DB_PASSWORD='sua_senha'
./mvnw spring-boot:run
```

## Verificação

`./mvnw test` executa um teste de integração com MockMvc, Spring Data JPA e H2 em memória. Ele verifica o JSON dos DTOs, a persistência, filtro de listagem e os status 201, 200, 204, 400, 404 e 409 para o fluxo de criação, busca, atualização e exclusão. Esse teste passou em 13/10/2026. O build da aplicação também compilou. A conexão PostgreSQL continua configurada para execução normal; nesta entrega, o teste automatizado usa H2 em vez de uma instância PostgreSQL externa.
