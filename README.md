# BIP Desafio Fullstack Integrado
![Line Coverage](.github/badges/coverage-line.svg) ![Branch Coverage](.github/badges/coverage-branch.svg)

Implementacao fullstack com:
- `ejb-module` (regras EJB/JPA de transferencia)
- `backend-module` (API REST Spring Boot em arquitetura hexagonal)
- `frontend` (Angular 17 + PrimeNG)

## Requisitos
- Java 17
- Maven 3.9+
- Node.js 18+ e npm 9+

## Como executar
1. Subir backend:
```bash
mvn -pl backend-module -am spring-boot:run
```
2. Em outro terminal, subir frontend:
```bash
cd frontend
npm install
npm start
```
3. Acessar:
- Frontend: `http://localhost:4200`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Banco e seed
- Para a execucao normal do backend, os scripts carregados automaticamente sao:
  - `backend-module/src/main/resources/db/schema.sql`
  - `backend-module/src/main/resources/db/seed.sql`
- A pasta `db/` permanece como referencia de setup manual do desafio.

## Testes
- Backend + EJB:
```bash
mvn -B clean verify
```
- Frontend:
```bash
cd frontend
npm test -- --watch=false --browsers=ChromeHeadless
```

## CI e cobertura
- Workflow: `.github/workflows/ci.yml`
- Builda e testa frontend + backend em todo push/PR.
- Gera artefatos de cobertura (JaCoCo e frontend).
- Atualiza badges de cobertura automaticamente na branch principal.
- Quality gate minimo:
  - Line coverage >= `80%`
  - Branch coverage >= `70%`

## Evidencias arquiteturais
- Arquitetura hexagonal backend: `docs/ARQUITETURA-HEXAGONAL.md`
- Visao geral e criterios do desafio: `docs/README.md`
