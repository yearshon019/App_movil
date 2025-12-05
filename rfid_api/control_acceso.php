<?php
header("Content-Type: application/json");
require_once "config.php";

$id_evento  = $_POST["id_evento"] ?? null;
$id_usuario = $_POST["id_usuario"] ?? null;
$accion     = $_POST["accion"] ?? null; // PERMITIR o DENEGAR

if (!$id_evento || !$id_usuario || !$accion) {
    echo json_encode(["ok" => false, "error" => "Datos incompletos"]);
    exit;
}

if ($accion == "PERMITIR") {
    $tipo_evento = "PERMISO_MANUAL";
    $resultado   = "PERMITIDO";
} else {
    $tipo_evento = "RECHAZO_MANUAL";
    $resultado   = "DENEGADO";
}

// Actualizar el evento para que Arduino lo lea
$sql = "UPDATE eventos_acceso
        SET tipo_evento=?, resultado=?, id_usuario=?
        WHERE id_evento=?";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("ssii", $tipo_evento, $resultado, $id_usuario, $id_evento);

$ok = $stmt->execute();

echo json_encode(["ok" => $ok]);
exit;
