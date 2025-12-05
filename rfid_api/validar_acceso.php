<?php
header("Content-Type: text/plain");
require_once "config.php";

$uid = $_GET["uid"] ?? "";

if ($uid == "") {
    echo "ERROR_UID";
    exit;
}

$sql = "SELECT id_usuario FROM sensores WHERE codigo_sensor=? LIMIT 1";
$stmt = $conexion->prepare($sql);
$stmt->bind_param("s", $uid);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows > 0) {
    $row = $res->fetch_assoc();

    if ($row["id_usuario"] !== null) {
        echo "PERMITIDO|" . $row["id_usuario"];
    } else {
        echo "DENEGADO|NULL";
    }

} else {
    echo "DENEGADO|NULL";
}
?>
