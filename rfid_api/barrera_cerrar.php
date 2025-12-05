<?php
header("Content-Type: application/json");
require_once "config.php";

$id_usuario = $_POST["id_usuario"] ?? null;

if (!$id_usuario) {
    echo json_encode(["ok" => false, "error" => "Usuario no enviado"]);
    exit;
}

$sql = "INSERT INTO eventos_acceso(id_sensor,id_usuario,tipo_evento,fecha_hora,resultado)
        VALUES(NULL, ?, 'CIERRE_MANUAL', NOW(), 'PERMITIDO')";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("i", $id_usuario);

echo json_encode(["ok" => $stmt->execute()]);
?>
