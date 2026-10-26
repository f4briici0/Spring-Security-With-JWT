# 🛡️ Spring Security API com JWT e Refresh Token

Este projeto é uma API RESTful robusta desenvolvida com **Java 17** e **Spring Boot**, focada em segurança utilizando **Spring Security** e **OAuth2 Resource Server**. Implementa autenticação via **JWT (JSON Web Token)** com assinatura assimétrica (RSA), mecanismo de **Refresh Token com rotação**, controle de acesso baseado em funções (RBAC - User/Admin) e tratamento global de exceções padronizado.

---

## 🚀 Tecnologias Utilizadas

* ☕ **Java 17**
* 🍃 **Spring Boot** (Spring MVC, Bean Validation)
* 🔒 **Spring Security** (OAuth2 Resource Server & Nimbus JWT)
* 🗄️ **Spring Data JPA / Hibernate**
* 🐘 **PostgreSQL**
* 🏗️ **Maven**

---

## ✨ Funcionalidades

* ✅ **Cadastro e Login de Usuários** com validação de payload (Bean Validation) e criptografia de senha via **BCrypt**.
* 🔑 **Autenticação JWT Stateless** com par de chaves assimétricas **RSA-256**.
* 🔄 **Refresh Token com Rotação** (prevenção contra replay attacks e renovação segura de sessão).
* 👮 **Controle de Acesso Baseado em Funções (RBAC)**:
  - Papéis suportados: `USER` e `ADMIN`.
  - Proteção em nível de rota na cadeia de filtros (`hasRole('ADMIN')`) e por anotações de método (`@PreAuthorize("hasRole('ADMIN')")`).
* 🔁 **Gerenciamento de Cargos**: Endpoint administrativo para alteração de papéis de usuários.
* 🛡️ **Tratamento Global de Exceções**: Respostas de erro padronizadas via `@RestControllerAdvice` e DTO semântico (`StandardErrorDTO`).

---

## 🛠️ Configuração e Instalação

### 1. Pré-requisitos
Certifique-se de ter instalado:
* Java JDK 17+
* Maven
* PostgreSQL 14+

### 2. Geração das Chaves RSA
O projeto utiliza um par de chaves assimétricas RSA para assinar e decodificar os tokens JWT. As chaves ficam localizadas no diretório `src/main/resources/keys`:

```bash
# Criar diretório (caso não exista)
mkdir -p src/main/resources/keys

# Gerar chave privada RSA (2048 bits)
openssl genrsa -out src/main/resources/keys/app.key 2048

# Extrair chave pública
openssl rsa -in src/main/resources/keys/app.key -pubout -out src/main/resources/keys/app.pub
```

### 3. Configuração do Banco de Dados

Crie um banco de dados no PostgreSQL chamado `security` ou ajuste as credenciais no arquivo `src/main/resources/application.properties`:

```properties
spring.application.name=security
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/security
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

jwt.private.key=classpath:keys/app.key
jwt.public.key=classpath:keys/app.pub
```

### 4. Executando o Projeto

Via Maven Wrapper (Linux / macOS):
```bash
./mvnw spring-boot:run
```

Ou no Windows (PowerShell / CMD):
```powershell
.\mvnw.cmd spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

---

## 🔌 Endpoints da API

### Autenticação (`/auth`)

| Método | Endpoint | Descrição | Permissão |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Cadastra um novo usuário com cargo padrão `USER` | Pública |
| `POST` | `/auth/login` | Autentica as credenciais e retorna `accessToken` e `refreshToken` | Pública |
| `POST` | `/auth/refresh-token` | Rotaciona o `refreshToken` e emite novo par de tokens | Pública |

### Rotas de Teste e Demonstração

| Método | Endpoint | Descrição | Permissão |
| --- | --- | --- | --- |
| `GET` | `/public` | Rota aberta para teste de conectividade | Pública |
| `GET` | `/private` | Rota protegida para validar tokens de qualquer usuário logado | Autenticado |
| `GET` | `/admin` | Rota protegida via filtro de requisição | `ROLE_ADMIN` |
| `GET` | `/admin/test` | Rota protegida via anotação `@PreAuthorize("hasRole('ADMIN')")` | `ROLE_ADMIN` |

### Administração (`/admin`)

| Método | Endpoint | Descrição | Permissão |
| --- | --- | --- | --- |
| `PUT` | `/admin/change-role` | Altera o papel de um usuário (`USER` ou `ADMIN`) | `ROLE_ADMIN` |

---

## 🧩 Estrutura de Segurança e Ciclo do Token

1. **Access Token (JWT):**
   - **Duração:** 1 hora (3600 segundos).
   - **Algoritmo:** RSA-256 assimétrico.
   - **Claims:** Contém emissor (`SPRING-SECURITY-JWT`), `subject` (username), data de emissão, expiração e a lista de `roles` concedidas (ex: `["ROLE_USER"]` ou `["ROLE_ADMIN"]`).
   - Enviado nas requisições protegidas através do cabeçalho HTTP:
     ```http
     Authorization: Bearer <seu_token_jwt>
     ```

2. **Refresh Token:**
   - **Duração:** 24 horas (86400 segundos).
   - Persistido no banco de dados (`TB_REFRESH_TOKENS`) vinculado ao usuário.
   - Utilizado exclusivamente para renovar a sessão sem a necessidade de reinserir usuário e senha.

3. **Mecanismo de Rotação (Token Rotation):**
   - A cada chamada válida para `/auth/refresh-token`, o `refreshToken` enviado é imediatamente rotacionado (atualizado com um novo identificador e nova expiração).
   - O token antigo deixa de existir/funcionar, prevenindo ataques de replay e garantindo que sessões comprometidas sejam invalidadas.

---

## 🛡️ Tratamento de Erros e Exceções

Todas as exceções capturadas pela aplicação são tratadas pelo `@RestControllerAdvice` (`GlobalExceptionHandler`) e retornam a estrutura padronizada em JSON:

```json
{
  "timestamp": "2026-10-26T20:00:00.000Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Credenciais inválidas: usuário ou senha incorretos.",
  "path": "/auth/login"
}
```

Status HTTP tratados:
* `400 Bad Request` – Erros de validação nos campos do DTO (`@Valid`) ou payload malformado.
* `401 Unauthorized` – Falha nas credenciais (login inválido) ou Refresh Token expirado.
* `403 Forbidden` – Usuário autenticado sem a permissão necessária (`AccessDeniedException`).
* `404 Not Found` – Usuário ou Refresh Token não encontrado no banco.
* `409 Conflict` – Tentativa de cadastrar usuário com username já existente.
* `500 Internal Server Error` – Falhas genéricas não mapeadas.

---

## 👨‍💻 Autor

Desenvolvido por **Fabricio Ferreira de Lima**
