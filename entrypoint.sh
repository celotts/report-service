#!/usr/bin/env bash
set -euo pipefail

function wait_for_service() {
  local name="$1"
  local host_port="$2"
  local timeout="${3:-30}"

  echo "🕒 Esperando $name en $host_port (timeout ${timeout}s)..."
  bash ./wait-for-it.sh "$host_port" --timeout="$timeout" --strict -- \
    echo "✅ $name está disponible."
}

# === Espera por dependencias ===
wait_for_service "registry-service" "registry-service:8761" 60
wait_for_service "config-service" "config-service:7777" 60
wait_for_service "patient-service" "patient-service:8081" 120

# === Lanzar app ===
echo "🚀 Iniciando app.jar..."
exec java -jar app.jar