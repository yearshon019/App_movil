<?php
header("Content-Type: application/json");
require_once "config.php";

// -----------------------
// LEER id_usuario
// -----------------------
$id_usuario = $_POST["id_usuario"] ?? null;

if (!$id_usuario) {
    echo json_encode(["ok" => false, "error" => "id_usuario faltante"]);
    exit;
}

// -----------------------
// OBTENER DEPARTAMENTO
// -----------------------
$sqlDepto = "SELECT d.numero, d.torre, d.piso
             FROM usuarios u
             JOIN departamentos d ON u.id_departamento = d.id_departamento
             WHERE u.id_usuario = ?";

$stmtDepto = $conexion->prepare($sqlDepto);
$stmtDepto->bind_param("i", $id_usuario);
$stmtDepto->execute();
$depto = $stmtDepto->get_result()->fetch_assoc();


// -----------------------
// REGISTRAR EVENTO CIERRE
// -----------------------
$sql = "INSERT INTO eventos_acceso 
(id_usuario, tipo_evento, resultado, fecha_hora)
VALUES (?, 'CIERRE_MANUAL', 'PERMITIDO', NOW())";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("i", $id_usuario);
$stmt->execute();


// -----------------------
echo json_encode([
    "ok" => true,
    "accion" => "CERRAR",
    "departamento" => $depto
]);
?>
