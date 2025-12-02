<?php
header("Content-Type: application/json");
require_once "config.php";

$codigo = $_POST["codigo_sensor"];
$estado = $_POST["estado"];
$tipo   = $_POST["tipo"];
$dep    = $_POST["id_departamento"];

$sql = "INSERT INTO sensores(codigo_sensor,estado,tipo,id_departamento,fecha_alta)
        VALUES(?,?,?,?,NOW())";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("sssi", $codigo,$estado,$tipo,$dep);

echo json_encode(["ok" => $stmt->execute()]);
?>
