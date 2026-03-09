# Arquitetura Hexagonal (Backend)

## Objetivo
Organizar o backend para separar regra de negocio de framework e infraestrutura.

## Estrutura adotada
No modulo `backend-module/src/main/java/com/example/backend`:

- `domain/`
  - `model/Beneficio.java`: entidade de dominio.
  - `exception/*`: excecoes de negocio.
- `application/`
  - `port/in/*`: contratos dos casos de uso.
  - `port/in/command/*`: comandos de entrada dos casos de uso.
  - `port/in/query/*`: consultas de entrada (ex.: paginação).
  - `port/out/*`: portas de saida para persistencia e transferencia.
  - `shared/*`: contratos comuns (ex.: resultado de pagina).
  - `service/BeneficioApplicationService.java`: implementacao dos casos de uso.
- `adapters/in/web/`
  - `BeneficioController.java`: adaptador HTTP (entrada).
  - `dto/*`: contratos HTTP de request/response.
  - `ApiExceptionHandler.java`: tratamento de erro da API.
  - OpenAPI/Swagger habilitado via springdoc.
- `adapters/out/persistence/`
  - `BeneficioPersistenceAdapter.java`: implementa porta de saida.
  - `entity/BeneficioJpaEntity.java`: entidade JPA.
  - `repository/SpringDataBeneficioRepository.java`: Spring Data.
- `adapters/out/ejb/`
  - `BeneficioTransferenciaEjbAdapter.java`: delega transferencia para o `ejb-module`.

## Fluxo
1. Requisicao HTTP entra no controller (`adapter in`).
2. Controller chama uma porta de entrada (`application port in`).
3. Caso de uso aplica regras de negocio e orquestra.
4. Persistencia e acessada via porta de saida (`application port out`).
5. Para transferencia, a aplicacao usa uma porta especifica que chama o `ejb-module`.
6. Adapter de persistencia usa JPA/Spring Data e retorna para o dominio.

## Beneficios desta organizacao
- Troca de tecnologia de persistencia sem afetar casos de uso.
- Testes de negocio sem depender de controller, banco ou framework.
- Integracao com EJB isolada como adapter de saida sem acoplar a API HTTP ao modulo EJB.
- Paginação implementada no caso de uso sem vazar `Page` do Spring para o nucleo da aplicacao.
