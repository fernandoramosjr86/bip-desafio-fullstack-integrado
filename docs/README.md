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
1. Executar scripts de banco (`db/schema.sql` e `db/seed.sql`) ou usar carga automatica do backend
2. Corrigir bug no BeneficioEjbService
3. Implementar backend CRUD + integração com EJB
4. Desenvolver frontend Angular consumindo backend
5. Implementar testes
6. Documentar (Swagger, README)
7. Submeter via repositório público próprio (template/clone), com histórico de commits

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
- Matriz de aderência à vaga: `docs/ADERENCIA-VAGA.md`

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
  - `./mvnw -B clean package`
- Rodar backend junto com dependências do reactor:
  - `./mvnw -pl backend-module -am spring-boot:run`

## 🐳 Execução com Docker (opcional)
- Subir backend + frontend em containers:
  - `docker compose up --build`
- Subir backend + frontend + broker JMS opcional:
  - `APP_TRANSFER_JMS_ENABLED=true docker compose --profile jms up --build`
  - PowerShell: `$env:APP_TRANSFER_JMS_ENABLED='true'; docker compose --profile jms up --build`
- Encerrar ambiente:
  - `docker compose down`
- O projeto continua suportando execução local sem Docker.

## 🗄️ Fonte oficial de schema/seed em runtime
- O backend carrega automaticamente, via `spring.sql.init`, os arquivos:
  - `backend-module/src/main/resources/db/schema.sql`
  - `backend-module/src/main/resources/db/seed.sql`
- A pasta `db/` na raiz continua como referência de setup manual.

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
- `GET /api/v1/beneficios/transferencias/historico?page=0&size=10`
- `GET /actuator/health`

## ✅ Testes implementados
- `backend-module`:
  - unitário da camada de aplicação (`BeneficioApplicationServiceTest`)
  - integração HTTP (`BackendApiIntegrationTest`)
- `ejb-module`:
  - unitário de regras e concorrência da transferência (`BeneficioEjbServiceTest`)
- `frontend`:
  - unitário da integração HTTP (`BeneficioApiService`)
  - unitário de comportamento da página de benefícios (`BeneficiosPageComponent`)

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
- O workflow `.github/workflows/ci.yml` publica os relatórios como artifact (`coverage-artifacts`).
- Os badges em `.github/badges/` são atualizados automaticamente a cada `push` em `main/master`.
- O quality gate de cobertura bloqueia o job quando ficar abaixo dos mínimos backend+EJB (`MIN_LINE_COVERAGE=80` e `MIN_BRANCH_COVERAGE=70`) e frontend (`MIN_FRONTEND_LINE_COVERAGE=70` e `MIN_FRONTEND_BRANCH_COVERAGE=60`).
- O pipeline também builda imagens Docker, roda scan Trivy (SARIF) e publica SBOM CycloneDX.

## 🏢 Aderência enterprise adicional
- JMS: publicação de evento de transferência para fila configurável (`app.transfer.jms.*`), com fallback para log quando desligado.
- Observabilidade: `X-Correlation-Id` em todas as respostas para rastreabilidade ponta a ponta.
- Logging: JSON estruturado com profile opcional para envio ao Graylog (`SPRING_PROFILES_ACTIVE=graylog`).
- Deploy corporativo: profile Maven `websphere` para empacotar WAR (`./mvnw -pl backend-module -Pwebsphere clean package`).

## 🔍 Como verificar rastreabilidade de transferências
1. API de histórico:
   - `GET /api/v1/beneficios/transferencias/historico?page=0&size=10`
2. Swagger:
   - `http://localhost:8080/swagger-ui/index.html`
3. Banco (H2):
   - `http://localhost:8080/h2-console`
   - JDBC: `jdbc:h2:mem:beneficiosdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
   - Usuário: `sa`
   - Senha: em branco
   - Query:
```sql
SELECT ID, BENEFICIO_ORIGEM_ID, BENEFICIO_DESTINO_ID, VALOR, EXECUTADO_EM
FROM TRANSFERENCIA_HISTORICO
ORDER BY EXECUTADO_EM DESC;
```
4. Logs e correlação:
   - header `X-Correlation-Id` para rastrear requisição nos logs estruturados JSON.
