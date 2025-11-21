# Chrona: Roteiro de Execução (Roadmap)

Este documento transforma a [Análise do Projeto](project_analysis.md) em um plano de trabalho acionável.

## Estrutura do Plano
Dividimos o trabalho em **4 Fases**. Cada fase tem um objetivo claro e pontos de decisão onde precisarei da sua aprovação.

---

## 🏁 Fase 1: Fundação Técnica (Modernização)
**Objetivo:** Garantir que o projeto seja robusto, fácil de rodar e testar antes de adicionarmos complexidade.
*Risco: Baixo | Valor: Alto (Produtividade)*

- [ ] **Dockerização Completa:** Criar `docker-compose.yml` para rodar Backend + Frontend + Banco com um comando.
- [ ] **CI/CD (GitHub Actions):** Pipeline que roda testes automaticamente a cada push.
- [ ] **Refatoração de Testes:** Garantir que os testes atuais passem e cobrir áreas críticas.

> **🚦 Decisão Necessária:** Você prefere usar GitHub Actions (padrão) ou tem outra ferramenta de CI preferida (GitLab CI, Jenkins)?

---

## 🏛️ Fase 2: O Nicho (Arquitetura)
**Objetivo:** Implementar as funcionalidades que tornam o produto vendável para arquitetos.
*Risco: Médio | Valor: Muito Alto (Diferencial de Mercado)*

- [ ] **Nova Entidade: Etapas (Phases):**
    - Criar tabela `phases` (Estudo Preliminar, Executivo, etc.).
    - Vincular `TimeEntry` a `Phase` (além de Task).
- [ ] **Nova Entidade: Despesas (Expenses):**
    - Criar tabela `expenses` (Valor, Data, Comprovante, Reembolsável?).
    - CRUD no Backend e Frontend.
- [ ] **Relatório de "Burn Rate":** Visualização de quanto do orçamento da etapa já foi consumido.

> **🚦 Decisão Necessária:** As "Etapas" devem ser padronizadas pelo sistema (impossível mudar) ou cada escritório cria as suas? (Sugestão: Padrão CAU/IAB pré-carregado, mas editável).

---

## 🚀 Fase 3: Escala (Multi-tenancy)
**Objetivo:** Preparar o sistema para ter 10, 100, 1000 clientes isolados.
*Risco: Alto (Mudança Estrutural) | Valor: Essencial para SaaS*

- [ ] **Arquitetura Schema-per-Tenant:**
    - Configurar Hibernate para Multi-tenancy.
    - Criar `TenantContext` para interceptar requisições e definir o schema.
- [ ] **Migração de Dados:** Script para mover dados existentes para um schema "default".
- [ ] **Isolamento de Segurança:** Garantir que o Usuário A nunca acesse dados do Schema B.

> **🚦 Decisão Necessária:** Confirmar a estratégia de "Schema-per-Tenant" (aprovada na análise, mas requer confirmação final antes de mexer no Core do banco).

---

## 👑 Fase 4: Backoffice (Super Admin)
**Objetivo:** Ferramentas para você gerenciar o negócio.
*Risco: Baixo | Valor: Operacional*

- [ ] **Painel Administrativo:** Novo app (ou rota admin) para ver todos os Tenants.
- [ ] **Gestão de Assinaturas:** Integração básica (mockada inicialmente) para status de pagamento.
- [ ] **Impersonation:** Botão "Acessar como este cliente".

---

## Próximos Passos Sugeridos
Recomendo começarmos pela **Fase 1**. Ter o ambiente Dockerizado vai facilitar muito testar as mudanças complexas da Fase 3.

**Podemos iniciar a Fase 1?**
