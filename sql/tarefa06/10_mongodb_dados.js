// Executar com: mongosh 10_mongodb_dados.js
// Dados de demonstração independentes do PostgreSQL.
const banco = db.getSiblingDB("sistema_academico_sprint6");

const alunos = [
  { _id: "ALU001", nome: "Ana Souza", email: "ana@exemplo.com", contato: { telefone: "85999990001", cidade: "Fortaleza" }, interesses: ["banco de dados", "java"] },
  { _id: "ALU002", nome: "Carlos Lima", email: "carlos@exemplo.com", contato: { telefone: "85999990002", cidade: "Fortaleza" } },
  { _id: "ALU003", nome: "Mariana Alves", email: "mariana@exemplo.com", contato: { cidade: "Recife" }, bolsa: { tipo: "parcial", percentual: 50 } },
  { _id: "ALU004", nome: "João Silva", email: "joao@exemplo.com", contato: { telefone: "85999990004", cidade: "Natal" } },
  { _id: "ALU005", nome: "Beatriz Santos", email: "beatriz@exemplo.com", contato: { cidade: "Fortaleza" }, acessibilidade: { recurso: "legendas" } },
  { _id: "ALU006", nome: "Gabriel Costa", email: "gabriel@exemplo.com", contato: { telefone: "85999990006", cidade: "Recife" } },
];

const turmas = [
  { _id: "TUR001", codigo: "BD-01", disciplina: { nome: "Banco de Dados", carga_horaria: 60 }, professor: "Ricardo Santos", horario: { dias: ["segunda", "quarta"], inicio: "19:00" }, semestre: "2026.2" },
  { _id: "TUR002", codigo: "JAVA-01", disciplina: { nome: "Programação Java", carga_horaria: 80 }, professor: "Fernanda Costa", horario: { dias: ["terça", "quinta"], inicio: "19:00" }, semestre: "2026.2" },
  { _id: "TUR003", codigo: "ES-01", disciplina: { nome: "Engenharia de Software", carga_horaria: 60 }, professor: "Paulo Mendes", horario: { dias: ["sexta"], inicio: "18:00" }, semestre: "2026.2" },
  { _id: "TUR004", codigo: "ALG-01", disciplina: { nome: "Algoritmos", carga_horaria: 80 }, professor: "Juliana Rocha", horario: { dias: ["sábado"], inicio: "08:00" }, semestre: "2026.2" },
];

const matriculas = [
  { _id: "MAT001", aluno_id: "ALU001", turma_id: "TUR001", status: "ATIVA", data_matricula: ISODate("2026-09-01T00:00:00Z") },
  { _id: "MAT002", aluno_id: "ALU002", turma_id: "TUR001", status: "ATIVA", data_matricula: ISODate("2026-09-02T00:00:00Z") },
  { _id: "MAT003", aluno_id: "ALU003", turma_id: "TUR001", status: "ATIVA", data_matricula: ISODate("2026-09-03T00:00:00Z") },
  { _id: "MAT004", aluno_id: "ALU004", turma_id: "TUR002", status: "ATIVA", data_matricula: ISODate("2026-09-04T00:00:00Z") },
  { _id: "MAT005", aluno_id: "ALU005", turma_id: "TUR002", status: "ATIVA", data_matricula: ISODate("2026-09-05T00:00:00Z") },
  { _id: "MAT006", aluno_id: "ALU006", turma_id: "TUR003", status: "ATIVA", data_matricula: ISODate("2026-09-06T00:00:00Z") },
  { _id: "MAT007", aluno_id: "ALU001", turma_id: "TUR003", status: "ATIVA", data_matricula: ISODate("2026-09-07T00:00:00Z") },
  { _id: "MAT008", aluno_id: "ALU002", turma_id: "TUR004", status: "ATIVA", data_matricula: ISODate("2026-09-08T00:00:00Z") },
  { _id: "MAT009", aluno_id: "ALU003", turma_id: "TUR004", status: "ATIVA", data_matricula: ISODate("2026-09-09T00:00:00Z") },
  { _id: "MAT010", aluno_id: "ALU005", turma_id: "TUR004", status: "ATIVA", data_matricula: ISODate("2026-09-10T00:00:00Z") },
];

// Upsert por _id torna o exemplo repetível sem apagar outras coleções ou documentos.
for (const aluno of alunos) banco.alunos.replaceOne({ _id: aluno._id }, aluno, { upsert: true });
for (const turma of turmas) banco.turmas.replaceOne({ _id: turma._id }, turma, { upsert: true });
for (const matricula of matriculas) banco.matriculas.replaceOne({ _id: matricula._id }, matricula, { upsert: true });

// Índice simples para busca de aluno e composto para filtro + ordenação de matrículas.
banco.alunos.createIndex({ email: 1 }, { name: "idx_alunos_email", unique: true });
banco.matriculas.createIndex(
  { turma_id: 1, status: 1, data_matricula: -1 },
  { name: "idx_matriculas_turma_status_data" }
);

printjson({ alunos: banco.alunos.countDocuments({}), turmas: banco.turmas.countDocuments({}), matriculas: banco.matriculas.countDocuments({}) });
