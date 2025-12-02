<?php
header("Content-Type: application/json");
require_once "config.php";

$sql = "SELECT 
            u.id_usuario,
            u.nombre,
            u.email,
            u.telefono,
            u.rut,
            u.estado,
            u.rol,
            u.id_departamento,
            d.numero,
            d.torre,
            d.piso
        FROM usuarios u
        LEFT JOIN departamentos d ON d.id_departamento = u.id_departamento";

$res = $conexion->query($sql);

$data = [];
while ($row = $res->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode($data);
?>
