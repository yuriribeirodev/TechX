# Sistema Acadêmico — Sprint 6

Continuação da [Sprint 5](../tarefa05/README.md). Os arquivos `01_ddl.sql` a `07_explain.sql` preservam a estrutura PostgreSQL da entrega anterior; esta pasta acrescenta backup, teste de restauração e um modelo documental de demonstração em MongoDB. Os dados MongoDB são uma amostra independente, sem sincronização automática com o PostgreSQL.

## Domínio e modelo lógico

O sistema registra alunos, professores, cursos, disciplinas, turmas, matrículas, avaliações, notas e frequência. No PostgreSQL, as dez tabelas normalizadas são `aluno`, `professor`, `curso`, `disciplina`, `turma`, `matricula`, `avaliacao`, `nota`, `frequencia` e `curso_disciplina`. Chaves estrangeiras ligam professor a disciplina, disciplina a turma, aluno a matrícula, turma a avaliação e frequência, avaliação a nota e curso a disciplina. `matricula` e `curso_disciplina` representam relações entre entidades. `01_ddl.sql` define chaves, unicidade e verificações; `03_indices.sql` a `07_explain.sql` apresentam índices, consultas, transações, permissões e análise de planos.

## Backup PostgreSQL

`08_backup.sh` usa `pg_dump -Fc` para criar um arquivo customizado com data e hora no nome. O arquivo é escrito primeiro em um caminho temporário; o script verifica que ele não está vazio e que `pg_restore --list` consegue lê-lo antes de publicá-lo. Em caso de erro, remove o temporário e retorna status diferente de zero. Depois do sucesso, apaga apenas arquivos `sistema_academico_*.dump` com pelo menos 30 dias completos dentro de `BACKUP_DIR`.

Por padrão, o banco é `sistema_academico` e a pasta é `backups/` ao lado do script. `PGDATABASE` e `BACKUP_DIR` permitem configurar ambos. Use um usuário de PostgreSQL com acesso de leitura ao banco e autenticação por arquivo `.pgpass` com permissão `0600`; não coloque senha no script nem no crontab. O diretório de backup deve ter espaço e acesso restrito. O script não cria o banco nem inicializa as tabelas.

Exemplo de execução:

```bash
PGDATABASE=sistema_academico BACKUP_DIR=/var/backups/sistema_academico ./08_backup.sh
```

Entrada de crontab para execução diária às 02:00, no fuso horário do servidor:

```cron
PATH=/usr/local/bin:/usr/bin:/bin
PGDATABASE=sistema_academico
BACKUP_DIR=/var/backups/sistema_academico
0 2 * * * /caminho/absoluto/TechX/sql/tarefa06/08_backup.sh >> /var/log/sistema_academico_backup.log 2>&1
```

A regra **3-2-1** pede três cópias dos dados, em dois tipos de mídia, com uma cópia fora do local principal. Este script produz a cópia lógica local; para cumprir a regra completa, também é necessário copiar os backups para outro meio e para um destino externo, monitorar o agendamento e testar periodicamente a restauração. A retenção local de 30 dias não substitui a política de retenção do destino externo.

## Restauração e comprovação

`09_teste_restauracao.sh` recebe um arquivo `.dump`, valida o formato, cria um banco temporário, executa `pg_restore --exit-on-error --no-owner --no-acl`, consulta a contagem das dez tabelas e exige pelo menos 15 registros em cada uma. Ao terminar, remove o banco temporário, inclusive quando há falha. A conta usada precisa poder criar e remover bancos. O teste de contagem comprova que as tabelas e amostras foram recuperadas; uma validação de produção também deve conferir dados críticos e a aplicação.

```bash
./09_teste_restauracao.sh /var/backups/sistema_academico/sistema_academico_2026-10-09_02-00-00.dump
```

**Teste executado em 09/10/2026:** em PostgreSQL 16, os scripts `01_ddl.sql`, `02_inserts.sql` e `03_indices.sql` foram carregados em `sistema_academico`. O backup customizado foi gerado por `08_backup.sh` e restaurado por `09_teste_restauracao.sh` em um banco temporário. Resultado observado: `aluno`, `professor`, `disciplina`, `matricula`, `curso`, `turma`, `avaliacao`, `nota`, `frequencia` e `curso_disciplina` retornaram **15 registros cada**. O banco de teste foi removido pelo script. O arquivo `.dump` de teste fica apenas no ambiente local e não deve ser versionado.

## MongoDB: modelagem e consultas

`10_mongodb_dados.js` cria ou atualiza **20 documentos** em três collections: seis `alunos`, quatro `turmas` e dez `matriculas`. O mesmo `_id` em cada execução evita duplicar os exemplos. Alguns alunos possuem `bolsa`, `interesses` ou `acessibilidade` e outros não, demonstrando campos opcionais sem alterar uma tabela.

- **Embedding:** `contato` fica dentro de `alunos`; `disciplina` e `horario` ficam dentro de `turmas`. Esses dados são pequenos, limitados e normalmente lidos junto com o documento principal. O nome da disciplina no documento de turma funciona como uma cópia de demonstração: se o cadastro central mudar, a aplicação precisará atualizá-la.
- **Referencing:** cada documento de `matriculas` guarda `aluno_id` e `turma_id` com os valores de `_id` das outras collections. A matrícula cresce separadamente e é consultada tanto por aluno quanto por turma; não convém manter uma lista ilimitada embutida em um aluno ou turma. MongoDB não impõe chaves estrangeiras nessas referências, então a aplicação deve validar sua existência. `11_mongodb_consultas.js` usa `$lookup` para recuperar os nomes dos alunos.

Os scripts podem ser executados com `mongosh` conectado à instância desejada:

```bash
mongosh 10_mongodb_dados.js
mongosh 11_mongodb_consultas.js
```

As consultas incluem `find` com filtros, projeção, `sort` e `$lookup`. Há um índice simples e único em `alunos.email` e um composto em `matriculas` por `turma_id`, `status` e `data_matricula` decrescente. O `explain("executionStats")` permite conferir `winningPlan`, `nReturned`, `totalKeysExamined` e `totalDocsExamined`; planos e tempos dependem dos dados e do servidor.

**Teste executado em 09/10/2026:** em MongoDB 7, os scripts inseriram 6 alunos, 4 turmas e 10 matrículas. As consultas e o `$lookup` retornaram dados; os dois `explain()` mostraram `IXSCAN`. Na busca por email, foram examinadas 1 chave de índice e 1 documento para 1 resultado. Na busca por turma e status, foram examinadas 3 chaves de índice e 3 documentos para 3 resultados. Com essa amostra pequena, esses números apenas demonstram o plano observado, sem medir desempenho em produção.

## PostgreSQL ou MongoDB neste domínio

| Situação | Escolha e motivo |
| --- | --- |
| Matrícula, notas e frequência oficiais | **PostgreSQL:** chaves estrangeiras, unicidade e transações ajudam a impedir referências inválidas e atualizações parciais. |
| Relatórios acadêmicos que cruzam alunos, disciplinas e cursos | **PostgreSQL:** relacionamentos normalizados, `JOIN`, agregações e consultas SQL facilitam a análise. |
| Perfil do aluno com campos opcionais e visualização conjunta de contato | **MongoDB:** documentos acomodam campos variáveis e embedding reduz a necessidade de compor dados em leituras simples. |
| Visão de turma com horário e informações consultadas juntas | **MongoDB:** a leitura do documento reúne os dados; referências de matrícula mantêm o crescimento separado. |

O PostgreSQL permanece como fonte adequada para os registros oficiais desta atividade. O MongoDB ilustra uma alternativa documental para padrões de leitura específicos; não há replicação entre os dois bancos neste projeto.
