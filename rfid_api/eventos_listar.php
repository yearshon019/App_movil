<?php
header("Content-Type: application/json");
require_once "config.php";

$id_usuario = $_POST["id_usuario"] ?? null;
$rol = $_POST["rol"] ?? null;

if (!$rol) {
    echo json_encode([]);
    exit;
}

if ($rol == "ADMIN") {
    $sql = "SELECT e.*, 
                   u.nombre,
                   s.codigo_sensor,
                   s.tipo AS tipo_sensor,
                   COALESCE(d1.numero, d2.numero) AS numero,
                   COALESCE(d1.torre, d2.torre) AS torre,
                   COALESCE(d1.piso, d2.piso) AS piso
            FROM eventos_acceso e
            LEFT JOIN usuarios u ON e.id_usuario = u.id_usuario
            LEFT JOIN sensores s ON e.id_sensor = s.id_sensor
            LEFT JOIN departamentos d1 ON s.id_departamento = d1.id_departamento
            LEFT JOIN departamentos d2 ON u.id_departamento = d2.id_departamento
            ORDER BY e.fecha_hora DESC";
} else {
    $sql = "SELECT e.*, 
                   u.nombre,
                   s.codigo_sensor,
                   s.tipo AS tipo_sensor,
                   COALESCE(d1.numero, d2.numero) AS numero,
                   COALESCE(d1.torre, d2.torre) AS torre,
                   COALESCE(d1.piso, d2.piso) AS piso
            FROM eventos_acceso e
            LEFT JOIN usuarios u ON e.id_usuario = u.id_usuario
            LEFT JOIN sensores s ON e.id_sensor = s.id_sensor
            LEFT JOIN departamentos d1 ON s.id_departamento = d1.id_departamento
            LEFT JOIN departamentos d2 ON u.id_departamento = d2.id_departamento
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
    $data[] = $row;
}

echo json_encode($data);
?>
