<?php
// instalar.php - Haider Pabon Mejia
// Abre http://localhost/crudphpjson/instalar.php UNA vez para crear la BD y la tabla.
header("Content-Type: application/json; charset=utf-8");
try {
    $cx = new mysqli("localhost", "root", "");
    $cx->set_charset("utf8mb4");
    $sql = file_get_contents(__DIR__ . "/crudphpjson.sql");
    $cx->multi_query($sql);
    do { if ($r = $cx->store_result()) { $r->free(); } } while ($cx->more_results() && $cx->next_result());
    echo json_encode(array("mensaje" => "OK: base de datos crudphpjson lista"));
} catch (Exception $e) {
    echo json_encode(array("mensaje" => $e->getMessage()));
}
