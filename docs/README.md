# 🏗️ Desafio Fullstack Integrado
![Line Coverage](../.github/badges/coverage-line.svg) ![Branch Coverage](../.github/badges/coverage-branch.svg)
🚨 Instrução Importante (LEIA ANTES DE COMEÇAR)
❌ NÃO faça fork deste repositório.

Este repositório é fornecido como modelo/base. Para realizar o desafio, você deve:
✅ Opção correta (obrigatória)
  Clique em “Use this template” (se este repositório estiver marcado como Template)
OU
  Clone este repositório e crie um NOVO repositório público em sua conta GitHub.
📌 O resultado deve ser um repositório próprio, independente deste.

## 🎯 Objetivo
Criar solução completa em camadas (DB, EJB, Backend, Frontend), corrigindo bug em EJB e entregando aplicação funcional.

## 📦 Estrutura
- db/: scripts schema e seed
- ejb-module/: serviço EJB com bug a ser corrigido
- backend-module/: backend Spring Boot
- frontend/: app Angular
- docs/: instruções e critérios
- .github/workflows/: CI

## ✅ Tarefas do candidato
1. Executar db/schema.sql e db/seed.sql
2. Corrigir bug no BeneficioEjbService
3. Implementar backend CRUD + integração com EJB
4. Desenvolver frontend Angular consumindo backend
5. Implementar testes
6. Documentar (Swagger, README)
7. Submeter via fork + PR

## 🐞 Bug no EJB
- Transferência não verifica saldo, não usa locking, pode gerar inconsistência
- Espera-se correção com validações, rollback, locking/optimistic locking

## 📊 Critérios de avaliação
- Arquitetura em camadas (20%)
- Correção EJB (20%)
- CRUD + Transferência (15%)
- Qualidade de código (10%)
- Testes (15%)
- Documentação (10%)
- Frontend (10%)

## 🧱 Arquitetura Hexagonal (base backend)
- Implementada no módulo `backend-module`
- Separação em:
  - `domain` (modelo e regras)
  - `application` (casos de uso + portas)
  - `adapters` (HTTP e persistência)
- Porta de transferência integrada ao `ejb-module` via adapter de saída
- Documento detalhado em `docs/ARQUITETURA-HEXAGONAL.md`

## ▶️ Build e execução (Maven)
- Build completo dos módulos:
  - `mvn -B clean package`
- Rodar backend junto com dependências do reactor:
  - `mvn -pl backend-module -am spring-boot:run`

## 🔎 OpenAPI e Swagger
- OpenAPI JSON:
  - `GET /v3/api-docs`
- Swagger UI:
  - `GET /swagger-ui/index.html`

## 🌐 Endpoints principais
- `GET /api/v1/beneficios?page=0&size=10` (paginado)
- `GET /api/v1/beneficios/{id}`
- `POST /api/v1/beneficios`
- `PUT /api/v1/beneficios/{id}`
- `DELETE /api/v1/beneficios/{id}`
- `POST /api/v1/beneficios/transferencias`

## ✅ Testes implementados
- `backend-module`:
  - unitário da camada de aplicação (`BeneficioApplicationServiceTest`)
  - integração HTTP (`BackendApiIntegrationTest`)
- `ejb-module`:
  - unitário de regras e concorrência da transferência (`BeneficioEjbServiceTest`)

## 💻 Frontend Angular implementado
- Local: `frontend/`
- Funcionalidades:
  - CRUD de benefícios
  - transferência entre benefícios
  - paginação server-side
  - interface migrada para PrimeNG
  - consumo de API via proxy local (`/api -> http://localhost:8080`)
- Execução:
  - `cd frontend`
  - `npm install`
  - `npm start`

## 📈 Cobertura automatizada no CI
- JaCoCo é executado no `verify` dos módulos `backend-module` e `ejb-module`.
- O workflow `.github/workflows/ci.yml` publica os relatórios como artifact (`jacoco-reports`).
- Os badges em `.github/badges/` são atualizados automaticamente a cada `push` em `main/master`.
