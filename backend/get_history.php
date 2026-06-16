<?php
include 'db.php';

if (isset($_POST['user_id'])) {
    $userId = $_POST['user_id'];

    // Join complaints with users (u2) to get staff info based on assigned_staff_id
    $sql = "SELECT c.*, 
                   c.created_at AS date_submitted,
                   u2.name AS staff_name, 
                   u2.phone_number AS staff_phone
            FROM complaints c
            LEFT JOIN users u2 ON c.assigned_staff_id = u2.user_id
            WHERE c.user_id = ? 
            ORDER BY c.created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $result = $stmt->get_result();

    $history = array();
    while($row = $result->fetch_assoc()) {
        $history[] = $row;
    }

    header('Content-Type: application/json');
    echo json_encode($history);
    $stmt->close();
}
$conn->close();
?>