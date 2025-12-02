<?php
header("Content-Type: application/json");
require_once "config.php";

$id     = $_POST["id_sensor"];
$estado = $_POST["estado"];

$sql = "UPDATE sensores SET estado=? WHERE id_sensor=?";
$stmt = $conexion->prepare($sql);
$stmt->bind_param("si", $estado,$id);

echo json_encode(["ok" => $stmt->execute()]);
?>
