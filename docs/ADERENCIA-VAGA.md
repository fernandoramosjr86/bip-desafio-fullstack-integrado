# Aderencia Tecnica a Vaga (Resumo Objetivo)

## Backend Java / EJB / JPA
- `ejb-module`: regra de transferencia com validacoes de saldo, status e locking pessimista.
- `backend-module`: API REST em arquitetura hexagonal com portas e adapters.
- Persistencia com Spring Data JPA + H2 para execucao local.

## API REST
- CRUD completo de beneficios.
- Transferencia entre beneficios.
- Historico de transferencias com paginacao.
- OpenAPI + Swagger publicados.

## Banco relacional e consultas
- Modelagem em `BENEFICIO` e `TRANSFERENCIA_HISTORICO`.
- Paginacao server-side e ordenacao deterministica.

## Mensageria JMS
- Publicacao de evento de transferencia para fila configuravel.
- Listener JMS para consumo e log operacional.
- Fallback seguro para log quando JMS estiver desativado.

## Frontend Angular
- Angular 17 + PrimeNG.
- Estrutura por `core` e `features`.
- Roteamento lazy da feature de beneficios.

## Testes e qualidade
- Testes unitarios e de integracao em backend/ejb/frontend.
- CI com build/test de frontend e backend.
- JaCoCo + badges + quality gate de cobertura.

## Observabilidade
- Header `X-Correlation-Id` injetado em toda requisicao.
- Logs JSON estruturados.
- Profile opcional para envio ao Graylog.

## Deploy corporativo
- Profile Maven `websphere` para empacotar WAR.
- Classe `SpringBootServletInitializer` para container deploy.
