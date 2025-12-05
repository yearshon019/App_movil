<?php 
header("Content-Type: application/json");
require_once "config.php";

$sql = "SELECT 
            s.id_sensor,
            s.codigo_sensor,
            s.tipo,
            s.estado,
            u.nombre AS usuario,
            d.numero,
            d.torre,
            d.piso
        FROM sensores s
        LEFT JOIN usuarios u ON s.id_usuario = u.id_usuario
        LEFT JOIN departamentos d ON u.id_departamento = d.id_departamento
        ORDER BY s.id_sensor DESC";

$res = $conexion->query($sql);
$data = [];

while ($row = $res->fetch_assoc()) { 
    if (!$row["usuario"]) $row["usuario"] = "Desconocido";
    $data[] = $row;
}

echo json_encode($data);
?>
