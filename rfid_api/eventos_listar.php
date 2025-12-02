<?php
header("Content-Type: application/json");
require_once "config.php";

$id_usuario = $_POST["id_usuario"] ?? null;
$rol = $_POST["rol"] ?? null;

if (!$rol) {
    echo json_encode([]);
    exit;
}

// -------------------------------
// CONSULTA BASE (ACCESOS REALES)
// -------------------------------
if ($rol == "ADMIN") {

    $sql = "SELECT e.*, u.nombre, s.codigo_sensor, s.tipo AS tipo_sensor,
                   d.numero, d.torre, d.piso
            FROM eventos_acceso e
            LEFT JOIN usuarios u ON e.id_usuario = u.id_usuario
            LEFT JOIN sensores s ON e.id_sensor = s.id_sensor
            LEFT JOIN departamentos d ON s.id_departamento = d.id_departamento
            ORDER BY e.fecha_hora DESC";

} else {

    $sql = "SELECT e.*, u.nombre, s.codigo_sensor, s.tipo AS tipo_sensor,
                   d.numero, d.torre, d.piso
            FROM eventos_acceso e
            LEFT JOIN usuarios u ON e.id_usuario = u.id_usuario
            LEFT JOIN sensores s ON e.id_sensor = s.id_sensor
            LEFT JOIN departamentos d ON s.id_departamento = d.id_departamento
            WHERE e.id_usuario = ?
            ORDER BY e.fecha_hora DESC";

}

$stmt = $conexion->prepare($sql);

if ($rol != "ADMIN") {
    $stmt->bind_param("i", $id_usuario);
}

$stmt->execute();
$res = $stmt->get_result();

$data = [];

while ($row = $res->fetch_assoc()) {

    // ------------------------------
    // SI EL EVENTO ES MANUAL → AGREGAR DEPARTAMENTO DEL USUARIO
    // ------------------------------
    if ($row["tipo_evento"] == "APERTURA_MANUAL" || $row["tipo_evento"] == "CIERRE_MANUAL") {

        $sqlDep = "SELECT d.numero, d.torre, d.piso
                   FROM usuarios u
                   LEFT JOIN departamentos d ON u.id_departamento = d.id_departamento
                   WHERE u.id_usuario = ?
                   LIMIT 1";

        $stmtDep = $conexion->prepare($sqlDep);
        $stmtDep->bind_param("i", $row["id_usuario"]);
        $stmtDep->execute();
        $dep = $stmtDep->get_result()->fetch_assoc();

        // Esto envía el departamento que sí existe
        $row["departamento"] = $dep;
    }

    $data[] = $row;
}

echo json_encode($data);
?>
