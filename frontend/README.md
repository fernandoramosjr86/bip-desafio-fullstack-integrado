# Frontend Angular + PrimeNG

Novo frontend Angular (standalone) com PrimeNG, inspirado no visual do template Poseidon:
- Shell com sidebar + topbar
- Página de benefícios com tabela paginada server-side
- Modal para criar/editar benefício
- Modal para transferência entre benefícios
- Integração com backend via proxy local (`/api` e `/actuator` -> `http://localhost:8080`)

## Arquitetura
Estrutura organizada por camadas:
- `src/app/core/models`: contratos de domínio/frontend
- `src/app/core/services`: serviços HTTP e integração com API
- `src/app/features/beneficios/pages`: feature principal de benefícios
- `src/app/features/beneficios/beneficios.routes.ts`: rotas lazy da feature
- `src/app`: shell da aplicação (`app.component`) + roteamento raiz

## Requisitos
- Node.js 18+
- npm 9+
- Backend disponível em `http://localhost:8080`

## Executar
### Sem Docker
1. `npm install`
2. `npm start`
3. Acesse `http://localhost:4200`

### Com Docker (stack completa no repositório raiz)
1. No diretório raiz do projeto: `docker compose up --build`
2. Acesse `http://localhost:4200`

## Build
- `npm run build`

## Endpoints usados
- `GET /api/v1/beneficios?page={page}&size={size}`
- `POST /api/v1/beneficios`
- `PUT /api/v1/beneficios/{id}`
- `POST /api/v1/beneficios/transferencias`
- `GET /api/v1/beneficios/transferencias/historico?page={page}&size={size}`
- `GET /actuator/health` (status do backend no botão da topbar)
