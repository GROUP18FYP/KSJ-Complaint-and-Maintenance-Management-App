<?php
header('Content-Type: application/json');
ob_start();
include 'db.php';

error_reporting(0);
ini_set('display_errors', 0);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = strtolower(trim($_POST['email'] ?? '')); 
    $password = $_POST['password'] ?? '';

    // Step 1: Find the account in 'users' using 'name' column
    $stmt = $conn->prepare("SELECT user_id, password, role, name FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        if (password_verify($password, $row['password'])) {
            
            $role = strtolower($row['role']);
            $userId = $row['user_id'];
            $finalName = $row['name']; // Default name from users table

            // Step 2: If Staff, override with name from 'staff' table (column 'fullname')
            if ($role === 'staff') {
                $staffStmt = $conn->prepare("SELECT fullname FROM staff WHERE user_id = ?");
                if ($staffStmt) {
                    $staffStmt->bind_param("i", $userId);
                    $staffStmt->execute();
                    $staffRes = $staffStmt->get_result();
                    if ($staffRow = $staffRes->fetch_assoc()) {
                        $finalName = $staffRow['fullname'];
                    }
                    $staffStmt->close();
                }
            }

            echo json_encode([
                "status" => "success",
                "role" => $role,
                "user_id" => (string)$userId,
                "fullname" => $finalName
            ]);

        } else {
            echo json_encode(["status" => "error", "message" => "Incorrect password"]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "User not found"]);
    }
}
$conn->close();
ob_end_flush();
?>