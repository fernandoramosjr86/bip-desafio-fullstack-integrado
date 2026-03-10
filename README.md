# BIP Desafio Fullstack Integrado
![Line Coverage](.github/badges/coverage-line.svg) ![Branch Coverage](.github/badges/coverage-branch.svg)

Implementacao fullstack com:
- `ejb-module` (regras EJB/JPA de transferencia)
- `backend-module` (API REST Spring Boot em arquitetura hexagonal)
- `frontend` (Angular 17 + PrimeNG)
- observabilidade com `X-Correlation-Id` + logs estruturados JSON
- historico de transferencias persistido em banco
- publicacao de eventos de transferencia via JMS (opcional por configuracao)

## Requisitos
- Java 17
- Maven 3.9+ (ou usar `./mvnw` / `mvnw.cmd`)
- Node.js 18+ e npm 9+

## Como executar
### Sem Docker (modo local)
1. Subir backend:
```bash
./mvnw -pl backend-module -am spring-boot:run
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
- Health: `http://localhost:8080/actuator/health`

### Com Docker (modo container)
1. Subir stack completa:
```bash
docker compose up --build
```
2. Acessar:
- Frontend: `http://localhost:4200`
- Backend API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- Health backend: `http://localhost:8080/actuator/health`

2. Subir stack com broker JMS opcional:
```bash
APP_TRANSFER_JMS_ENABLED=true docker compose --profile jms up --build
```
No PowerShell (Windows):
```powershell
$env:APP_TRANSFER_JMS_ENABLED='true'; docker compose --profile jms up --build
```
- Broker (console Artemis): `http://localhost:8161`

3. Parar stack:
```bash
docker compose down
```

## Banco e seed
- Para a execucao normal do backend, os scripts carregados automaticamente sao:
  - `backend-module/src/main/resources/db/schema.sql`
  - `backend-module/src/main/resources/db/seed.sql`
- A pasta `db/` permanece como referencia de setup manual do desafio.

## Testes
- Backend + EJB:
```bash
./mvnw -B clean verify
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
- Builda as imagens Docker de backend e frontend.
- Executa scan de vulnerabilidades com Trivy (SARIF).
- Gera SBOM CycloneDX das imagens e publica como artifact.
- Quality gate minimo:
  - Line coverage >= `80%`
  - Branch coverage >= `70%`
  - Frontend line coverage >= `70%`
  - Frontend branch coverage >= `60%`

## Evidencias arquiteturais
- Arquitetura hexagonal backend: `docs/ARQUITETURA-HEXAGONAL.md`
- Visao geral e criterios do desafio: `docs/README.md`

## Recursos enterprise implementados
- JMS: evento de transferencia publicado para fila configuravel (`app.transfer.jms.queue-name`).
- Historico: endpoint `GET /api/v1/beneficios/transferencias/historico?page=0&size=10`.
- Observabilidade: `CorrelationIdFilter` adiciona `X-Correlation-Id` em toda resposta.
- Graylog: profile `graylog` em `logback-spring.xml` com envio UDP para GELF/Logstash.
- WebSphere readiness: profile Maven `websphere` para gerar WAR.

## Como verificar rastreabilidade de transferencias
1. Via API (principal):
```bash
curl "http://localhost:8080/api/v1/beneficios/transferencias/historico?page=0&size=10"
```
2. Via Swagger:
- `http://localhost:8080/swagger-ui/index.html`
- Endpoint: `GET /api/v1/beneficios/transferencias/historico`
3. Via banco (H2 console):
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:beneficiosdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Usuario: `sa`
- Senha: em branco
```sql
SELECT ID, BENEFICIO_ORIGEM_ID, BENEFICIO_DESTINO_ID, VALOR, EXECUTADO_EM
FROM TRANSFERENCIA_HISTORICO
ORDER BY EXECUTADO_EM DESC;
```
4. Via logs/correlacao:
- Use o header `X-Correlation-Id` da resposta para correlacionar requisicoes e logs JSON.

## Perfis opcionais
- Empacotar WAR para WebSphere:
```bash
./mvnw -pl backend-module -Pwebsphere clean package
```
- Ativar publicacao JMS:
```yaml
app:
  transfer:
    jms:
      enabled: true
```
- Ativar envio para Graylog:
```bash
SPRING_PROFILES_ACTIVE=graylog
GRAYLOG_HOST=localhost
GRAYLOG_PORT=12201
```
