<?php
header("Content-Type: application/json");
require_once "config.php";

$res = $conexion->query("SELECT * FROM sensores");
$data = [];

while ($row = $res->fetch_assoc()) { $data[] = $row; }
echo json_encode($data);
?>
