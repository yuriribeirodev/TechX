#!/usr/bin/env bash
# Restaura em um banco temporário e confere a contagem das 10 tabelas da Sprint 5.
set -Eeuo pipefail

if [[ $# -ne 1 || ! -f "$1" ]]; then
    echo "Uso: $0 /caminho/para/backup.dump" >&2
    exit 1
fi

for comando in pg_restore createdb dropdb psql; do
    if ! command -v "$comando" >/dev/null 2>&1; then
        echo "Comando obrigatório não encontrado: $comando" >&2
        exit 1
    fi
done

backup="$1"
pg_restore --list "$backup" >/dev/null
banco_teste="sprint6_restore_$(date +%Y%m%d_%H%M%S)_$$"
criado=0

limpar() {
    if [[ "$criado" -eq 1 ]]; then
        dropdb --if-exists "$banco_teste" || echo "Não foi possível remover $banco_teste" >&2
    fi
}
trap limpar EXIT

createdb "$banco_teste"
criado=1
pg_restore --exit-on-error --no-owner --no-acl --dbname="$banco_teste" "$backup"

contagens="$(psql -X -A -t -F '|' -v ON_ERROR_STOP=1 --dbname="$banco_teste" -c "
    SELECT 'aluno', COUNT(*) FROM aluno
    UNION ALL SELECT 'professor', COUNT(*) FROM professor
    UNION ALL SELECT 'disciplina', COUNT(*) FROM disciplina
    UNION ALL SELECT 'matricula', COUNT(*) FROM matricula
    UNION ALL SELECT 'curso', COUNT(*) FROM curso
    UNION ALL SELECT 'turma', COUNT(*) FROM turma
    UNION ALL SELECT 'avaliacao', COUNT(*) FROM avaliacao
    UNION ALL SELECT 'nota', COUNT(*) FROM nota
    UNION ALL SELECT 'frequencia', COUNT(*) FROM frequencia
    UNION ALL SELECT 'curso_disciplina', COUNT(*) FROM curso_disciplina;")"

linhas=0
while IFS='|' read -r tabela quantidade; do
    [[ -n "$tabela" ]] || continue
    echo "$tabela: $quantidade registros"
    if [[ ! "$quantidade" =~ ^[0-9]+$ ]] || (( quantidade < 15 )); then
        echo "Verificação falhou para a tabela $tabela." >&2
        exit 1
    fi
    (( linhas += 1 ))
done <<< "$contagens"

if (( linhas != 10 )); then
    echo "Verificação incompleta: esperadas 10 tabelas, encontradas $linhas." >&2
    exit 1
fi

echo "Restauração íntegra: 10 tabelas verificadas no banco temporário $banco_teste."
