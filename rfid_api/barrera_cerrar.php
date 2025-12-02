<?php
header("Content-Type: application/json");
require_once "config.php";

$id_usuario = $_POST["id_usuario"] ?? null;

if (!$id_usuario) {
    echo json_encode([
        "ok" => false,
        "msg" => "id_usuario faltante"
    ]);
    exit;
}

// ------------------------------------------------------
// OBTENER EL DEPARTAMENTO DEL USUARIO
// ------------------------------------------------------
$sqlDep = "SELECT d.numero, d.torre, d.piso
           FROM usuarios u
           LEFT JOIN departamentos d ON u.id_departamento = d.id_departamento
           WHERE u.id_usuario = ?
           LIMIT 1";

stmtDep = $conexion->prepare($sqlDep);
$stmtDep->bind_param("i", $id_usuario);
$stmtDep->execute();
$depRes = $stmtDep->get_result()->fetch_assoc();

// Si NO existe departamento → evitar NULLs
if (!$depRes) {
    $depRes = [
        "numero" => "N/A",
        "torre"  => "N/A",
        "piso"   => "N/A"
    ];
}

// ------------------------------------------------------
// REGISTRAR CIERRE MANUAL
// ------------------------------------------------------
$sql = "INSERT INTO eventos_acceso (id_sensor, id_usuario, tipo_evento, fecha_hora, resultado)
        VALUES (NULL, ?, 'CIERRE_MANUAL', NOW(), 'PERMITIDO')";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("i", $id_usuario);
$stmt->execute();

echo json_encode([
    "ok" => true,
    "accion" => "CERRAR",
    "id_evento" => $stmt->insert_id,
    "departamento" => $depRes
]);
?>
