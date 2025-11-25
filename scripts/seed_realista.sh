#!/usr/bin/env bash
# Seeder realista para Chrona (multi-tenant) via API
# Não executa nada automaticamente: revise antes de rodar.
# Uso típico:
#   chmod +x scripts/seed_realista.sh
#   ./scripts/seed_realista.sh
#
# Variáveis:
#   TENANTS="demo arqmax urbanbr sirius"   # lista de tenants a popular
#   API="http://localhost:8080/api/v1"
#   AUTH="http://localhost:8080/auth/login"
#   ADMIN_PASS="ChangeMe123!"              # senha padrão
#   USERS_PER_TENANT=12                    # quantos usuários (incluindo admin + chefes + membros)
#   CLIENTS_PER_TENANT=10                  # quantos clientes por tenant
#   PROJECTS_PER_CLIENT=2                  # quantos projetos por cliente
#   TASKS_PER_PROJECT=8
#   EXPENSES_PER_PROJECT=8
#
# O script cria: usuários (admin + chefes + membros), clientes, projetos,
# fases, tarefas e despesas, com dados em português.

set -euo pipefail

TENANTS="${TENANTS:-demo arqmax urbanbr sirius}"
ADMIN_PASS="${ADMIN_PASS:-ChangeMe123!}"
API="${API:-http://localhost:8080/api/v1}"
AUTH="${AUTH:-http://localhost:8080/auth/login}"
USERS_PER_TENANT="${USERS_PER_TENANT:-12}"
CLIENTS_PER_TENANT="${CLIENTS_PER_TENANT:-10}"
PROJECTS_PER_CLIENT="${PROJECTS_PER_CLIENT:-2}"
TASKS_PER_PROJECT="${TASKS_PER_PROJECT:-8}"
EXPENSES_PER_PROJECT="${EXPENSES_PER_PROJECT:-8}"

seed_tenant() {
  local TENANT="$1"
  local ADMIN_EMAIL="admin@${TENANT}.local"

  echo ">> [$TENANT] Obtendo token"
  TOKEN="$(curl -s -X POST "$AUTH" \
    -H "Content-Type: application/json" \
    -H "X-Tenant-ID: $TENANT" \
    -d "{\"email\":\"$ADMIN_EMAIL\",\"password\":\"$ADMIN_PASS\"}" | jq -r '.token')"

  if [[ -z "$TOKEN" || "$TOKEN" == "null" ]]; then
    echo "Erro ao obter token para $TENANT. Verifique tenant/creds." >&2
    return
  fi

  auth_curl() {
    curl -s "$@" -H "X-Tenant-ID: $TENANT" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
  }

  # Usuários: um admin (já existe), 2 chefes, resto membros
  declare -a user_emails=()
  chiefs=('chefe.obras' 'coordenador.projetos')
  membros=('engenheiro' 'arquiteto' 'designer' 'estagiario' 'coordenador' 'analista' 'modeladorBIM' 'projExec' 'gestorFinanceiro')
  echo ">> [$TENANT] Criando usuários chefes e membros (senha padrão: $ADMIN_PASS)"
  for c in "${chiefs[@]}"; do
    email="${c}@${TENANT}.local"
    auth_curl -X POST "$API/users" -d "{\"email\":\"$email\",\"name\":\"${c^}\",\"password\":\"$ADMIN_PASS\"}" >/dev/null || true
    user_emails+=("$email")
  done
  count=${#user_emails[@]}
  for m in "${membros[@]}"; do
    [[ $count -ge $USERS_PER_TENANT ]] && break
    email="${m}${count}@${TENANT}.local"
    auth_curl -X POST "$API/users" -d "{\"email\":\"$email\",\"name\":\"${m^} ${count}\",\"password\":\"$ADMIN_PASS\"}" >/dev/null || true
    user_emails+=("$email")
    ((count++))
  done

  # Clientes (nomes brasileiros)
  clients=('Construtora Horizonte' 'Arquitetura Plena' 'Urbanismo Brasil' 'Engenharia Atlântica' 'Studio Luz' 'Porto Engenharia' 'Planta & Obra' 'Habitat Urbano' 'Prime Arquitetos' 'ObraForte')
  declare -a client_ids=()
  echo ">> [$TENANT] Criando clientes"
  for i in $(seq 1 $CLIENTS_PER_TENANT); do
    name="${clients[$(( (i-1) % ${#clients[@]} ))]} $TENANT $i"
    id=$(auth_curl -X POST "$API/clients" -d "{\"name\":\"$name\",\"status\":\"Ativo\"}" | jq -r '.id')
    [[ "$id" != "null" ]] && client_ids+=("$id") && echo "   + $name (id=$id)"
  done

  # Modelos de projetos
  projects=(
    'Residencial Jardim Aurora'
    'Escritório Vista Paulista'
    'Complexo Esportivo Vila Verde'
    'Reforma Hospital São Lucas'
    'Condomínio Solar das Acácias'
    'Torre Empresarial Atlântica'
    'Centro Médico Bela Vista'
    'Campus Universitário Sul'
    'Hotel Costa Dourada'
    'Shopping Vale Verde'
  )

  declare -a project_ids=()
  echo ">> [$TENANT] Criando projetos"
  for cid in "${client_ids[@]}"; do
    for j in $(seq 1 $PROJECTS_PER_CLIENT); do
      pname="${projects[$(( RANDOM % ${#projects[@]} ))]} - Lote $j"
      pid=$(auth_curl -X POST "$API/projects" -d "{\"name\":\"$pname\",\"clientId\":$cid,\"status\":\"Ativo\"}" | jq -r '.id')
      [[ "$pid" != "null" ]] && project_ids+=("$pid") && echo "   + $pname (id=$pid, cliente=$cid)"
    done
  done

  # Etapas modelo
  phases_template=(
    '{"name":"Estudos Preliminares","description":"Levantamento, briefing e anteprojeto","budget":40000}'
    '{"name":"Projeto Executivo","description":"Detalhamento técnico completo","budget":80000}'
    '{"name":"Compatibilização","description":"Coordenação interdisciplinar","budget":25000}'
    '{"name":"Aprovação Legal","description":"Prefeitura e órgãos","budget":15000}'
  )

  declare -A phase_ids_by_project=()
  echo ">> [$TENANT] Criando etapas"
  for pid in "${project_ids[@]}"; do
    for ph in "${phases_template[@]}"; do
      ph_id=$(auth_curl -X POST "$API/phases" -d "$(jq -c --argjson ph "$ph" --arg pid "$pid" '{name:$ph.name,description:$ph.description,budget:$ph.budget,projectId:($pid|tonumber)}' <<< "$ph")" | jq -r '.id')
      [[ "$ph_id" != "null" ]] && phase_ids_by_project["$pid"]+="$ph_id " && echo "   + fase $ph_id no projeto $pid"
    done
  done

  # Tarefas por projeto/fase
  tasks_template=(
    'Levantamento de requisitos'
    'Modelagem BIM'
    'Renderizações 3D'
    'Projeto Elétrico'
    'Projeto Hidrossanitário'
    'Detalhamento de marcenaria'
    'Coordenação com obra'
    'Vistorias e RRT'
    'As built'
  )

  echo ">> [$TENANT] Criando tarefas"
  for pid in "${project_ids[@]}"; do
    read -ra phases_ids <<< "${phase_ids_by_project[$pid]}"
    for t in "${tasks_template[@]}"; do
      phase_ref="${phases_ids[$(( RANDOM % ${#phases_ids[@]} ))]}"
      auth_curl -X POST "$API/tasks" \
        -d "{\"name\":\"$t\",\"projectId\":$pid,\"phaseId\":$phase_ref,\"billable\":true,\"status\":\"Ativo\"}" >/dev/null
    done
  done

  echo ">> [$TENANT] Criando despesas"
  for pid in "${project_ids[@]}"; do
    read -ra phases_ids <<< "${phase_ids_by_project[$pid]}"
    for i in $(seq 1 $EXPENSES_PER_PROJECT); do
      phase_ref="${phases_ids[$(( RANDOM % ${#phases_ids[@]} ))]}"
      amount=$(( (RANDOM % 9000) + 1000 ))
      desc="Despesa operacional $i"
      auth_curl -X POST "$API/expenses" \
        -d "{\"description\":\"$desc\",\"amount\":$amount,\"date\":\"2025-11-$((10 + i))\",\"reimbursable\":false,\"projectId\":$pid,\"phaseId\":$phase_ref,\"userId\":1}" >/dev/null
    done
  done

  echo ">> [$TENANT] Resumo: clientes=${#client_ids[@]}, projetos=${#project_ids[@]}, usuários=${#user_emails[@]} (admin + chefes + membros)."
}

for t in $TENANTS; do
  seed_tenant "$t"
done

echo "Seed concluído para tenants: $TENANTS"
