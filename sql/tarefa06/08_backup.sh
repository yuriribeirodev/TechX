#!/usr/bin/env bash
# Backup lógico diário do sistema acadêmico (PostgreSQL).
set -Eeuo pipefail

umask 077
PGDATABASE="${PGDATABASE:-sistema_academico}"
BACKUP_DIR="${BACKUP_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/backups}"

if [[ ! "$PGDATABASE" =~ ^[a-zA-Z0-9_-]+$ ]]; then
    echo "Nome de banco inválido para compor o arquivo de backup." >&2
    exit 1
fi

for comando in pg_dump pg_restore find; do
    if ! command -v "$comando" >/dev/null 2>&1; then
        echo "Comando obrigatório não encontrado: $comando" >&2
        exit 1
    fi
done

mkdir -p "$BACKUP_DIR"
arquivo="$BACKUP_DIR/${PGDATABASE}_$(date +%Y-%m-%d_%H-%M-%S).dump"
temporario="$(mktemp "$BACKUP_DIR/.${PGDATABASE}_XXXXXX")"
trap 'rm -f "$temporario"' EXIT

if ! pg_dump -Fc --dbname="$PGDATABASE" --file="$temporario"; then
    echo "Falha no pg_dump; nenhum backup incompleto foi publicado." >&2
    exit 1
fi

if [[ ! -s "$temporario" ]] || ! pg_restore --list "$temporario" >/dev/null; then
    echo "O arquivo gerado está vazio ou não é um backup customizado válido." >&2
    exit 1
fi

mv "$temporario" "$arquivo"
trap - EXIT
echo "Backup criado: $arquivo"

# -mtime +29 seleciona arquivos com pelo menos 30 dias completos.
find "$BACKUP_DIR" -maxdepth 1 -type f \
    -name "${PGDATABASE}_*.dump" -mtime +29 -delete
