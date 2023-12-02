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

<h2 id="functions">🚀 Funções/Recursos</h2>

Principais recursos e funções da aplicação:

- **Autenticação e Autorização:** Sistema de autenticação stateless com JWT (JSON Web Token) e autorização/proteção das rotas da API feitos com o módulo Spring Security.
- **Camadas:** Divisão da aplicação em 3 camadas principais: `Repository`, `Service` e `Controller`. Fazendo com que as reponsabilidades da aplicação fiquem bem definidas e separadas, melhorando as possibilidades de escalonamento e manutenibilidade.
- **Testes unitários:** Testes unitários das camadas `Repository`, `Service` e `Controller` feitos com o JUnit5 em conjunto com o Mockito.
- **Tratamento de exceções:** Centralização do tratamento de todas as exceções da aplicação em um `Rest Controller Advice`.
- **DTO(Data Transfer Objects):** Utilização de [Java Records](https://docs.oracle.com/en/java/javase/14/language/records.html#GUID-6699E26F-4A9B-4393-A08B-1E47D4B2D263) como DTOs para transferência de dados entre as camadas de `service` e `controller`.
- **Validação:** Validação dos dados das requisições com o Hibernate/Jakarta Validation.
- **Armazenamento:** Armazenamento dos dados em um banco de dados Postgres executando em container Docker.

<h2 id="routes">🧭 Rotas da API</h2>

Rotas referentes ao `User`:

| Tipo                                                                    | Rota                                      | Ação             |
| ---------------------------------------------------------------------   | ----------------------------------------- | ---------------- |
| [![](https://img.shields.io/badge/POST-4682B4?style=for-the-badge)]()   | `http://localhost:8080/api/auth/login`    | Logar na aplicação |
| [![](https://img.shields.io/badge/POST-4682B4?style=for-the-badge)]()   | `http://localhost:8080/api/auth/register` | Criar um novo usuário |
| [![](https://img.shields.io/badge/GET-2E8B57?style=for-the-badge)]()    | `http://localhost:8080/api/profile/{id}`  | Ver informações do usuário autenticado |
| [![](https://img.shields.io/badge/DELETE-F74747?style=for-the-badge)]() | `http://localhost:8080/api/profile/{id}`  | Excluir usuário e todas as tarefas |

<br />

Rotas referentes a `Task`:

| Tipo                                                                    | Rota                             | Ação             |
| ----------------------------------------------------------------------- | -------------------------------- | ---------------- |
| [![](https://img.shields.io/badge/POST-4682B4?style=for-the-badge)]()   | `http://localhost:8080/api/task`                             | Criar tarefa |
| [![](https://img.shields.io/badge/GET-2E8B57?style=for-the-badge)]()    | `http://localhost:8080/api/task?field={field}&order={order}` | Listar todas as tarefas do usuário. As tarefas podem ser filtradas por campo(field) e ordem(order) crescente(asc) e decrescente(desc) |
| [![](https://img.shields.io/badge/GET-2E8B57?style=for-the-badge)]()    | `http://localhost:8080/api/task/done?status={status}`  | Listar todas as tarefas marcadas como feitas ou não feitas de acordo com o valor passado no parâmetro `status`. Valores aceitos: `true` ou `false` |
| [![](https://img.shields.io/badge/GET-2E8B57?style=for-the-badge)]()    | `http://localhost:8080/api/task/{id}`  | Ver tarefa específica de acordo com o `id` passado |
| [![](https://img.shields.io/badge/PATCH-9370DB?style=for-the-badge)]()  | `http://localhost:8080/api/task/{id}`  | Atualizar tarefa específica de acordo com `id` passado |
| [![](https://img.shields.io/badge/DELETE-F74747?style=for-the-badge)]() | `http://localhost:8080/api/task/{id}`  | Deletar tarefa específica de acordo com `id` passado |

<br />
