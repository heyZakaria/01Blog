# Spring Boot & Database Learning Reference

## JDBC

* **What offering:**

  * Direct Java API to connect and interact with DB
  * Execute SQL, handle ResultSet
  * Example: `Connection → PreparedStatement → ResultSet`
* **Problems:**

  * Lots of boilerplate (open/close connections, handle exceptions)
  * Manual mapping from ResultSet → Object
  * Expensive for large apps
* **Solution (next feature):** Spring JDBC

---

## Spring JDBC

* **What offering:**

  * `JdbcTemplate` handles connection, exceptions, resources
  * Simplifies SQL execution and mapping
  * Example: `jdbcTemplate.query("SELECT * FROM users", rowMapper)`
* **Problems:**

  * Still writing SQL manually
  * Still mapping manually
* **Solution (next feature):** JPA

---

## JPA (Java Persistence API)

* **What offering:**

  * Specification for object-relational mapping
  * Maps Java objects to DB tables (Entity → Table, Object → Row)
  * Example: `User user = entityManager.find(User.class, 1)`
* **Problems:**

  * JPA itself does not execute SQL, needs an implementation
* **Solution (next feature):** Hibernate

---

## Hibernate

* **What offering:**

  * Implementation of JPA
  * Auto-generates SQL, manages Persistence Context
  * Handles caching, relations, lazy/eager loading
  * Example: `@Entity class User { @Id Long id; String name; }`
* **Problems:**

  * Still requires EntityManager and transactions for manual operations
* **Solution (next feature):** Spring Data JPA

---

## Spring ORM

* **What offering:**

  * Integrates Spring with ORM frameworks (Hibernate, JPA, JDO)
  * Manages transactions and exception translation
* **Problems:**

  * Rarely used directly
* **Solution (next feature):** Spring Boot + Spring Data JPA

---

## Spring Boot JPA (Spring Data JPA)

* **What offering:**

  * Auto-configuration: JPA, Hibernate, DataSource, TransactionManager
  * `JpaRepository` gives CRUD, pagination, sorting automatically
  * Example: `interface UserRepo extends JpaRepository<User, Long> {}`
* **Problems:**

  * Limited control for complex queries (solved via `@Query` or custom repo)
* Everyday usage with minimal boilerplate

---

## Summary Table

| Concept         | Offering                                          | Problem                     | Next Feature    |
| --------------- | ------------------------------------------------- | --------------------------- | --------------- |
| JDBC            | Direct DB API, SQL execution                      | Boilerplate, manual mapping | Spring JDBC     |
| Spring JDBC     | JdbcTemplate, connection & exception handling     | Still manual SQL & mapping  | JPA             |
| JPA             | Object-relational mapping (Entities)              | Needs implementation        | Hibernate       |
| Hibernate       | Auto SQL, persistence context, caching, relations | Still EntityManager needed  | Spring Data JPA |
| Spring ORM      | Spring integration with ORMs                      | Rarely used directly        | Spring Data JPA |
| Spring Boot JPA | Repositories, auto CRUD, transaction management   | Limited complex queries     | Everyday usage  |
