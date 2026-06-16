<?php
header('Content-Type: application/json');
include 'db.php';

error_reporting(0);
ini_set('display_errors', 0);

$sql = "SELECT user_id, fullname as name FROM staff";
$result = $conn->query($sql);

$staff = array();
if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $staff[] = $row;
    }
}

echo json_encode($staff);
$conn->close();
?>
