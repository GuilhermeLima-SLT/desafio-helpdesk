# HelpDesk — Desafio Técnico (Arquitetura baseada em Microsserviços)

#### Dashboard HelpDesk simples que faz a gestão de chamados com clientes, técnicos, categorias, prioridades e notificações automáticas.

#### Criação dos chamados em interface visual feita com React JS funcionando com HTML + CSS e Javascript puro, baseados em formularios com campos validados pelo backend, conexões usando axios apontando apenas para Gateway e react-router-dom para mapeamento de endereços das requisições HTTP e "tradução" dos endereços para utilizados em frontend. 


## Arquitetura

```
React (8080)
    ↓
API Gateway (8004)
    ↓
user-service (8001) | ticket-service (8002) | notification-service (8006)
    ↓                        ↓                           ↓
  user_db                ticket_db                notification_db
                             ↓                           ↑
                          RabbitMQ  ─────────────────────┘
                    (TicketCreated / TicketAssigned / TicketStatusChanged)

Service Discovery: Eureka Server (8003)
```

## Stack

- Java 21, Spring Boot 4.1.1 (Web, Data JPA, Validation, AMQP)
- Spring Cloud Gateway + Netflix Eureka (service discovery)
- PostgreSQL (um banco por serviço) + Flyway (para migrations)
- RabbitMQ (mensageria assíncrona)
- React 19 + Vite (JavaScript), servido por nginx (para ajustes de CORS aos novos endereços passados ao frontend)
- Docker / Docker Compose

## Pré-requisitos

- Docker 20+ e Docker Compose 2+

## Como executar

```bash
docker compose up --build
```

> A primeira execução leva alguns minutos (build das imagens Maven).
> Após subir, aguarde alguns segundos para que todos os serviços se registrem
> no Eureka antes de usar a aplicação.

Acesse: **http://localhost:8080**

Para parar de rodar o projeto:
```bash
docker compose down
```

## Portas e URLs

| Serviço | URL | Observação |
|---|---|---|
| Frontend (React) | http://localhost:8080 | interface principal frontend |
| API Gateway | http://localhost:8004 | ponto único resolvedor de entrada da API |
| Eureka | http://localhost:8003 | painel de service discovery (Eureka) |
| RabbitMQ | http://localhost:15672 | painel de administrador do message broker (guest / guest) |
| user-service | http://localhost:8001 | acesso direto (para debug/Swagger) |
| ticket-service | http://localhost:8002 | acesso direto (para debug/Swagger) |
| notification-service | http://localhost:8006 | acesso direto (para debug/Swagger) |

Swagger de cada serviço: `http://localhost:<porta>/swagger-ui.html`

## Usuários pré-cadastrados

Criados automaticamente via Flyway (`V3__mock_users_models.sql`):

| Nome | E-mail | Papel |
|---|---|---|
| Guilherme Client | guilherme.cliente@solutis.com.br | CLIENT |
| Manuel Client | manuel.cliente@solutis.com.br | CLIENT |
| João Técnico | joao.technician@solutis.com.br | TECHNICIAN |
| Marcia Técnica | marcia.technician@solutis.com.br | TECHNICIAN |
| Paulo Administrador | paulo.admin@solutis.com.br | ADMIN |

## Roteiro de verificação

1. Abra http://localhost:8080 → **Dashboard** com os contadores simples solicitados.
2. **Chamados → + Novo chamado**: preencha e selecione um cliente. O chamado é
   criado com status `OPEN` e o evento `TicketCreated` é publicado no RabbitMQ.
3. Abra os **Detalhes** do chamado → altere status/prioridade e **atribua um técnico**
   (eventos `TicketStatusChanged` e `TicketAssigned`).
4. Confirme as notificações geradas:
   `curl http://localhost:8004/api/notifications`
5. (Opcional) Veja as filas e mensagens no RabbitMQ http://localhost:15672.

## Principais endpoints (API REST - via Gateway)

| Método | Rota | Descrição |
|---|---|---|
| USER |
| POST | `/api/users` | criar usuário |
| GET | `/api/users` | listar usuários ativos |
| GET | `/api/users/{id}` | consultar usuário |
| PUT | `/api/users/{id}` | atualizar usuário |
| PATCH | `/api/users/{id}/deactivate` | inativar usuário |
| TICKET |
| POST | `/api/tickets` | criar chamado |
| GET | `/api/tickets` | listar chamados |
| GET | `/api/tickets/{id}` | consultar chamado |
| PUT | `/api/tickets/{id}` | alterar descrição, prioridade, categoria e status |
| PUT | `/api/tickets/{id}/assign` | atribuir técnico |
|NOTIFICATION|
| GET | `/api/notifications` | listar notificações |
| GET | `/api/notifications/{id}` | consultar notificação |


E alguns outros endpoints criados para filtrar também por meio de requisições HTTP:
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/tickets/status/{status}` | Listar tickets por status |
| GET | `/api/tickets/priority/{priority}` | Listar tickets por prioridade |
| GET | `/api/tickets/category/{category}` | Listar tickets por categoria |
| GET | `/api/tickets/customer/{customerId}` | Listar tickets de um determinado cliente |
| DELETE | `/api/tickets/{id}/close` | Encerrar ticket (Exclusao logica) |
| DELETE | `/api/tickets` | Deletar chamado (Delete no banco de dados também) |

## Mensageria

Exchange `ticket.events` (do tipo *topic*), consumida pela fila `notification.tickets`
(binding `ticket.#`):

| Evento | Routing key | Quando ocorre |
|---|---|---|
| TicketCreated | `ticket.created` | criação de chamado |
| TicketAssigned | `ticket.assigned` | atribuição de técnico |
| TicketStatusChanged | `ticket.status-changed` | alteração de status / encerramento |

## Configuração por variáveis de ambiente

Nenhuma credencial fica fixa no código. Os serviços leem (com defaults locais):

| Variável | Exemplo (Docker) |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres-ticket:5432/ticket_db` |
| `SPRING_DATASOURCE_USERNAME` | `ticket` |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` |
| `SPRING_RABBITMQ_HOST` | `rabbitmq` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://eureka-server:8003/eureka` |

## Testes

Testes de unidade da camada de serviço (JUnit 5 + Mockito):

```bash
cd backend/ticket-service/ticket
./mvnw test
```

#### Os testes automatizados cobrem as regras minimamente importantes dos endpoints principais do projeto, dentre elas, status inicial OPEN, publicação de eventos, visualização de ticket, validação de cliente/técnico (sem conexões diretas como solicitado) e códigos de erro 400/404.