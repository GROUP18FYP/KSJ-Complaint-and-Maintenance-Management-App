<?php
header('Content-Type: application/json');
include 'db.php';

error_reporting(0);
ini_set('display_errors', 0);

$user_id = $_POST['user_id'] ?? null;

if ($user_id) {
    $sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $user_id);

    if ($stmt->execute()) {
        echo json_encode(["status" => "success"]);
    } else {
        echo json_encode(["status" => "error", "message" => $conn->error]);
    }
    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "Missing user_id"]);
}

$conn->close();
?>
