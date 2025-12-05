<?php
header("Content-Type: application/json");
require_once "config.php";

// Recibir parámetros GET o POST
$codigo     = $_REQUEST["uid"] ?? "";
$tipo       = trim($_REQUEST["tipo_evento"] ?? "");
$result     = trim($_REQUEST["resultado"] ?? "");
$id_usuario = $_REQUEST["id_usuario"] ?? 1;

if ($codigo == "") {
    echo json_encode(["ok" => false, "error" => "UID vacío"]);
    exit;
}

// ENUM permitidos según tu base
$tipos_validos = ["ACCESO_VALIDO", "ACCESO_RECHAZADO", "APERTURA_MANUAL"];
$resultados_validos = ["PERMITIDO", "DENEGADO"];

// Validación ENUM estricta
if (!in_array($tipo, $tipos_validos)) {
    $tipo = "ACCESO_VALIDO";
}

if (!in_array($result, $resultados_validos)) {
    $result = "DENEGADO";
}

// Buscar sensor
$cons = $conexion->prepare("SELECT id_sensor FROM sensores WHERE codigo_sensor=? LIMIT 1");
$cons->bind_param("s", $codigo);
$cons->execute();
$res = $cons->get_result();

if ($res->num_rows == 0) {
    $tipo = "ACCESO_RECHAZADO";
    $result = "DENEGADO"; // UID no registrado
    $id_sensor = null;
} else {
    $row = $res->fetch_assoc();
    $id_sensor = $row["id_sensor"];
}

// Insertar evento
$sql = "INSERT INTO eventos_acceso(id_sensor,id_usuario,tipo_evento,fecha_hora,resultado)
        VALUES(?,?,?,?,?)";

$stmt = $conexion->prepare($sql);
$fecha = date("Y-m-d H:i:s");
$stmt->bind_param("iisss", $id_sensor,$id_usuario,$tipo,$fecha,$result);

echo json_encode(["ok" => $stmt->execute()]);
?>
