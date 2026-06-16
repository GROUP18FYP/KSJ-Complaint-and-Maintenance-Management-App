<?php
header('Content-Type: application/json');
include 'db.php'; 

// Get staff_id if provided (for Staff view)
$staff_id = isset($_POST['staff_id']) ? mysqli_real_escape_string($conn, $_POST['staff_id']) : '';

if (!empty($staff_id)) {
    // STAFF MODE: Get feedback for specific staff, JOIN to get their name
    $sql = "SELECT f.rating, f.message, f.created_at, u.name AS staff_name, u_std.name AS student_name
            FROM feedback f
            LEFT JOIN users u ON f.staff_id = u.user_id
            LEFT JOIN users u_std ON f.user_id = u_std.user_id
            WHERE f.staff_id = '$staff_id' 
            ORDER BY f.created_at DESC";
} else {
    // ADMIN MODE: Get ALL feedback, JOIN twice (once for staff name, once for student name)
    $sql = "SELECT f.rating, f.message, f.created_at, u.name AS staff_name, u_std.name AS student_name
            FROM feedback f
            LEFT JOIN users u ON f.staff_id = u.user_id
            LEFT JOIN users u_std ON f.user_id = u_std.user_id
            ORDER BY f.created_at DESC";
}

$result = mysqli_query($conn, $sql);
$feedbackList = array();

if ($result) {
    while($row = mysqli_fetch_assoc($result)) {
        $feedbackList[] = array(
            // Use student_name if exists, otherwise "Anonymous Student"
            "name" => $row['student_name'] ? $row['student_name'] : "Anonymous Student",
            "rating" => (int)$row['rating'],
            "message" => $row['message'],
            "date" => $row['created_at'],
            // Add the staff name so the Android app can see it
            "staff_name" => $row['staff_name'] ? $row['staff_name'] : "General"
        );
    }
}

echo json_encode($feedbackList);
?>