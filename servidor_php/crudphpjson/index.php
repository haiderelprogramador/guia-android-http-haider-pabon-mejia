<?php
// index.php - Haider Pabon Mejia
// Prueba rapida: http://localhost/crudphpjson/
header("Content-Type: application/json; charset=utf-8");
echo json_encode(array(
    "mensaje" => "Servicio HTTP crudphpjson funcionando",
    "autor" => "Haider Pabon Mejia",
    "servicio" => "crud/operacion.php?accion=listar"
));
