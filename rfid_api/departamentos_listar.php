<?php
header("Content-Type: application/json");
require_once "config.php";

$res = $conexion->query("SELECT id_departamento, numero, torre FROM departamentos");

$data = [];

while ($row = $res->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode($data);
?>
