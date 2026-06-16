<?php
include 'db.php';
$staff_id = $_POST['staff_id'];

// SQL for get_assigned_tasks.php
$sql = "SELECT c.*, s.fullname as staff_name, s.phone_number as staff_phone 
        FROM complaints c 
        LEFT JOIN staff s ON c.assigned_staff_id = s.user_id 
        WHERE c.assigned_staff_id = ? 
        ORDER BY c.created_at DESC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $staff_id);
$stmt->execute();
$result = $stmt->get_result();
$tasks = [];
while($row = $result->fetch_assoc()) $tasks[] = $row;
echo json_encode($tasks);
?>