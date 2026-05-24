# S.O.S Eletro

Solução para gestão de assistência técnica em eletrônicos: API REST em **Java 17 + Spring Boot 3**, banco **MySQL** e **landing page** (HTML, CSS e JavaScript) servida junto com a aplicação.

## Pré-requisitos

- [JDK 17](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [MySQL Server 8](https://dev.mysql.com/downloads/mysql/) em execução local
- [MySQL Workbench](https://dev.mysql.com/downloads/workbench/) (recomendado para executar o script e administrar o banco)

## 1. Banco de dados (MySQL Workbench)

1. Abra o MySQL Workbench e conecte-se à sua instância local (`localhost`, usuário `root` ou outro).
2. Abra o arquivo `db/01_schema_sos_eletro.sql` e execute o script completo.
3. Será criado o banco `sos_eletro` e as tabelas `cliente`, `protocolo`, `tecnico`, `funcionario_portal` e `agendamento` com chaves estrangeiras.

Se já tiver uma base antiga sem `funcionario_portal`, execute também `db/06_funcionario_portal.sql`.

Se ainda existir a tabela antiga `ordem_servico` e quiser removê-la, execute `db/07_drop_ordem_servico.sql`.

## 2. Configuração da conexão (Spring Boot)

A URL e o usuário (`root`) estão em `application.yml`. A **senha não fica no repositório** — configure de uma destas formas:

### Opção A — `application-local.yml` (recomendado)

1. Edite `src/main/resources/application-local.yml` (já existe no projeto com um placeholder) **ou** copie o exemplo:

   ```powershell
   Copy-Item "src\main\resources\application-local.example.yml" "src\main\resources\application-local.yml"
   ```

2. Troque `COLOQUE_SUA_SENHA_MYSQL_AQUI` pela senha do MySQL Workbench (mesmo usuário `root`):

   ```yaml
   spring:
     datasource:
       password: "sua_senha_mysql"
   ```

   O arquivo `application-local.yml` está no `.gitignore` e é carregado automaticamente pelo Spring.

### Opção B — variável de ambiente

No PowerShell, **na mesma sessão** em que for rodar a API:

```powershell
$env:MYSQL_PASSWORD = "sua_senha_mysql"
mvn spring-boot:run
```

**Hibernate `ddl-auto`:** o projeto usa `update` em desenvolvimento (cria/atualiza tabelas a partir das entidades). Para produção, prefira `validate` e os scripts em `db/`.

### Se aparecer `BUILD FAILURE` no `spring-boot:run`

Rode com stack trace completo:

```powershell
mvn spring-boot:run -e
```

Causas frequentes:

| Mensagem / situação | O que fazer |
|---------------------|-------------|
| `Access denied for user 'root'@'localhost'` | Senha incorreta ou ausente — configure `application-local.yml` ou `MYSQL_PASSWORD` (veja acima). |
| `Unable to determine Dialect without JDBC metadata` | Consequência de falha na conexão MySQL — corrija a senha/serviço antes de investigar Hibernate. |
| `Communications link failure` / `Connection refused` | Serviço MySQL parado — inicie o MySQL (Serviços do Windows ou XAMPP). |
| `Unknown database` | Com a URL atual o JDBC tenta criar o banco; confirme permissão `CREATE` para o usuário. |
| `Schema-validation` / `missing table` | Com `ddl-auto: validate`, execute `db/01_schema_sos_eletro.sql` no Workbench. |
| Porta em uso | Altere `server.port` em `application.yml` (ex.: `8081`) ou libere a porta 8080. |

## 3. Executar a API e ver a landing page

Na raiz do projeto (`sos-eletro`):

```bash
mvn spring-boot:run
```

- **Landing page:** [http://localhost:8080/](http://localhost:8080/) ou [http://localhost:8080/index.html](http://localhost:8080/index.html)
- **Agendamento (cliente):** [http://localhost:8080/agendamento.html](http://localhost:8080/agendamento.html) — também acessível pelo botão **Solicitar assistência** na página inicial
- **Cadastro de funcionário:** [http://localhost:8080/funcionario-cadastro.html](http://localhost:8080/funcionario-cadastro.html)
- **Área do funcionário (login):** [http://localhost:8080/funcionario-login.html](http://localhost:8080/funcionario-login.html) — link no menu superior da landing e da página de agendamento
- **Painel interno:** [http://localhost:8080/funcionario-painel.html](http://localhost:8080/funcionario-painel.html) (após login; exige JWT) — agendamentos
- **Health check da API:** [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health)
- **Documentação da API (Swagger UI):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) — interface interativa da API
- **Especificação OpenAPI:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) — especificação JSON da API

### Conta de funcionário

1. Garanta que a tabela `funcionario_portal` existe (script `01` ou `06`).
2. Abra **Cadastro de funcionário**, preencha nome, e-mail e senha (mínimo 8 caracteres) e submeta.
3. Faça **login** com o mesmo e-mail e senha. O Spring Security valida contra a base (`UsuarioFuncionarioDetailsService`); o JWT usa a chave `sos.security.jwt-secret` em `application.yml` (ou variável `JWT_SECRET`, mínimo 32 caracteres).

## 4. Rotas da API (`/api/v1`)

| Recurso            | Métodos                          | Caminho                      | Autenticação |
|--------------------|----------------------------------|------------------------------|--------------|
| Clientes           | GET, POST, GET/{id}, PUT, DELETE | `/api/v1/clientes`         | JWT funcionário |
| Protocolos (cadastro) | GET, POST, GET/{id}, PUT, DELETE | `/api/v1/protocolos`     | JWT funcionário |
| Técnicos           | GET, POST, GET/{id}, PUT, DELETE | `/api/v1/tecnicos`         | JWT funcionário |
| Agendamentos (público) | GET `slots-permitidos`, GET `horarios-ocupados?data=`, POST `/`, PATCH `*/contato` | `/api/v1/agendamentos` | Público |
| Autenticação | POST `login` (JSON: `email`, `password`); POST `cadastro` (JSON: `nomeCompleto`, `email`, `senha`, `confirmarSenha`) | `/api/v1/auth/login`, `/api/v1/auth/cadastro` | Público |
| Agendamentos (admin) | GET `?status=&q=`, GET `/{id}`, PUT `/{id}` (corpo só `{ "status": "…" }`), PATCH `/{id}/concluir`, DELETE `/{id}` | `/api/v1/admin/agendamentos` | JWT funcionário |
| Sincronizar cliente/protocolo | POST (corpo vazio) | `/api/v1/admin/sync/clientes-protocolos-desde-agendamentos` | JWT funcionário |

Ao **POST** de novo agendamento (e ao **PATCH** de contato do cliente), o sistema **cria ou atualiza** `cliente` e **`protocolo`** no MySQL com o mesmo `numero_protocolo` do agendamento (e-mail sintético `importado.*@agendamento.importado.sos-eletro` para cliente novo). No painel interno, o **PUT** sobre um agendamento altera **apenas o status** (nome, telefone, data e serviço não são editáveis pelo funcionário).

**Agendamentos:** conflito de horário é evitado na aplicação para status **AGENDADO** e **EM_ATENDIMENTO** (concluídos/cancelados libertam o horário). O cliente consulta `horarios-ocupados` antes de reservar. Migrações úteis: `db/02_agendamento.sql`, `db/04_agendamento_drop_unique.sql` e `db/05_agendamento_add_status.sql` se a base foi criada com o script antigo.

Se o formulário de agendamento mostrar erro ao gravar: confira no Workbench com `DESCRIBE agendamento;` se existe a coluna **`numero_protocolo`**. Se a tabela foi criada com um `db/02_agendamento.sql` antigo, ela não tinha essa coluna — execute **`db/11_agendamento_numero_protocolo.sql`** (ou o bloco `agendamento` em **`db/01_schema_sos_eletro.sql`** numa base nova). Se a tabela nem existir, execute o **`db/02_agendamento.sql`** atualizado e reinicie a API. Com `ddl-auto: update`, o Hibernate pode criar/ajustar colunas ao subir.

Corpo JSON utiliza DTOs com validação (`jakarta.validation`).

**Status dos agendamentos:** `AGENDADO`, `EM_ATENDIMENTO`, `CONCLUIDO`, `CANCELADO`.

## 5. WhatsApp na landing page

Substitua o número de exemplo pelo WhatsApp real da sua assistência:

- Em `src/main/resources/static/js/app.js`, altere `WHATSAPP_E164` (apenas dígitos, com DDI, ex.: `558199698779`) e `WHATSAPP_MENSAGEM` se quiser outro texto pré-preenchido no WhatsApp.
- Os links em `index.html` são atualizados pelo mesmo script ao carregar a página.

## Estrutura do projeto (back-end)

- `entity` — modelo JPA + `@PrePersist` (defaults de insert: `criadoEm`, protocolo, etc.)
- `dto` — request/response da API (validação JSON)
- `mapper` — conversão entidade ↔ DTO (`AgendamentoMapper`, `ClienteMapper`, …)
- `service` — regras de negócio e orquestração
- `repository` — Spring Data JPA
- `controller` — endpoints REST
- `security` — Spring Security, JWT, `FuncionarioUserDetailsFactory`
- `util` — helpers (`TelefoneUtil`, `StringUtil`, mensagens JDBC)
- `exception` — `GlobalExceptionHandler`
- `config` — CORS
- `static` — landing, agendamento público, login/painel funcionário

### Testes

```powershell
mvn test
```

Usa perfil `test` com H2 em memória (`src/test/resources/application-test.yml`). Não exige MySQL para os testes unitários/JPA de teste.

## Identidade visual (landing)

Paleta utilizada: `#8F0B13`, `#EFDFC5`, `#380F17`, `#4C4F54`, `#252B2B`.

---

Projeto acadêmico/protótipo para substituir anotações soltas por um fluxo centralizado e escalável.
