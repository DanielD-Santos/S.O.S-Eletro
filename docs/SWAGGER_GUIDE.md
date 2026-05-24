# 📖 Guia Rápido - Swagger UI

## 🚀 Como Acessar

1. **Inicie a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

2. **Acesse o Swagger UI:**
   - URL: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - Especificação JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## 🔐 Testando com Autenticação

### 1. Cadastrar/Login (se necessário)
- Use o endpoint `POST /api/v1/auth/cadastro` para criar um funcionário
- Use o endpoint `POST /api/v1/auth/login` para obter o token JWT

### 2. Autorizar no Swagger
1. Clique no botão **"Authorize"** (🔒) no topo da página
2. Cole o token JWT no campo (sem "Bearer ")
3. Clique em "Authorize"

### 3. Testar Endpoints Protegidos
Agora você pode testar todos os endpoints que requerem autenticação:
- `/api/v1/admin/agendamentos/*`
- `/api/v1/clientes/*`
- `/api/v1/protocolos/*`
- `/api/v1/tecnicos/*`

## 🏷️ Organização dos Endpoints

Os endpoints estão organizados em grupos (tags):

- **🔓 Autenticação** - Login e cadastro de funcionários
- **❤️ Health Check** - Status da aplicação
- **📅 Agendamentos Públicos** - Endpoints para clientes
- **🔒 Admin - Agendamentos** - Gerenciamento de agendamentos
- **👥 Clientes** - CRUD de clientes
- **📋 Protocolos** - CRUD de protocolos
- **🔧 Técnicos** - CRUD de técnicos
- **🔄 Admin - Sincronização** - Utilitários de sincronização

## ✨ Recursos Disponíveis

- **Try it out** - Execute requests diretamente no navegador
- **Schemas** - Visualize a estrutura dos DTOs
- **Examples** - Veja exemplos de requests e responses
- **Authentication** - Sistema de autorização integrado
- **Validation** - Veja as validações de cada campo

## 🎯 Endpoints Mais Úteis para Teste

1. **Health Check**: `GET /api/v1/health` (público)
2. **Login**: `POST /api/v1/auth/login` (público)
3. **Criar Agendamento**: `POST /api/v1/agendamentos` (público)
4. **Listar Agendamentos**: `GET /api/v1/admin/agendamentos` (autenticado)

---

💡 **Dica**: O Swagger UI é totalmente funcional - você pode executar todas as operações da API diretamente pela interface web!