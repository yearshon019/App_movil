<?php
header("Content-Type: application/json");

$uid = $_GET["uid"] ?? "";

if ($uid == "") {
    echo json_encode(["ok" => false, "msg" => "UID vacío"]);
    exit;
}

file_put_contents("ultimo_uid.txt", $uid);

echo json_encode(["ok" => true, "uid" => $uid]);
?>
