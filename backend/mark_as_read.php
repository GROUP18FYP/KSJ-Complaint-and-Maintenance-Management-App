<?php 
include 'db.php';
$id = $_POST['notification_id'];
$sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $id);
if($stmt->execute()) echo "success";
?>