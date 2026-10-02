<?php
// operacion.php - Haider Pabon Mejia
// Servicio HTTP: recibe parametros (GET o POST) y responde en JSON
require_once '../bd/conexion_bd.php';

header("Content-Type: application/json; charset=utf-8");

$accion = @$_REQUEST["accion"];
switch ($accion) {
    case "login":
        login();
        break;
    case "Agregar":
        guardar();
        break;
    case "editar":
        guardar();
        break;
    case "listar":
        listar();
        break;
    case "eliminar":
        eliminar();
        break;
    default:
        echo json_encode(array("mensaje" => "Accion no valida"));
        break;
}

function login() {
    try {
        $email = limpiar(@$_REQUEST["email"]);
        $pass = limpiar(@$_REQUEST["psw"]);
        $res = consultar("SELECT * FROM Usuarios WHERE email ='$email' AND password='$pass'");

        if ($res != NULL && $res->num_rows > 0) {
            $json = json_encode($res->fetch_assoc());
            echo $json;
            /* liberar el conjunto de resultados */
            $res->free();
        } else {
            echo json_encode(array("mensaje" => "Acceso denegado"));
        }
    } catch (Exception $error) {
        echo json_encode(array("mensaje" => $error->getMessage()));
    }
}

function eliminar() {
    try {
        $email = limpiar(@$_REQUEST["email"]);
        $res = consultar("SELECT * FROM Usuarios WHERE email = '$email'");
        if ($res != NULL && $res->num_rows > 0) {
            $res = consultar("DELETE FROM Usuarios WHERE email ='$email'");
            echo json_encode(array("mensaje" => "OK"));
        } else {
            echo json_encode(array("mensaje" => "Usuario no existe"));
        }
    } catch (Exception $error) {
        echo json_encode(array("mensaje" => $error->getMessage()));
    }
}

function guardar() {
    try {
        $email = limpiar(@$_REQUEST["email"]);
        $pass = limpiar(@$_REQUEST["psw"]);
        $nombre = limpiar(@$_REQUEST["nombre"]);
        if ($email == "" || $pass == "" || $nombre == "") {
            echo json_encode(array("mensaje" => "Faltan datos"));
            return;
        }
        $res = consultar("SELECT * FROM Usuarios WHERE email = '$email'");
        if ($res != NULL && $res->num_rows > 0) {
            $res = consultar("UPDATE Usuarios SET password ='$pass', nombre = '$nombre' WHERE email = '$email'");
        } else {
            $res = consultar("INSERT INTO Usuarios VALUES('$email','$pass','$nombre')");
        }
        echo json_encode(array("mensaje" => "OK"));
    } catch (Exception $error) {
        echo json_encode(array("mensaje" => "Usuario No Registrado"));
    }
}

function listar() {
    try {
        $res = consultar("SELECT * FROM Usuarios");
        if ($res != NULL && $res->num_rows > 0) {
            $json = json_encode($res->fetch_all(MYSQLI_ASSOC));
            echo $json;
            /* liberar el conjunto de resultados */
            $res->free();
        } else {
            echo json_encode(array("mensaje" => "No hay Usuarios"));
        }
    } catch (Exception $error) {
        echo json_encode(array("mensaje" => $error->getMessage()));
    }
}
