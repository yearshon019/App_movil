<?php
header('Content-Type: application/json');
require_once "config.php";

$sql = "SELECT id_evento, tipo_evento, resultado 
        FROM eventos_acceso
        WHERE tipo_evento IN ('PERMISO_MANUAL','RECHAZO_MANUAL')
        ORDER BY id_evento DESC
        LIMIT 1";

$res = $conexion->query($sql);

if ($res && $res->num_rows > 0) {
    echo json_encode($res->fetch_assoc());
} else {
    echo json_encode(["ok" => false]);
}
