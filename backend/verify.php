<?php
include 'db.php';

// Get data from Android
$email = $_POST['email'] ?? '';
$code = $_POST['code'] ?? '';

if (empty($email) || empty($code)) {
    echo "missing_data";
    exit;
}

try {
    // UPDATED: Use 'user_id' instead of 'id' to match your table structure
    $stmt = $conn->prepare("SELECT user_id FROM users WHERE email = ? AND verification_code = ?");
    $stmt->bind_param("ss", $email, $code);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        // Success - Update the verified status
        $update = $conn->prepare("UPDATE users SET is_verified = 1 WHERE email = ?");
        $update->bind_param("s", $email);
        
        if ($update->execute()) {
            echo "success"; 
        } else {
            echo "update_failed";
        }
    } else {
        echo "wrong_code";
    }
} catch (Exception $e) {
    // If it crashes, this will return a clean message instead of a Fatal Error
    echo "error: " . $e->getMessage();
}

$conn->close();
?>