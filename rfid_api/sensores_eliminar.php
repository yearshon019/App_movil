<?php
header("Content-Type: application/json");
require_once "config.php";

$id_sensor = $_POST["id_sensor"] ?? null;

if (!$id_sensor) {
    echo json_encode(["ok" => false, "msg" => "id_sensor faltante"]);
    exit;
}

$sql = "DELETE FROM sensores WHERE id_sensor = ?";
$stmt = $conexion->prepare($sql);
$stmt->bind_param("i", $id_sensor);

$ok = $stmt->execute();

echo json_encode(["ok" => $ok]);
?>
