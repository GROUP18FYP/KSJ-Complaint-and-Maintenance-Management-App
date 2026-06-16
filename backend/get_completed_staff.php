<?php
include 'db.php';// 1. Get student_id from the Android GET request
$student_id = isset($_GET['student_id']) ? $_GET['student_id'] : '';

if (empty($student_id)) {
    echo json_encode([]);
    exit;
}

// 2. Query using your actual column names from the screenshot:
// - c.user_id is the student
// - c.assigned_staff_id is the staff
// - c.status = 'resolved' is the completed status
$sql = "SELECT DISTINCT u.user_id, u.name 
        FROM complaints c 
        JOIN users u ON c.assigned_staff_id = u.user_id 
        WHERE c.user_id = '$student_id' AND c.status = 'resolved'";

$result = mysqli_query($conn, $sql);

if (!$result) {
    // This helps debug if the SQL itself fails
    die(json_encode(["error" => mysqli_error($conn)]));
}

$staffList = array();
while($row = mysqli_fetch_assoc($result)) {
    $staffList[] = $row;
}

echo json_encode($staffList);
?>