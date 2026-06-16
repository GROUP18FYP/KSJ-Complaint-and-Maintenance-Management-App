<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "ksj_complaint_db";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    header('Content-Type: application/json');
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}
// Do not type anything after this line!