# ToDo REST API com Spring Boot

Esta aplicação é uma API REST de "ToDo" onde cada usuário autenticado pode criar, atualizar, listar e excluir tarefas, feita com Spring Boot 3 (Spring 6) e Java 17. <br />
Nesta ToDo API estão presentes todas as operações de CRUD, autenticação stateless e autorização feitas com o módulo Spring Security e JWT(JSON Web Token), além de testes unitários das camadas (repository, service, controller) com JUnit5 + Mockito.

<br />

<p align="center">
  <a href="#technologies">Tecnologias utilizadas</a> •
  <a href="#functions">Funções/Recursos</a> •
  <a href="#routes">Rotas da API</a> •
  <a href="#license">Licença</a> •
</p>

<br />

<h2 id="technologies">💻 Tecnologias utilizadas</h2>
As ferramentas que foram utilizadas na construção do projeto:

- [Java 17](https://docs.oracle.com/en/java/javase/17)
- [Spring Boot 3 (Spring 6)](https://spring.io/projects/spring-boot#overview)
- [Spring Security 6](https://docs.spring.io/spring-security/reference/index.html)
- [Maven](https://maven.apache.org/)
- [JPA + Hibernate](https://spring.io/projects/spring-data-jpa#overview)
- [PostgreSQL](https://www.postgresql.org/)
- [JUnit5 + Mockito](https://docs.spring.io/spring-framework/reference/testing.html)
- [JWT (JSON Web Token)](https://github.com/auth0/java-jwt)
- [Docker](https://www.docker.com/)

<br />

<h2 id="functions">🚀 Funções/Recursos</h2>
Principais recursos e funções da aplicação:

- **Autenticação e Autorização:** Sistema de autenticação stateless com JWT (JSON Web Token) e autorização/proteção das rotas da API feitos com o módulo Spring Security.
- **Camadas:** Divisão da aplicação em 3 camadas principais: `Repository`, `Service` e `Controller`. Fazendo com que as reponsabilidades da aplicação fiquem bem definidas e separadas, melhorando as possibilidades de escalonamento e manutenibilidade.
- **Testes unitários:** Testes unitários das camadas `Repository`, `Service` e `Controller` feitos com o JUnit5 em conjunto com o Mockito.
- **Tratamento de exceções:** Centralização do tratamento de todas as exceções da aplicação em um `Rest Controller Advice`.
- **DTO(Data Transfer Objects):** Utilização de [Java Records](https://docs.oracle.com/en/java/javase/14/language/records.html#GUID-6699E26F-4A9B-4393-A08B-1E47D4B2D263) como DTOs para transferência de dados entre as camadas de `service` e `controller`.
- **Validação:** Validação dos dados das requisições com o Hibernate/Jakarta Validation.
- **Armazenamento:** Armazenamento dos dados em um banco de dados Postgres executando em container Docker.

<br />

<h2 id="routes">🧭 Rotas da API</h2>

Rotas referentes ao `User`:

| Tipo | Rota | Ação |
| ---  | ---- | ---- |
|[![](https://img.shields.io/badge/POST-4682B4?style=for-the-badge)]() | `http://localhost:8080/api/auth/login`
| Logar na aplicação |



