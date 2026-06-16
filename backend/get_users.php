<?php
$host = "localhost";
$db_user = "root";
$db_pass = "";
$db_name = "ksj_complaint_db";

$conn = new mysqli($host, $db_user, $db_pass, $db_name);

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Connection failed"]));
}

// MATCHED TO SCREENSHOT: Using 'name' instead of 'fullname'
$sql = "SELECT user_id, name, email, role FROM users ORDER BY role ASC, name ASC";
$result = $conn->query($sql);

$users = array();

if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $users[] = $row;
    }
}

header('Content-Type: application/json');
echo json_encode($users);

$conn->close();
?>