<?php
header("Content-Type: application/json");
require_once "config.php";

$nombre   = $_POST["nombre"];
$email    = $_POST["email"];
$password = $_POST["password"]; // viene desde Kotlin
$telefono = $_POST["telefono"];
$rut      = $_POST["rut"];
$estado   = $_POST["estado"];
$rol      = $_POST["rol"];
$id_departamento = $_POST["id_departamento"];  // 🔵 NUEVO Y OBLIGATORIO

// Encriptar contraseña
$password_hash = password_hash($password, PASSWORD_DEFAULT);

$sql = "INSERT INTO usuarios(nombre,email,password_hash,telefono,rut,estado,rol,id_departamento)
        VALUES(?,?,?,?,?,?,?,?)";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("sssssssi",
    $nombre,
    $email,
    $password_hash,
    $telefono,
    $rut,
    $estado,
    $rol,
    $id_departamento
);

echo json_encode(["ok" => $stmt->execute()]);
?>
