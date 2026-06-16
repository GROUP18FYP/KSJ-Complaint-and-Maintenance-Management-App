<?php
header('Content-Type: application/json');
include 'db.php';

// Turn off errors to ensure only clean JSON is sent
error_reporting(0);
ini_set('display_errors', 0);

$user_id = $_POST['user_id'] ?? null;
$name = $_POST['name'] ?? '';
$room = $_POST['room_number'] ?? '';
$date_prefer = $_POST['date_prefer_service'] ?? '';
$phone = $_POST['phone_number'] ?? '';
$type = $_POST['complaint_type'] ?? '';
$desc = $_POST['description'] ?? '';
$image_data = $_POST['image'] ?? null;

if (!$user_id) {
    echo "error: User ID is required";
    exit;
}

$image_url = "";
if ($image_data) {
    $target_dir = "uploads/";
    if (!file_exists($target_dir)) {
        mkdir($target_dir, 0777, true);
    }

    $filename = "img_" . time() . "_" . rand(1000, 9999) . ".jpg";
    $target_file = $target_dir . $filename;

    if (file_put_contents($target_file, base64_decode($image_data))) {
        $image_url = "uploads/" . $filename;
    }
}

$sql = "INSERT INTO complaints (user_id, name, room_number, date_prefer_service, phone_number, complaint_type, description, image_url, status, priority)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'pending', 'low')";

$stmt = $conn->prepare($sql);
if ($stmt) {
    $stmt->bind_param("isssssss", $user_id, $name, $room, $date_prefer, $phone, $type, $desc, $image_url);
    if ($stmt->execute()) {
        echo "success";
    } else {
        echo "error: " . $stmt->error;
    }
    $stmt->close();
} else {
    echo "error: SQL preparation failed - " . $conn->error;
}

$conn->close();
?>
