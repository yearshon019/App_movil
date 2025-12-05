<?php
header("Content-Type: application/json");
require_once "config.php";

$codigo     = $_POST["codigo_sensor"] ?? "";
$tipo       = $_POST["tipo"] ?? "";
$estado     = $_POST["estado"] ?? "";
$id_usuario = $_POST["id_usuario"] ?? "";

if ($codigo == "" || $id_usuario == "") {
    echo json_encode(["ok" => false, "error" => "Datos incompletos"]);
    exit;
}

// Obtener departamento del usuario
$sqlDep = "SELECT id_departamento FROM usuarios WHERE id_usuario=? LIMIT 1";
$stmtDep = $conexion->prepare($sqlDep);
$stmtDep->bind_param("i", $id_usuario);
$stmtDep->execute();
$row = $stmtDep->get_result()->fetch_assoc();
$id_departamento = $row ? $row["id_departamento"] : null;

// Insertar sensor
$sql = "INSERT INTO sensores (codigo_sensor, tipo, estado, id_usuario, id_departamento)
        VALUES (?, ?, ?, ?, ?)";

$stmt = $conexion->prepare($sql);
$stmt->bind_param("sssii", $codigo, $tipo, $estado, $id_usuario, $id_departamento);

echo json_encode(["ok" => $stmt->execute()]);
?>
