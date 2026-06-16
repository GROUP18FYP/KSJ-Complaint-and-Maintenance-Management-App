<?php
include 'db.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user_id  = $_POST['user_id'] ?? null;
    $staff_id = $_POST['staff_id'] ?? null;
    $rating   = $_POST['rating'] ?? null;
    $message  = $_POST['message'] ?? null;

    if ($user_id && $staff_id && $rating && $message) {
        $sql = "INSERT INTO feedback (user_id, staff_id, rating, message) 
                VALUES ('$user_id', '$staff_id', '$rating', '$message')";

        if (mysqli_query($conn, $sql)) {
            echo "Success";
        } else {
            echo "SQL Error: " . mysqli_error($conn);
        }
    } else {
        echo "Missing data: ID=$user_id, Staff=$staff_id, Rating=$rating";
    }
} else {
    echo "Direct access not allowed.";
}
?>