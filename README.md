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
3. Será criado o banco `sos_eletro` e as tabelas `cliente`, `equipamento`, `tecnico`, `funcionario_portal` e `agendamento` com chaves estrangeiras.

Se já tiver uma base antiga sem `funcionario_portal`, execute também `db/06_funcionario_portal.sql`.

Se ainda existir a tabela antiga `ordem_servico` e quiser removê-la, execute `db/07_drop_ordem_servico.sql`.

## 2. Configuração da conexão (Spring Boot)

Em `application.yml` já existem URL (com `createDatabaseIfNotExist=true`), usuário `root` e senha vinda de **`MYSQL_PASSWORD`** (padrão `root` se a variável não existir).

**Se a senha do seu MySQL não for `root`**, use uma das opções:

1. **Arquivo local (recomendado):** copie `application-local.example.yml` para `application-local.yml` na mesma pasta (`src/main/resources/`) e defina:

   ```yaml
   spring:
     datasource:
       password: "sua_senha_mysql"
   ```

   O ficheiro `application-local.yml` está no `.gitignore` e é carregado automaticamente.

2. **Variável de ambiente (PowerShell):** antes de `mvn spring-boot:run`:

   ```powershell
   $env:MYSQL_PASSWORD = "sua_senha_mysql"
   ```

**Hibernate `ddl-auto`:** o projeto usa `update` em desenvolvimento (cria/atualiza tabelas a partir das entidades). Assim a app costuma subir mesmo sem ter executado o script SQL antes. Para ambiente controlado só com o script do Workbench, pode alterar para `validate` em `application.yml`.

### Se aparecer `BUILD FAILURE` no `spring-boot:run`

Rode com stack trace completo:

```bash
mvn spring-boot:run -e
```

Causas frequentes:

| Mensagem / situação | O que fazer |
|---------------------|-------------|
| `Communications link failure` / `Connection refused` | Serviço MySQL parado — inicie o MySQL (Serviços do Windows ou XAMPP). |
| `Access denied for user` | Senha ou usuário incorretos — use `application-local.yml` ou `MYSQL_PASSWORD`. |
| `Unknown database` | Com a URL atual o JDBC tenta criar o banco; confirme que o utilizador tem permissão `CREATE`. |
| `Schema-validation` / `missing table` | Se mudar para `ddl-auto: validate`, execute o script `db/01_schema_sos_eletro.sql` no Workbench. |
| Porta em uso | Altere `server.port` em `application.yml` (ex.: `8081`) ou encerre o processo na porta 8080. |

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

### Conta de funcionário

1. Garanta que a tabela `funcionario_portal` existe (script `01` ou `06`).
2. Abra **Cadastro de funcionário**, preencha nome, e-mail e senha (mínimo 8 caracteres) e submeta.
3. Faça **login** com o mesmo e-mail e senha. O Spring Security valida contra a base (`UsuarioFuncionarioDetailsService`); o JWT usa a chave `sos.security.jwt-secret` em `application.yml` (ou variável `JWT_SECRET`, mínimo 32 caracteres).

## 4. Rotas da API (`/api/v1`)

| Recurso            | Métodos                          | Caminho                      |
|--------------------|----------------------------------|------------------------------|
| Clientes           | GET, POST, GET/{id}, PUT, DELETE | `/api/v1/clientes`         |
| Equipamentos       | GET, POST, GET/{id}, PUT, DELETE | `/api/v1/equipamentos`     |
| Técnicos           | GET, POST, GET/{id}, PUT, DELETE | `/api/v1/tecnicos`         |
| Agendamentos (público) | GET `slots-permitidos`, GET `horarios-ocupados?data=`, POST `/` | `/api/v1/agendamentos` |
| Autenticação | POST `login` (JSON: `email`, `password`); POST `cadastro` (JSON: `nomeCompleto`, `email`, `senha`, `confirmarSenha`) | `/api/v1/auth/login`, `/api/v1/auth/cadastro` |
| Agendamentos (admin) | GET `?status=&q=`, GET `/{id}`, PUT `/{id}`, PATCH `/{id}/concluir`, DELETE `/{id}` | `/api/v1/admin/agendamentos` — **requer** cabeçalho `Authorization: Bearer <token>` |

**Agendamentos:** conflito de horário é evitado na aplicação para status **AGENDADO** e **EM_ATENDIMENTO** (concluídos/cancelados libertam o horário). O cliente consulta `horarios-ocupados` antes de reservar. Migrações úteis: `db/02_agendamento.sql`, `db/04_agendamento_drop_unique.sql` e `db/05_agendamento_add_status.sql` se a base foi criada com o script antigo.

Se o formulário de agendamento mostrar erro ao gravar, a causa mais comum é **a tabela `agendamento` não existir** na base `sos_eletro`. Nesse caso, execute `db/02_agendamento.sql` (ou o bloco completo em `db/01_schema_sos_eletro.sql`) e reinicie a API. Com `ddl-auto: update`, o Hibernate também pode criar a tabela ao subir a aplicação. A API passa a devolver uma mensagem explícita em JSON (campo `message`) em vez de apenas “erro interno”.

Corpo JSON utiliza DTOs com validação (`jakarta.validation`).

**Status dos agendamentos:** `AGENDADO`, `EM_ATENDIMENTO`, `CONCLUIDO`, `CANCELADO`.

## 5. WhatsApp na landing page

Substitua o número de exemplo pelo WhatsApp real da sua assistência:

- Em `src/main/resources/static/js/app.js`, altere `WHATSAPP_E164` (apenas dígitos, com DDI, ex.: `558199698779`) e `WHATSAPP_MENSAGEM` se quiser outro texto pré-preenchido no WhatsApp.
- Os links em `index.html` são atualizados pelo mesmo script ao carregar a página.

## Estrutura do projeto (back-end)

- `entity` — modelo JPA (`Cliente`, `Equipamento`, `Tecnico`, `Agendamento`, `FuncionarioPortal`)
- `security` — Spring Security, JWT (`JwtService`, `JwtAuthFilter`, `SecurityConfig`)
- `repository` — Spring Data JPA
- `service` — regras de negócio e mapeamento para DTOs
- `controller` — endpoints REST
- `dto` — objetos de entrada/saída da API
- `exception` — tratamento centralizado de erros (`GlobalExceptionHandler`)
- `config` — CORS para consumo da API em desenvolvimento
- `static` — landing (`index.html`), agendamento cliente (`agendamento.html`), login/painel funcionário (`funcionario-login.html`, `funcionario-painel.html`), `css/` (`agendamento.css`, `funcionario.css`), `js/` correspondentes

## Identidade visual (landing)

Paleta utilizada: `#8F0B13`, `#EFDFC5`, `#380F17`, `#4C4F54`, `#252B2B`.

---

Projeto acadêmico Para fins de aprendizado
