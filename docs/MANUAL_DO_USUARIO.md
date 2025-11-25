# Manual do Usuário - Sistema Chrona
> **Versão**: 1.0  
> **Última Atualização**: 23/11/2025  
> **Público**: Usuários internos e futuros clientes
## 📋 Índice
1. [Visão Geral do Sistema](#visão-geral)
2. [Conceitos Fundamentais](#conceitos-fundamentais)
3. [Fluxo de Trabalho Completo](#fluxo-de-trabalho)
4. [Funcionalidades por Tela](#funcionalidades)
5. [Perguntas Frequentes](#faq)
---
## Visão Geral do Sistema {#visão-geral}
O **Chrona** é um sistema de gestão de projetos desenvolvido especificamente para escritórios de arquitetura. Ele permite:
- Gerenciar clientes e projetos
- Organizar trabalho em tarefas hierárquicas
- Definir etapas orçamentárias
- Apontar horas trabalhadas
- Controlar despesas
- Monitorar burn rate (consumo de orçamento)
### Arquitetura de Informação
Cliente └─ Projeto ├─ Tarefas (organização do trabalho) ├─ Etapas (controle orçamentário) ├─ Despesas (gastos) └─ Apontamentos (horas trabalhadas em tarefas)

---
## Conceitos Fundamentais {#conceitos-fundamentais}
### 🏢 Cliente
Empresa ou pessoa que contrata projetos.
- **Campos**: Nome, Status (Ativo/Inativo), Contato, Endereço, Notas
- **Status**: Clientes inativos não podem ter novos projetos
### 📁 Projeto  
Um trabalho contratado por um cliente.
- **Campos**: Nome, Cliente, Status (Ativo/Pausado/Concluído)
- **Usado para**: Agrupar todo o trabalho relacionado a um contrato
### ✅ Tarefa
Item de trabalho executável. Pode ter sub-tarefas (hierárquico).
- **Campos**: Nome, Projeto, Tarefa Pai, Faturável (Sim/Não), Status
- **Usado para**: Organizar o trabalho e receber apontamentos de horas
- **Exemplo**: "Levantamento de Requisitos" → "Entrevista com Cliente"
### 🎯 Etapa  
Bloco orçamentário de um projeto (também chamado de Phase).
- **Campos**: Nome, Descrição, Orçamento, Status
- **Usado para**: Controle financeiro e planejamento
- **Exemplo**: "Discovery" (orçamento R$ 10.000), "Development" (orçamento R$ 50.000)
### 💰 Despesa
Gasto direto do projeto (materiais, serviços terceirizados, etc.).
- **Campos**: Descrição, Data, Valor, Projeto
- **Impacta**: Burn rate da etapa
### ⏱️ Apontamento (Time Entry)
Registro de horas trabalhadas em uma tarefa.
- **Campos**: Tarefa, Data, Duração, Notas
- **Impacta**: Burn rate da etapa (horas × custo hora)
---
## Fluxo de Trabalho Completo {#fluxo-de-trabalho}
### 1️⃣ Cadastro Inicial
Criar Cliente
Criar Projeto para o Cliente
Definir Etapas do Projeto (com orçamentos)
Criar Tarefas do Projeto
### 2️⃣ Execução
Equipe aponta horas nas Tarefas (tela Apontamentos)
Despesas são registradas conforme surgem
Sistema calcula automaticamente o burn rate
### 3️⃣ Monitoramento
Ver Detalhes do Projeto
Verificar burn rate nas Etapas
Ajustar planejamento se necessário
---
## Funcionalidades por Tela {#funcionalidades}
### 🏠 Dashboard
**Quando usar**: Primeira tela ao fazer login. Visão geral do sistema.
**O que mostra**:
- Resumo de projetos ativos
- Horas apontadas recentemente
- Períodos de aprovação pendentes (se você for gestor)
**Ações disponíveis**: Nenhuma. É apenas visualização.
---
### 📊 Apontamentos
**Quando usar**: Para registrar horas trabalhadas diariamente.
**Fluxo de uso**:
1. Clique em **"Novo Apontamento"**
2. Selecione a **Tarefa** (automaticamente mostra o Projeto)
3. Informe a **Data** e **Duração** (ex: 2.5 horas)
4. Adicione **Notas** (opcional mas recomendado)
5. Clique em **"Salvar"**
**Dicas**:
- Aponte horas diariamente para não esquecer
- Use notas para descrever o que foi feito
- Você só pode apontar em tarefas de projetos ativos
**Permissões necessárias**: `timesheet:own:create`
---
### 📁 Projetos e Tarefas
**Quando usar**: Para visualizar, criar e gerenciar projetos.
#### Tela: Lista de Projetos
**O que mostra**:
- Todos os projetos cadastrados
- Cliente de cada projeto
- Lista de tarefas de cada projeto (hierárquica)
**Ações disponíveis**:
- **"Novo Projeto"**: Cria um projeto
  - Selecione o **Cliente**
  - Informe **Nome** e **Status**
  - Clique em "Salvar"
- **"Nova Tarefa"** (dentro de cada projeto):
  - Informe **Nome** da tarefa
  - Marque se é **Faturável**
  - Selecione **Tarefa Pai** (opcional, para criar sub-tarefa)
  - Clique em "Salvar"
- **"Ver Detalhes"**: Abre a tela de detalhes do projeto
**Dica**: Organize tarefas de forma hierárquica. Ex:
✅ Projeto Arquitetônico ├─ Levantamento │ ├─ Visita ao terreno │ └─ Medições └─ Desenvolvimento ├─ Plantas baixas └─ Cortes

---
#### Tela: Detalhes do Projeto
**Quando usar**: Para ver tudo relacionado a um projeto específico.
**Estrutura (TABS)**:
1️⃣ **Aba TAREFAS**
- Lista todas as tarefas do projeto
- **"Nova Tarefa"**: Criar tarefa
- **Editar** (ícone lápis): Modificar tarefa
- **Arquivar** (ícone lixeira): Inativar tarefa
2️⃣ **Aba ETAPAS**  
- Lista etapas orçamentárias
- Mostra **barra de progresso** (burn rate)
  - 🟢 Verde: < 80% do orçamento consumido
  - 🟡 Amarelo: 80-100% consumido
  - 🔴 Vermelho: > 100% (estouro!)
- **"Nova Etapa"**: Criar etapa
  - Informe **Nome**, **Descrição**, **Orçamento**
  - Status: Não Iniciada/Em Andamento/Concluída/Em Espera
3️⃣ **Aba DESPESAS**
- Lista despesas do projeto
- **"Nova Despesa"**: Registrar gasto
  - Descrição, Data, Etapa (opcional), Valor
- Total de despesas exibido
**Fluxo recomendado**:
Criar Etapas com orçamentos
Criar Tarefas para organizar trabalho
Equipe aponta horas nas Tarefas 4.Registrar Despesas conforme surgem
Monitorar burn rate nas Etapas
---
### 👥 Clientes
**Quando usar**: Para gerenciar a carteira de clientes.
**Ações disponíveis**:
- **"Novo Cliente"**: Cadastrar cliente
  - Nome, Contato (nome/email/telefone)
  - Endereço, Notas
- **Editar**: Modificar dados
- **Arquivar**: Inativar cliente (não pode ter projetos ativos)
**Regra importante**: Não é possível arquivar cliente com projetos ativos.
---
### ✔️ Aprovações
**Quando usar**: Apenas para gestores. Aprovar períodos de horas da equipe.
**O que mostra**: Lista de períodos submetidos pela equipe
**Ações**:
- **"Aprovar"**: Confirma as horas do período
- **"Rejeitar"**: Devolve para correção (exige motivo)
**Permissões necessárias**: `timesheet:approve`
---
### ⚙️ Configurações
**Quando usar**: Ajustar preferências da organização.
**Seções**:
- **Dados da Organização**: Nome, configurações gerais
- **Usuários**: Gerenciar equipe (se admin)
- **Roles e Permissões**: Configurar acessos (se admin)
---
### 👤 Admin (Backoffice)
**Quando usar**: Apenas para administradores do sistema. Multi-tenancy.
**Funcionalidades**:
- Listar todos os tenants (organizações)
- Criar novo tenant
- Impersonar tenant (entrar como admin daquele tenant)
**Permissões necessárias**: Usuário deve ser admin global
---
## Perguntas Frequentes {#faq}
### ❓ Qual a diferença entre Tarefa e Etapa?
- **Tarefa**: Item de trabalho executável. Recebe apontamento de horas.
- **Etapa**: Bloco orçamentário. Controla financeiro.
**Exemplo prático**:
- Etapa "Discovery" (orçamento R$ 10.000)
  - Tarefa "Entrevista com cliente" (3h apontadas)
  - Tarefa "Pesquisa de mercado" (5h apontadas)
### ❓ Como criar sub-tarefas?
Na hora de criar/editar tarefa, selecione a "Tarefa Pai". A tarefa será criada como filha.
### ❓ O que é "Faturável"?
Indica se aquela tarefa será cobrada do cliente. Tarefas não-faturáveis (ex: reuniões internas) não entram no cálculo de burn rate.
### ❓ Como funciona o burn rate?
Burn rate = (Horas × Custo Hora + Despesas) / Orçamento da Etapa
**Exemplo**:
- Etapa "Development": R$ 50.000
- Horas apontadas: 100h × R$ 150/h = R$ 15.000
- Despesas: R$ 5.000
- **Burn rate**: (15.000 + 5.000) / 50.000 = **40%** 🟢
### ❓ Posso excluir um projeto?
Não há função de exclusão. Você pode inativar (Status = Concluído/Pausado).
### ❓ Como desfazer um apontamento?
Edite o apontamento ou exclua-o (se tiver permissão `timesheet:own:delete`).
---
## 📝 Notas para Manutenção Futura
**Este manual deve ser atualizado sempre que**:
- Nova funcionalidade for adicionada
- Funcionalidade existente for modificada
- Novo fluxo de trabalho for criado
- Permissões forem adicionadas/removidas
**Formato de atualização**:
```markdown
## [Data] - [Funcionalidade Alterada]
**O que mudou**: Descrição clara
**Como usar agora**: Instruções atualizadas
Fim do Manual v1.0
```
