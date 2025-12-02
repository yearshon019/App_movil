<?php
header("Content-Type: text/plain");

if (file_exists("ultimo_uid.txt")) {
    echo trim(file_get_contents("ultimo_uid.txt"));
} else {
    echo "";
}
?>
