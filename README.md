# MatchInvest API

## 📌 Descrição do Projeto
O **MatchInvest** é uma aplicação desenvolvida em **Java com Spring Boot** que conecta **Investidores** e **Assessores de Investimentos**.  
O sistema permite o cadastro e gerenciamento de investidores e assessores, possibilitando criar **matches** entre eles com base em critérios como perfil de risco, foco de investimento e experiência.  

Este projeto foi desenvolvido como parte do trabalho acadêmico, contemplando boas práticas de **arquitetura de software**, **camadas de abstração**, **validações**, **tratamento de erros** e **documentação**.

---

## 🚀 Tecnologias Utilizadas
- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway** (migração e versionamento do banco de dados)
- **Hibernate Validator** (validações)
- **Lombok**
- **Swagger / Springdoc OpenAPI** (documentação da API)
- **JUnit 5 + Mockito** (testes unitários e de serviço)

---

## 📂 Estrutura do Projeto
A API foi organizada seguindo boas práticas de **arquitetura em camadas**:

```
src/main/java/com/matchinvest/api
│── controllers   # Camada de exposição (endpoints REST)
│── services      # Regras de negócio e lógica da aplicação
│── repositories  # Interfaces para comunicação com o banco (JPA)
│── entities      # Entidades do domínio (mapeamento JPA)
│── dto           # Objetos de transferência de dados (entrada/saída)
│── vo            # Value Objects (como Email e Money)
│── enums         # Enumerações (RiskProfile, InvestmentFocus, MatchStatus)
│── exceptions    # Tratamento centralizado de erros (RestControllerAdvice)
```

---

## ⚙️ Configuração e Execução

### 1️⃣ Pré-requisitos
- Java 21+
- Maven 3.9+
- PostgreSQL 14+
- IDE (IntelliJ, Eclipse ou VSCode)

### 2️⃣ Criar banco de dados no PostgreSQL
```sql
CREATE DATABASE matchinvest;
```

### 3️⃣ Configurar o arquivo `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/matchinvest
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
```

### 4️⃣ Rodar o projeto
```bash
mvn spring-boot:run
```

### 5️⃣ Baixar a Collection do Postman
- Você pode importar a collection do Postman para testar todos os endpoints da API:  
- [📥 Baixar Collection do Postman](SPRINT04_SOA.postman_collection.json)


### 6️⃣ Acessar a documentação Swagger
- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🔐 Autenticação e Perfis de Acesso

O sistema implementa autenticação **JWT (JSON Web Token)** e controle de acesso baseado em roles.

| Role | Permissões |
|------|-------------|
| **ROLE_ADMIN** | Acesso completo a todos os endpoints |
| **ROLE_INVESTOR** | Pode atualizar e deletar o próprio perfil |
| **ROLE_ADVISOR** | Pode atualizar e deletar o próprio perfil |

Após o login, o token JWT deve ser enviado no header de cada requisição:
```
Authorization: Bearer <token>
```

---

## 🔍 Exemplos de Registro e Login

### 💼 Registro de Investor
**POST** `/api/auth/register`
```json
{
  "name": "João da Silva",
  "email": "joao@investor.com",
  "password": "123456",
  "accountType": "INVESTOR",
  "investor": {
    "goals": "Aposentadoria e independência financeira",
    "avaliableAmount": 50000.00,
    "riskProfile": "MODERADO"
  },
  "advisor": null
}
```

### 💼 Registro de Advisor
**POST** `/api/auth/register`
```json
{
  "name": "Maria Consultora",
  "email": "maria@advisor.com",
  "password": "123456",
  "accountType": "ADVISOR",
  "advisor": {
    "certifications": "CPA-20",
    "investmentFocus": "ACOES",
    "yearsExperience": 7
  },
  "investor": null
}
```

### 🔑 Login e Autenticação
**POST** `/api/auth/login`
```json
{
  "email": "joao@investor.com",
  "password": "123456"
}
```
**Resposta:**
```json
{
  "token": "<JWT_TOKEN>",
  "tokenType": "Bearer",
  "expiresInMs": 3600000
}
```

Utilize esse token nas requisições autenticadas:
```
Authorization: Bearer <JWT_TOKEN>
```

## 🧩 Migrations Flyway

A base de dados é versionada via **Flyway**, garantindo consistência entre ambientes.  
Versões aplicadas:

| Versão | Descrição |
|---------|------------|
| `V1__create_tables.sql` | Estrutura inicial de entidades |
| `V2__add_score_and_status_to_matches.sql` | Campos adicionais no match |
| `V3__auth_tables.sql` | Tabelas de autenticação e roles |
| `V4__add_user_fk_to_advisors_investors.sql` | Associação entre usuário e perfis |

---

## 📌 Endpoints Principais

### 🔑 Autenticação
- `POST /api/auth/register` → Cria um usuário e seu perfil (investor/advisor)  
- `POST /api/auth/login` → Retorna token JWT  

### 👤 Investor
- `GET /api/investors` → Lista todos os investidores  
- `GET /api/investors/me` → Retorna o investor logado  
- `PUT /api/investors/{id}` → Atualiza o próprio perfil (INVESTOR ou ADMIN)  
- `DELETE /api/investors/{id}` → Deleta o próprio perfil  

### 💼 Advisor
- `GET /api/advisors` → Lista todos os assessores  
- `PUT /api/advisors/{id}` → Atualiza o próprio perfil (ADVISOR ou ADMIN)  

### 🔗 Match
- `GET /api/matches` → Lista matches existentes  
- `PATCH /api/matches/{id}/accept` → Aceita match (ADMIN)  
- `PATCH /api/matches/{id}/reject` → Rejeita match (ADMIN)

---

## 🛠️ Tratamento de Erros
A API possui tratamento centralizado de erros com **RestControllerAdvice**.  
Erros de validação retornam no formato padronizado:

```json
{
  "error": "Validation error",
  "message": "Invalid request fields",
  "fields": [
    {
      "field": "email",
      "message": "E-mail é obrigatório"
    }
  ],
  "timestamp": "2025-09-18T12:08:53.384768-03:00",
  "status": 422
}
```

---

## 🧪 Testes Automatizados

Os testes foram expandidos com **MockMvc** e cobrem:
- Registro e login via `/api/auth/register` e `/api/auth/login`
- Criação de advisor por ADMIN (201 Created)
- Acesso negado (403) para usuários comuns
- Atualização de perfil investor/advisor (200 OK)
- Rejeição de requisições sem token (401 Unauthorized)

Para executar:
```bash
mvn test
```

---

---

## 📊 Diagramas

### 🔹 Arquitetura do Sistema
![Diagrama de Arquitetura](./assets/arq_diagram.png)

### 🔹 Diagrama de Classes (Entidades e Relacionamentos)
![Diagrama de Classes](./assets/classes_diagram.png)

### 🔹 Diagrama de Casos de Uso
![Diagrama de Casos de Uso](./assets/uc_diagram.png)

---

## 👨‍💻 Integrantes
- Enzo Luiz Goulart - RM99666  
- Gustavo Henrique Santos Bonfim - RM98864  
- Kayky Paschoal Ribeiro - RM99929  
- Lucas Yuji Farias Umada - RM99757  
- **Natan Eguchi dos Santos** - **RM98720** 
