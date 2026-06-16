<?php
include 'db.php';

// Get the user ID from the Android app
$id = $_POST['user_id'] ?? null;

if ($id) {
    // This query deletes the user from the master users table
    $sql = "DELETE FROM users WHERE user_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $id);

    if($stmt->execute()){
        echo "success";
    } else {
        echo "error";
    }
    $stmt->close();
} else {
    echo "No ID provided";
}

$conn->close();
?>