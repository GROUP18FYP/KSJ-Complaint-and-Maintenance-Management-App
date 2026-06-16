<?php
header('Content-Type: application/json');
include 'db.php';

$id = $_POST['user_id'] ?? 0;

// Fetches the ID and Type for the list in UserInfo
$sql = "SELECT complaint_id, complaint_type FROM complaints WHERE user_id = ? ORDER BY created_at DESC";
$stmt = $conn->prepare($sql);

if ($stmt) {
    $stmt->bind_param("i", $id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $list = [];
    while($row = $result->fetch_assoc()) {
        $list[] = $row;
    }
    
    echo json_encode($list);
    $stmt->close();
} else {
    echo json_encode([]);
}

$conn->close();
?>