<?php
// conexion_bd.php - Haider Pabon Mejia
// Conexion de PHP con el servidor MySQL de WAMP

$bd = NULL;

function conectar() {
    try {
        global $bd;
        // servidor, usuario, clave, base de datos
        $bd = new mysqli("localhost", "root", "", "crudphpjson");
        $bd->set_charset("utf8mb4");
    } catch (Exception $error) {
        // relanzamos el error con su mensaje original
        throw new Exception($error->getMessage());
    }
}

function consultar($sql) {
    global $bd;
    $res = NULL;
    try {
        if ($bd == NULL) {
            conectar();
        }
        return $bd->query($sql);
    } catch (Exception $error) {
        // relanzamos el error con su mensaje original
        throw new Exception($error->getMessage());
    }
}

// Escapa los textos que llegan de la app para evitar errores con comillas
// (y ataques de inyeccion SQL)
function limpiar($valor) {
    global $bd;
    if ($bd == NULL) {
        conectar();
    }
    return $bd->real_escape_string(trim((string) $valor));
}
