# Real-Time Fraud Flagging System

This is an enterprise-level backend service built with Java and Spring Boot. It provides a REST API to process transactions and applies a set of configurable rules to flag potentially fraudulent activity in real-time.

This project demonstrates a robust, N-tier architecture to handle complex business logic, data persistence, and auditing.

## Tech Stack

* **Language:** Java
* **Framework:** Spring Boot
* **Data:** Spring Data JPA with PostgreSQL
* **Testing:** JUnit
* **Build:** Maven

## Key Features & Architecture

This project is built using a clean separation of concerns:

* **Controllers**: Handles all incoming HTTP requests for transactions, rules, and fraud reports.
* **Services**: Contains all core business logic. When a transaction is received, it is passed to the `RulesService` to be evaluated.
* **Rules Engine**: A custom-built engine that checks a transaction against a dynamic set of rules, such as:
    * `AmountCheckService`: Flags transactions over a certain value.
    * `LocationCheckService`: Flags transactions from high-risk locations.
    * `BottingCheckService`: Flags rapid, repetitive transactions from a single user.
* **Repositories**: Manages all database operations using Spring Data JPA.
* **Entities & DTOs**: Uses database-first entities for persistence and DTOs to ensure clean data transfer to and from the API.
* **Auditing**: A full audit trail system that logs all changes to transactions and fraud rules for security and traceability.
* **Unit Testing**: Includes JUnit tests to validate the business logic of the rules engine.
