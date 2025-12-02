<?php
header("Content-Type: application/json");
require_once "config.php";

$id = $_POST["id_usuario"];

$sql = "DELETE FROM usuarios WHERE id_usuario=?";
$stmt = $conexion->prepare($sql);
$stmt->bind_param("i", $id);

echo json_encode(["ok" => $stmt->execute()]);
?>
