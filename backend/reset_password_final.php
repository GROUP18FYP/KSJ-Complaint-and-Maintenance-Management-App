<?php
include 'db.php';

header('Content-Type: application/json');

$email = $_POST['email'] ?? '';
$new_password = $_POST['password'] ?? '';

if (empty($email) || empty($new_password)) {
    echo json_encode(["status" => "error", "message" => "Missing data"]);
    exit;
}

// 1. Hash the new password exactly like you do in register.php
$hashed_password = password_hash($new_password, PASSWORD_DEFAULT);

// 2. Update the users table
// NOTE: Make sure your column name is 'password'. Change it if it's different (e.g., 'user_pass')
$stmt = $conn->prepare("UPDATE users SET password = ? WHERE email = ?");
$stmt->bind_param("ss", $hashed_password, $email);

if ($stmt->execute()) {
    // 3. Optional: Clear the verification code so it can't be reused
    $conn->query("UPDATE users SET verification_code = NULL WHERE email = '$email'");
    
    echo json_encode(["status" => "success", "message" => "Password updated"]);
} else {
    echo json_encode(["status" => "error", "message" => "Database update failed"]);
}

$conn->close();
?>