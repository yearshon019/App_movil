<?php
header('Content-Type: application/json');
require_once "config.php";

$id = $_POST["id_evento"] ?? null;
$accion = $_POST["accion"] ?? null; // OK = ejecutado por Arduino

if (!$id || !$accion) {
    echo json_encode(["ok" => false, "error" => "Datos incompletos"]);
    exit;
}

// Restauramos evento como ACCESO con resultado FINAL
$sql = "UPDATE eventos_acceso
        SET tipo_evento='ACCESO', resultado='PROCESADO'
        WHERE id_evento=?";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("i", $id);

echo json_encode(["ok" => $stmt->execute()]);
