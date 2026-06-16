<?php
header('Content-Type: application/json');
include 'db.php';

// Use $_REQUEST for testing in browser or POST for the App
$id = isset($_REQUEST['complaint_id']) ? $_REQUEST['complaint_id'] : null;

if ($id) {
    // JOIN 1: users (u) to get the student's name
    // JOIN 2: staff (s) to get the worker's details (linked via assigned_staff_id)
    $sql = "SELECT c.*, 
                   u.name as student_name, 
                   s.fullname as staff_name, 
                   s.staff_role as staff_role, 
                   s.phone_number as staff_phone 
            FROM complaints c 
            JOIN users u ON c.user_id = u.user_id 
            LEFT JOIN staff s ON c.assigned_staff_id = s.user_id 
            WHERE c.complaint_id = ?";
            
    $stmt = $conn->prepare($sql);
    
    if ($stmt) {
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($row = $result->fetch_assoc()) {
            echo json_encode(["status" => "success", "data" => $row]);
        } else {
            echo json_encode(["status" => "error", "message" => "Complaint ID $id not found"]);
        }
        $stmt->close();
    } else {
        echo json_encode(["status" => "error", "message" => "SQL Error: " . $conn->error]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "No complaint_id received by server"]);
}

$conn->close();
?>