# 🛡️ Guia de Implementação do Spring Security
Este repositório contém um exemplo de aplicação Spring Boot que demonstra a implementação e configuração básica do Spring Security.
O objetivo é fornecer autenticação básica e autorização baseada em URLs, protegendo endpoints específicos e permitindo acesso a outros.

## Tecnologias Utilizadas
- Java 17
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Spring Security
- Lombok
- PostgreSQL
- Maven
- JWT

## Configuração do projeto
O projeto foi criado com [Spring Initializer](https://start.spring.io/).

## Dependências
- Spring Web
- Spring Data JPA
- Spring Security
- Java JWT
- Oauth2 client
- Validation
- PostgreSQL Driver
- Lombok
- MapStruct
- Spring Boot DevTools

> [!NOTE]
> 💡 Ao adicionar `spring-boot-starter-security`, o Spring Boot automaticamente exige autenticação para todos os endpoints por padrão.

## Como Rodar a Aplicação
1.  **Pré-requisito:** Tenha o Java 17 (ou superior) e o Maven instalados. Tenha uma instância do PostgreSQL rodando.
2.  Crie um banco de dados no PostgreSQL (ex: `CREATE DATABASE security_db;`).
3.  Configure suas credenciais do banco no arquivo `src/main/resources/application.yaml`.
4.  Abra o projeto em sua IDE (ex: IntelliJ IDEA).
5.  Execute a classe principal `SpringSecurityApplication.java`.
6.  O servidor estará rodando em `http://localhost:8080`.

## Endpoints da API

#### Autenticação

- **`POST /api/users`**
    - Cadastra um novo usuário.
    - **Body**: `{ "username": "user", "email": "user@example.com", "fisrtName": "User", "lastName": "Teste", "password": "password123" }`

- **`POST /api/auth/login`**
    - Autentica um usuário e retorna um token JWT.
    - **Body**: `{ "username": "user", "password": "password123" }`
    - **Retorno**: `{ "token": "seu-jwt-token" }`

- **`GET /oauth2/authorization/github`**
    - Autentica um usuário com login social do GitHub e retorna um token JWT.
    - **Retorno**: `{ "token": "seu-jwt-token" }`

#### Usuários (Protegido)
- **`PUT /api/users/{id}`**
    - Atualiza um usuário dado o seu id.
    - **Header Obrigatório**: `Authorization: Bearer <seu-jwt-token>`

## Estrutura de diretórios
```
src/main/java/dev/marcos/spring_security/
├── config/
│   └── SecurityConfig.java        <-- Configuração central de segurança
├── controller/
├── dto/
├── entity/
│   └── User.java                  <-- Implementação do UserDetails
├── exception/
│   └── (Handlers de Exceção)
├── mapper/
├── repository/
├── security/
│   └── SecurityFilter.java         <-- Filtro de validação de JWT
├── service/
│   ├── TokenService.java           <-- Geração e Validação de Token
│   └── UserService.java            <-- Implementação do UserDetailsService
└── (Outros packages)
```
