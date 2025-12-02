<?php
header("Content-Type: application/json");
require_once "config.php";

$id_sensor = $_POST["id_sensor"] ?? null;
$estado    = $_POST["estado"] ?? null;
$tipo      = $_POST["tipo"] ?? null;

if (!$id_sensor || !$estado || !$tipo) {
    echo json_encode(["ok" => false, "msg" => "Datos incompletos"]);
    exit;
}

// Opcional: manejar fecha_baja según estado
$sql = "UPDATE sensores 
        SET estado = ?, tipo = ?, 
            fecha_baja = IF(? = 'ACTIVO', NULL, NOW())
        WHERE id_sensor = ?";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("sssi", $estado, $tipo, $estado, $id_sensor);

$ok = $stmt->execute();

echo json_encode(["ok" => $ok]);
?>
