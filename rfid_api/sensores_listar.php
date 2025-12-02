<?php 
header("Content-Type: application/json");
require_once "config.php";

$sql = "SELECT s.*, d.numero, d.torre, d.piso
        FROM sensores s
        LEFT JOIN departamentos d 
        ON s.id_departamento = d.id_departamento";

$res = $conexion->query($sql);
$data = [];

while ($row = $res->fetch_assoc()) { 
    $data[] = $row; 
}

echo json_encode($data);
?>
