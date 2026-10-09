// Executar com: mongosh 11_mongodb_consultas.js
const banco = db.getSiblingDB("sistema_academico_sprint6");

print("Aluno por email: filtro e projeção");
printjson(banco.alunos.find({ email: "ana@exemplo.com" }, { _id: 0, nome: 1, email: 1, contato: 1 }).toArray());

print("Matrículas ativas da turma BD-01: filtro, ordenação e projeção");
printjson(banco.matriculas.find(
  { turma_id: "TUR001", status: "ATIVA" },
  { _id: 0, aluno_id: 1, data_matricula: 1 }
).sort({ data_matricula: -1 }).toArray());

print("Turmas do semestre: filtro em documento e projeção");
printjson(banco.turmas.find(
  { semestre: "2026.2", "disciplina.carga_horaria": { $gte: 60 } },
  { _id: 0, codigo: 1, "disciplina.nome": 1, horario: 1 }
).sort({ codigo: 1 }).toArray());

print("Referencing: matrículas com dados dos alunos via $lookup");
printjson(banco.matriculas.aggregate([
  { $match: { turma_id: "TUR001" } },
  { $lookup: { from: "alunos", localField: "aluno_id", foreignField: "_id", as: "aluno" } },
  { $unwind: "$aluno" },
  { $project: { _id: 0, turma_id: 1, aluno: "$aluno.nome", status: 1 } },
]).toArray());

// executionStats mostra plano, totalDocsExamined e nReturned.
// Com poucos documentos, o otimizador pode escolher COLLSCAN; não se presume ganho.
print("EXPLAIN: índice simples em email");
printjson(banco.alunos.find({ email: "ana@exemplo.com" }).explain("executionStats"));

print("EXPLAIN: índice composto em turma, status e data");
printjson(banco.matriculas.find(
  { turma_id: "TUR001", status: "ATIVA" }
).sort({ data_matricula: -1 }).explain("executionStats"));
