<?php
include 'db.php';

// Suppress errors to ensure clean text output
error_reporting(0);
ini_set('display_errors', 0);

// 1. Retrieve data from Android
$userId    = $_POST['user_id'] ?? null;
$name      = $_POST['fullname'] ?? "";   // User's name
$role      = $_POST['role'] ?? "";       // Account role (admin/staff/user)
$phone     = $_POST['phone_number'] ?? "";
$staff_job = $_POST['staff_role'] ?? ""; // Job title from the Spinner (Technician, etc.)

if (!$userId) {
    echo "error: Missing User ID";
    exit();
}

// 2. Update the Master 'users' table
// Uses column 'name' for students/admins/staff accounts
$stmt = $conn->prepare("UPDATE users SET role = ?, name = ?, phone_number = ? WHERE user_id = ?");

if (!$stmt) {
    echo "error: SQL Prepare failed: " . $conn->error;
    exit();
}

$stmt->bind_param("sssi", $role, $name, $phone, $userId);

if ($stmt->execute()) {
    // 3. If the user is staff, update/create their specific job profile
    if ($role === 'staff') {
        // Staff table uses 'fullname' for the name and 'staff_role' for the job title
        $sqlStaff = "INSERT INTO staff (user_id, fullname, phone_number, staff_role) 
                     VALUES (?, ?, ?, ?) 
                     ON DUPLICATE KEY UPDATE fullname = ?, phone_number = ?, staff_role = ?";
        
        $stmt2 = $conn->prepare($sqlStaff);
        if ($stmt2) {
            // Mapping: we use $staff_job (from Spinner) for the job role columns
            $stmt2->bind_param("issssss", $userId, $name, $phone, $staff_job, $name, $phone, $staff_job);
            $stmt2->execute();
            $stmt2->close();
        }
    }
    
    echo "success";
} else {
    echo "error: Execution failed";
}

$stmt->close();
$conn->close();
?>