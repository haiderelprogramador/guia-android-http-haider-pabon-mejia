-- ============================================================
-- Guia: App Android conectada a una aplicacion web (HTTP + JSON)
-- Estudiante: Haider Pabon Mejia
-- Base de datos: crudphpjson  (MySQL / MariaDB de WAMP)
-- ============================================================

-- 1. CREAR LA BASE DE DATOS
CREATE DATABASE IF NOT EXISTS crudphpjson
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE crudphpjson;

-- 2. CREAR LA TABLA
CREATE TABLE IF NOT EXISTS Usuarios (
    email    VARCHAR(70)  PRIMARY KEY NOT NULL,
    password VARCHAR(40)  NOT NULL,
    nombre   VARCHAR(100) NOT NULL
) ENGINE = InnoDB;

-- 3. USUARIO INICIAL (para poder iniciar sesion desde la app)
INSERT IGNORE INTO Usuarios (email, password, nombre)
VALUES ('haider@gmail.com', '1234', 'HAIDER PABON MEJIA');
