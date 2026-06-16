<?php
header('Content-Type: text/plain');
include 'db.php';

// Enable error reporting for debugging - this will show the error in Logcat
error_reporting(E_ALL);
ini_set('display_errors', 1);

$id       = $_POST['complaint_id'] ?? null;
$priority = $_POST['priority'] ?? "low"; 
$status   = $_POST['status'] ?? "pending"; 
$staff_id = $_POST['assigned_staff_id'] ?? "0";

if (empty($id)) {
    echo "error: missing complaint id";
    exit();
}

// Ensure $id and $staff_id are integers
$id = (int)$id;
$staff_val = ($staff_id == "0" || empty($staff_id)) ? 0 : (int)$staff_id;

// 1. UPDATE COMPLAINT
$sql = "UPDATE complaints SET priority = ?, status = ?, assigned_staff_id = ? WHERE complaint_id = ?";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo "Prepare failed: " . $conn->error;
    exit();
}

// If your DB allows NULL for assigned_staff_id, you can use null, 
// otherwise use $staff_val (0). I'll use $staff_val for safety.
$stmt->bind_param("ssii", $priority, $status, $staff_val, $id);

if ($stmt->execute()) {
    // 2. GET STUDENT ID (Using bind_result for maximum compatibility)
    // IMPORTANT: Check if the column is 'user_id' or 'student_id' in your database
    $getUser = $conn->prepare("SELECT user_id FROM complaints WHERE complaint_id = ?");
    $getUser->bind_param("i", $id);
    $getUser->execute();
    $getUser->bind_result($studentId);
    
    if ($getUser->fetch()) {
        $getUser->close(); // Close this before opening the next one
        
        $displayStatus = str_replace('_', ' ', $status);
        $msg = "Complaint #$id status updated to: " . ucwords($displayStatus);
        
        // 3. INSERT NOTIFICATION
        $notif = $conn->prepare("INSERT INTO notifications (user_id, complaint_id, message) VALUES (?, ?, ?)");
        if ($notif) {
            $notif->bind_param("iis", $studentId, $id, $msg);
            $notif->execute();
            $notif->close();
        }
    }
    echo "success";
} else {
    echo "error: " . $stmt->error;
}

$conn->close();
?>