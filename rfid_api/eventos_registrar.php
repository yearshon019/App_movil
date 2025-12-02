<?php
header("Content-Type: application/json");
require_once "config.php";

$codigo   = $_POST["codigo_sensor"];
$tipo     = $_POST["tipo_evento"];
$result   = $_POST["resultado"];
$id_usuario = $_POST["id_usuario"];

// obtener id_sensor
$cons = $conexion->query("SELECT id_sensor FROM sensores WHERE codigo_sensor='$codigo'");
$row = $cons->fetch_assoc();
$id_sensor = $row["id_sensor"];

$sql = "INSERT INTO eventos_acceso(id_sensor,id_usuario,tipo_evento,fecha_hora,resultado)
        VALUES(?,?,?,?,?)";

$stmt = $conexion->prepare($sql);
$fecha = date("Y-m-d H:i:s");
$stmt->bind_param("iisss", $id_sensor,$id_usuario,$tipo,$fecha,$result);

echo json_encode(["ok" => $stmt->execute()]);
?>
