CREATE DATABASE employee_db
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE employee_db;

CREATE TABLE employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

