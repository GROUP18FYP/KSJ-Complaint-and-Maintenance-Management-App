<?php
header('Content-Type: application/json');
include 'db.php';

error_reporting(0);
ini_set('display_errors', 0);

$user_id = $_POST['user_id'] ?? null;
$staff_id = $_POST['staff_id'] ?? null;
$rating = $_POST['rating'] ?? 0;
$message = $_POST['message'] ?? '';

if (!$user_id || !$staff_id) {
    echo "error: Missing information";
    exit;
}

$sql = "INSERT INTO feedback (user_id, staff_id, rating, message) VALUES (?, ?, ?, ?)";
$stmt = $conn->prepare($sql);

if ($stmt) {
    $stmt->bind_param("iiis", $user_id, $staff_id, $rating, $message);
    if ($stmt->execute()) {
        echo "success";
    } else {
        echo "error: " . $stmt->error;
    }
    $stmt->close();
} else {
    echo "error: SQL preparation failed";
}

$conn->close();
?>
