<?php 
header('Content-Type: application/json'); 
error_reporting(0); 
ini_set('display_errors', 0); 
include 'db.php';

$userId = $_POST['user_id'] ?? 0;
$sql = "SELECT u.user_id, u.email, u.role, u.name, u.phone_number as u_phone, s.fullname, s.phone_number as s_phone, s.staff_role, (SELECT COUNT(*) FROM complaints WHERE assigned_staff_id = u.user_id AND status = 'Completed') AS tasks_done FROM users u LEFT JOIN staff s ON u.user_id = s.user_id WHERE u.user_id = ?";
$stmt = $conn->prepare($sql);

if ($stmt) { $stmt->bind_param("i", $userId); 
$stmt->execute(); $result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    $displayName = !empty($row['fullname']) ? $row['fullname'] : ($row['name'] ?? "User");
    $displayPhone = !empty($row['s_phone']) ? $row['s_phone'] : ($row['u_phone'] ?? "N/A");

    $data = [
        "fullname" => $displayName,
        "phone_number" => $displayPhone,
        "role" => $row['role'],
        "staff_role" => $row['staff_role'] ?? "", // FIXED: Added this missing line
        "tasks_done" => (string)($row['tasks_done'] ?? 0)
    ];
    echo json_encode(["status" => "success", "data" => $data]);
} else {
    echo json_encode(["status" => "error", "message" => "User not found"]);
}
$stmt->close();
} else { echo json_encode(["status" => "error", "message" => "Query Error"]); 
} $conn->close(); 
?>