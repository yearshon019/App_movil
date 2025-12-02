<?php
header("Content-Type: application/json");
require_once "config.php";

// LEE el UID que viene desde Android
$codigo = $_GET["codigo_sensor"];

// BUSCA si ese sensor existe y pertenece a un usuario
$sql = "SELECT u.id_usuario, u.nombre, u.email, u.estado, u.rol
        FROM sensores s
        INNER JOIN usuarios u ON u.id_usuario = s.id_usuario
        WHERE s.codigo_sensor = '$codigo'";

$res = $conexion->query($sql);

if ($res->num_rows == 0) {
    echo json_encode(["ok" => false, "mensaje" => "Tarjeta no registrada"]);
    exit;
}

$datos = $res->fetch_assoc();

echo json_encode([
    "ok" => true,
    "id_usuario" => $datos["id_usuario"],
    "nombre" => $datos["nombre"],
    "email" => $datos["email"],
    "estado" => $datos["estado"],
    "rol" => $datos["rol"]
]);
?>
