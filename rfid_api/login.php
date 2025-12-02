<?php
header("Content-Type: application/json; charset=utf-8");
require_once "config.php";

$email    = $_GET['email'] ?? '';
$password = $_GET['password_hash'] ?? '';

$sql = "SELECT * FROM usuarios WHERE email=? AND password_hash=? LIMIT 1";
$stmt = $conexion->prepare($sql);
$stmt->bind_param("ss", $email, $password);
$stmt->execute();
$res = $stmt->get_result();

if ($row = $res->fetch_assoc()) {
    echo json_encode([
        "ok" => true,
        "id_usuario" => $row["id_usuario"],
        "nombre"     => $row["nombre"],
        "estado"     => $row["estado"],
        "rol"        => $row["rol"]
    ]);
} else {
    echo json_encode(["ok" => false, "mensaje" => "Credenciales incorrectas"]);
}
?>
