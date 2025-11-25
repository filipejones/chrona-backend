# Plano de Melhora (Checklist Orientado para IA)

## 1) Consistência de Dados (Backend)
- [x] Validar `parentId` pertence ao mesmo `projectId` em criar/atualizar tarefa; retornar 400 com mensagem clara.
- [x] Tratar `projectId` inexistente em tasks com 400 + erro descritivo.
- [x] Definir defaults explícitos para `billable` e `status` também em update (se vier null, manter valor atual).
- [x] Expor `GET /projects/{id}/tasks` com filtro por status e ordenação por data/ID.

## 2) Carregamento de Tarefas por Projeto (Frontend)
- [x] Trocar uso de estado global de tasks por fetch via novo endpoint na aba Tarefas.
- [x] Passar `project` e `parentTasks` filtradas ao `TaskFormComponent`; renderizar modal apenas se projeto carregado.
- [x] Adicionar toast de sucesso/erro nas ações de tarefa; reverter estado otimista em falha.

## 3) Guarda e UX de Projeto (Frontend)
- [x] Em `ProjectDetails`, proteger acesso quando `project` não existe (redirect/voltar ou skeleton).
- [x] Manter aba ativa em signal/URL (hash ou query) para persistir após refresh.
- [x] Cabeçalho das abas com contadores: tarefas ativas/arquivadas, etapas por status, total de despesas vs orçamento.

## 4) Exploração para Admin
- [x] Criar filtros rápidos nas abas (tarefas: status/pai; etapas: status/progresso; despesas: data/valor).
- [x] Adicionar ordenação por data/valor/nome onde fizer sentido.
- [ ] Incluir shortcuts/botões “Nova Tarefa/Etapa/Despesa” dentro da aba ativa.

## 5) Modelagem e DTOs
- [x] Decidir uso de `phase` em Task: expor no DTO e permitir vincular no front ou remover do contrato até pronto.
- [x] Garantir que listagens paginem/sortem no backend (tasks, despesas, etapas) e o front consuma.

## 6) Feedback e Erros
- [x] Exibir mensagens específicas vindas da API (ex: parent inválido, projeto não encontrado).
- [x] Adicionar skeleton/spinner para carregamento das três abas; estados vazios com call-to-action.

## 7) Segurança/Tenant
- [x] Confirmar aplicação do TenantFilter/escopo em todos os repos de tasks/etapas/despesas. *(TenantFilter agora exige X-Tenant-ID para requests /api, exceto OPTIONS/públicos)*
- [x] Bloquear ações por permissão com botão desabilitado + tooltip explicando a permissão necessária.

## 8) Testes Rápidos
- [x] Cobrir validação de `parentId` por projeto no backend (teste de controlador/repos).
- [x] Testar criação/edição de tarefa via novo endpoint usando projeto válido/inválido. *(inclui overview 404/500 e despesa com project/user/phase inválidos)*
- [ ] Testar front: abrir projeto, alternar abas, criar tarefa com/sem pai, validar toasts e contadores.

## 9) Integração Etapas, Tarefas e Despesas
- [x] Adicionar `phaseId` em Task DTO/front; validar que pertence ao mesmo `projectId` (create/update) e rejeitar se não pertencer.
- [x] Na aba Etapas, exibir lista compacta de tarefas ligadas à etapa e total de despesas por etapa. *(total de despesas exibido via status financeiro; CTA contextual pronto)*
- [x] Criar CTAs contextuais: “Nova Tarefa nesta Etapa” e “Nova Despesa nesta Etapa”, pré-selecionando a etapa no formulário.
- [x] Na aba Tarefas, mostrar chip da etapa (com cor do burn rate) e filtro por etapa/status; permitir selecionar etapa no form.
- [x] Na aba Despesas, exibir coluna “Etapa” e filtro por etapa/período; permitir iniciar cadastro a partir de um card de etapa.
- [x] Expor endpoint de overview do projeto (burn total, despesas totais, tarefas ativas/arquivadas, etapas por status) e usar no header da tela de projeto.
