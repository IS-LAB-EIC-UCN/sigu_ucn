#!/usr/bin/env bash
set -euo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly MIGRATIONS_DIR="${SCRIPT_DIR}/migrations"

PGHOST="${PGHOST:-localhost}"
PGPORT="${PGPORT:-5432}"
PGUSER="${PGUSER:-postgres}"
PGDATABASE="${PGDATABASE:-sigu_ucn}"

if [[ -z "${PGPASSWORD:-}" ]]; then
  PGPASSWORD="postgres"
fi

export PGHOST PGPORT PGUSER PGPASSWORD PGDATABASE

log() {
  printf '\033[1;34m[migrate]\033[0m %s\n' "$*"
}

warn() {
  printf '\033[1;33m[migrate]\033[0m %s\n' "$*" >&2
}

err() {
  printf '\033[1;31m[migrate]\033[0m %s\n' "$*" >&2
}

precondiciones() {
  if ! command -v psql >/dev/null 2>&1; then
    err "psql no esta instalado. Instala el cliente de PostgreSQL."
    exit 1
  fi

  if ! psql -d "$PGDATABASE" -c "SELECT 1" >/dev/null 2>&1; then
    err "No se pudo conectar a la base de datos ${PGDATABASE} en ${PGHOST}:${PGPORT}."
    err "Verifica que 'docker compose up -d' haya terminado y que el puerto este accesible."
    exit 1
  fi
}

inicializar_control() {
  psql -d "$PGDATABASE" -v ON_ERROR_STOP=1 -q <<'SQL'
CREATE TABLE IF NOT EXISTS schema_migrations (
    version    VARCHAR(255) PRIMARY KEY,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
SQL
}

fue_aplicada() {
  local version="$1"
  local count
  count=$(psql -d "$PGDATABASE" -tA -c "SELECT COUNT(*) FROM schema_migrations WHERE version = '$version'")
  [[ "$count" -gt 0 ]]
}

aplicar() {
  local file="$1"
  local version
  version="$(basename "$file" .sql)"

  if fue_aplicada "$version"; then
    log "SKIP  $version (ya aplicada)"
    return 0
  fi

  log "APPLY $version"
  if ! psql -d "$PGDATABASE" -v ON_ERROR_STOP=1 -q -f "$file"; then
    err "Fallo al aplicar $version. Abortando."
    exit 1
  fi

  psql -d "$PGDATABASE" -v ON_ERROR_STOP=1 -q -c "INSERT INTO schema_migrations (version) VALUES ('$version')"
  log "OK    $version"
}

main() {
  log "Directorio de migraciones: $MIGRATIONS_DIR"
  log "Base de datos: $PGDATABASE en $PGHOST:$PGPORT (usuario $PGUSER)"

  precondiciones
  inicializar_control

  if [[ ! -d "$MIGRATIONS_DIR" ]]; then
    err "No existe el directorio $MIGRATIONS_DIR"
    exit 1
  fi

  local archivos
  archivos=$(find "$MIGRATIONS_DIR" -maxdepth 1 -type f -name 'V*.sql' | sort)
  if [[ -z "$archivos" ]]; then
    warn "No hay archivos V*.sql en $MIGRATIONS_DIR. Nada que hacer."
    return 0
  fi

  local total=0
  local aplicadas=0
  while IFS= read -r archivo; do
    total=$((total + 1))
    local version
    version="$(basename "$archivo" .sql)"
    if ! fue_aplicada "$version"; then
      aplicar "$archivo"
      aplicadas=$((aplicadas + 1))
    fi
  done <<<"$archivos"

  if [[ "$aplicadas" -eq 0 ]]; then
    log "Sin cambios. $total migraciones ya estaban aplicadas."
  else
    log "Listo. $aplicadas nuevas migraciones aplicadas de $total totales."
  fi
}

main "$@"
