<?php 
header('Content-Type: application/json'); 
include 'db.php';

// Check if user_id is provided if (isset($_POST['user_id'])) { $id = $_POST['user_id'];
// Count only UNREAD (is_read = 0) notifications for this user
$sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = 0";
$stmt = $conn->prepare($sql);

if ($stmt) {
    $stmt->bind_param("i", $id);
    $stmt->execute();
    $result = $stmt->get_result()->fetch_assoc();
    
    // Output clean JSON: {"count": 5}
    echo json_encode($result);
    $stmt->close();
} else {
    echo json_encode(["count" => 0, "error" => "SQL Error"]);
} else { echo json_encode(["count" => 0, "error" => "No User ID"]); 
}
$conn->close(); 
?>