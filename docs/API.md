# S.O.S Eletro - Documentação da API

## 📖 Visão Geral

A API REST do S.O.S Eletro fornece endpoints completos para gestão de assistência técnica em eletrônicos, incluindo agendamentos, clientes, protocolos, técnicos e autenticação.

## 🔗 URLs Importantes

| Recurso | URL | Descrição |
|---------|-----|-----------|
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interface interativa da API |
| **OpenAPI Spec** | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) | Especificação OpenAPI (JSON) |
| **Health Check** | [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health) | Status da aplicação |
| **Collection Postman** | `docs/postman/SOS-Eletro.postman_collection.json` | Collection completa para testes |

## 🔐 Autenticação

A API utiliza **JWT (JSON Web Token)** para autenticação. Endpoints que requerem autenticação estão marcados com 🔒.

### Como Obter um Token

1. **Cadastrar Funcionário** (se necessário):
   ```http
   POST /api/v1/auth/cadastro
   Content-Type: application/json
   
   {
     "nomeCompleto": "Maria Funcionária",
     "email": "maria@soseletro.local",
     "senha": "senha12345",
     "confirmarSenha": "senha12345"
   }
   ```

2. **Fazer Login**:
   ```http
   POST /api/v1/auth/login
   Content-Type: application/json
   
   {
     "email": "maria@soseletro.local",
     "password": "senha12345"
   }
   ```

3. **Usar o Token**:
   ```http
   Authorization: Bearer {seu-jwt-token}
   ```

## 📚 Grupos de Endpoints

### 🔓 Públicos (Sem Autenticação)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/v1/health` | Status da aplicação |
| `POST` | `/api/v1/auth/cadastro` | Cadastrar funcionário |
| `POST` | `/api/v1/auth/login` | Fazer login |
| `GET` | `/api/v1/agendamentos/slots-permitidos` | Horários disponíveis |
| `GET` | `/api/v1/agendamentos/horarios-ocupados?data=YYYY-MM-DD` | Horários ocupados |
| `POST` | `/api/v1/agendamentos` | Criar agendamento |
| `PATCH` | `/api/v1/agendamentos/{protocolo}/contato` | Atualizar contato |

### 🔒 Autenticados (Requer JWT)

#### Gerenciamento de Agendamentos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/v1/admin/agendamentos` | Listar agendamentos |
| `GET` | `/api/v1/admin/agendamentos/{id}` | Buscar por ID |
| `PUT` | `/api/v1/admin/agendamentos/{id}` | Atualizar status |
| `PATCH` | `/api/v1/admin/agendamentos/{id}/concluir` | Concluir agendamento |
| `DELETE` | `/api/v1/admin/agendamentos/{id}` | Excluir agendamento |

#### Clientes
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/v1/clientes` | Listar clientes |
| `GET` | `/api/v1/clientes/{id}` | Buscar por ID |
| `POST` | `/api/v1/clientes` | Criar cliente |
| `PUT` | `/api/v1/clientes/{id}` | Atualizar cliente |
| `DELETE` | `/api/v1/clientes/{id}` | Excluir cliente |

#### Protocolos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/v1/protocolos` | Listar protocolos |
| `GET` | `/api/v1/protocolos/{id}` | Buscar por ID |
| `POST` | `/api/v1/protocolos` | Criar protocolo |
| `PUT` | `/api/v1/protocolos/{id}` | Atualizar protocolo |
| `DELETE` | `/api/v1/protocolos/{id}` | Excluir protocolo |

#### Técnicos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/v1/tecnicos` | Listar técnicos |
| `GET` | `/api/v1/tecnicos/{id}` | Buscar por ID |
| `POST` | `/api/v1/tecnicos` | Criar técnico |
| `PUT` | `/api/v1/tecnicos/{id}` | Atualizar técnico |
| `DELETE` | `/api/v1/tecnicos/{id}` | Excluir técnico |

#### Utilitários
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/admin/sync/clientes-protocolos-desde-agendamentos` | Sincronizar dados |

## 🎯 Casos de Uso Comuns

### 1. Fluxo do Cliente (Criar Agendamento)

```bash
# 1. Verificar horários disponíveis
GET /api/v1/agendamentos/slots-permitidos

# 2. Verificar horários ocupados para uma data
GET /api/v1/agendamentos/horarios-ocupados?data=2026-05-25

# 3. Criar agendamento
POST /api/v1/agendamentos
{
  "nomeCliente": "João Silva",
  "telefone": "81999998888",
  "tipoAparelho": "CELULAR",
  "tipoConserto": "Troca de tela",
  "dataAtendimento": "2026-05-25",
  "horario": "09:00",
  "observacoes": "Aparelho com trinca na lateral"
}

# 4. Atualizar contato (se necessário)
PATCH /api/v1/agendamentos/{protocolo}/contato
{
  "telefoneCadastrado": "81999998888",
  "nomeCliente": "João Silva Santos",
  "telefone": "81988887777"
}
```

### 2. Fluxo do Funcionário (Gerenciar Agendamentos)

```bash
# 1. Fazer login
POST /api/v1/auth/login
{
  "email": "maria@soseletro.local",
  "password": "senha12345"
}

# 2. Listar agendamentos (com filtros opcionais)
GET /api/v1/admin/agendamentos?status=AGENDADO&q=João

# 3. Atualizar status do agendamento
PUT /api/v1/admin/agendamentos/1
{
  "status": "EM_ATENDIMENTO"
}

# 4. Concluir agendamento
PATCH /api/v1/admin/agendamentos/1/concluir
```

## 📋 Status dos Agendamentos

| Status | Descrição |
|--------|-----------|
| `AGENDADO` | Agendamento confirmado, bloqueia horário |
| `EM_ATENDIMENTO` | Atendimento em andamento, bloqueia horário |
| `CONCLUIDO` | Atendimento concluído, libera horário |
| `CANCELADO` | Agendamento cancelado, libera horário |

## 📱 Tipos de Aparelho

- `CELULAR`
- `NOTEBOOK` 
- `IMPRESSORA`
- `COMPUTADOR`
- `TABLET`

## ⚠️ Códigos de Erro Comuns

| Código | Descrição | Solução |
|--------|-----------|----------|
| `400` | Dados inválidos | Verificar formato dos dados enviados |
| `401` | Não autenticado | Fazer login e incluir token no cabeçalho |
| `403` | Não autorizado | Verificar se o funcionário tem permissão |
| `404` | Recurso não encontrado | Verificar se o ID existe |
| `409` | Conflito de horário | Escolher outro horário disponível |

## 🧪 Testando a API

### Opção 1: Swagger UI
Acesse [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) para uma interface interativa completa.

### Opção 2: Postman
Importe a collection: `docs/postman/SOS-Eletro.postman_collection.json`

### Opção 3: curl
```bash
# Health check
curl http://localhost:8080/api/v1/health

# Slots permitidos
curl http://localhost:8080/api/v1/agendamentos/slots-permitidos

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"maria@soseletro.local","password":"senha12345"}'
```

## 📝 Notas Importantes

1. **Protocolos Únicos**: Cada agendamento gera um protocolo único no formato `SOS-YYYYMMDD-XXXX`
2. **Validação de Horário**: Apenas horários de 08:00 às 17:00 são aceitos
3. **Conflitos**: Dois agendamentos não podem ocupar o mesmo horário se ambos estão `AGENDADO` ou `EM_ATENDIMENTO`
4. **Sincronização**: Agendamentos criam/atualizam automaticamente clientes e protocolos
5. **Segurança**: Tokens JWT expiram em 24 horas por padrão

---

**💡 Dica**: Use sempre o Swagger UI para explorar a API de forma interativa e ver todos os schemas disponíveis!