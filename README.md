# Roots — Backend

API REST em Spring Boot para o [Roots](../README.md), marketplace B2B que conecta produtores/exportadores brasileiros a compradores internacionais. Este serviço cobre o fluxo de acesso antecipado: cadastro, confirmação, pagamento (simulado ou Stripe real) e e-mail de confirmação.

> Para a visão geral do projeto completo (frontend incluso) e a documentação técnica aprofundada, veja o [README raiz](../README.md) e a pasta [`../docs/`](../docs/00-indice.md).

## Stack

- Java 21, Spring Boot 3.5
- PostgreSQL + Flyway (migrações versionadas)
- Stripe (PaymentIntents + webhook) ou modo de pagamento simulado
- Spring Boot Actuator (health check + métricas, porta separada)
- Maven (via wrapper `./mvnw`, não precisa instalar Maven)

## Pré-requisitos

- JDK 21
- PostgreSQL 16 acessível (via Docker ou instalado localmente — veja o [README raiz](../README.md#1-suba-a-infraestrutura-postgres--e-mail))

## Como rodar

Com a infraestrutura (Postgres, opcionalmente Mailhog) já no ar:

```bash
./mvnw spring-boot:run
```

- API pública em `http://localhost:8080`
- Health check e métricas (Actuator) em `http://localhost:8081/actuator/health`
- Migrações Flyway rodam automaticamente na primeira execução

Rodar os testes:

```bash
./mvnw test
```

## Estrutura

```
src/main/java/com/origem/backend/
├── domain/       Entidades JPA (Signup, ProfileType, SignupStatus)
├── dto/          Records de requisição/resposta
├── repository/   Spring Data JPA
├── service/      Regras de negócio (SignupService, PaymentService, EmailService)
├── web/          Controllers REST, tratamento global de erros, rate limit
├── config/       CORS, propriedades da aplicação, configuração do Stripe
└── exception/    Exceções de domínio
src/main/resources/
├── application.yml         Configuração padrão (desenvolvimento)
├── application-prod.yml    Configuração de produção (profile "prod")
└── db/migration/           Migrações Flyway
```

## Configuração

Toda a configuração é feita por variáveis de ambiente — nenhuma precisa ser editada no código, e os defaults de desenvolvimento já funcionam com o `docker-compose.yml` da raiz do projeto.

Referência completa (banco, e-mail/SMTP, Stripe, CORS, rate limit, taxa de fundador): **[../docs/02-configuracao.md](../docs/02-configuracao.md)**.

## Modo demo vs. Stripe real

Por padrão (`DEMO_MODE=true`), `POST /api/signups/{id}/pay` marca o cadastro como pago sem chamar o Stripe — nenhuma chave é necessária. Para ligar o Stripe de verdade, veja [../docs/02-configuracao.md](../docs/02-configuracao.md#pagamento--modo-demo-vs-stripe-real).

## API

Todos os endpoints, formatos de requisição/resposta e erros: **[../docs/03-api.md](../docs/03-api.md)**.

## Acesso e segurança

Não há login de usuário — o acesso a um cadastro é protegido por um token gerado na criação (header `X-Access-Token`). Detalhes: **[../docs/04-seguranca-e-acesso.md](../docs/04-seguranca-e-acesso.md)**.

## Deploy em produção

Checklist, perfil `prod`, Dockerfile, Actuator, alerta de falha de e-mail e rate limiting: **[../docs/06-deploy-producao.md](../docs/06-deploy-producao.md)**.

## Testes

Cobertura da suíte automatizada: **[../docs/07-testes.md](../docs/07-testes.md)**.
