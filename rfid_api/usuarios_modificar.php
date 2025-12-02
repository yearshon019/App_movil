<?php
header("Content-Type: application/json");
require_once "config.php";

$id           = $_POST["id_usuario"];
$nombre       = $_POST["nombre"];
$email        = $_POST["email"];
$telefono     = $_POST["telefono"];
$rut          = $_POST["rut"];
$estado       = $_POST["estado"];
$rol          = $_POST["rol"];
$id_departamento = $_POST["id_departamento"]; // 🔵 NUEVO

$sql = "UPDATE usuarios 
        SET nombre=?, email=?, telefono=?, rut=?, estado=?, rol=?, id_departamento=?
        WHERE id_usuario=?";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("ssssssii",
    $nombre,
    $email,
    $telefono,
    $rut,
    $estado,
    $rol,
    $id_departamento,
    $id
);

echo json_encode(["ok" => $stmt->execute()]);
?>
