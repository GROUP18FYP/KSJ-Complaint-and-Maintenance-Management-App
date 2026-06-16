<?php
// Set headers for JSON response and clear any previous output buffers
ob_start();
header('Content-Type: application/json');

// 1. Include Database Connection
include 'db.php'; 

// 2. Import PHPMailer classes (Ensure these files are in C:/xampp/htdocs/ksj_api/PHPMailer/)
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'PHPMailer/Exception.php';
require 'PHPMailer/PHPMailer.php';
require 'PHPMailer/SMTP.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $fullName = $_POST['fullname'] ?? ''; 
    $email = $_POST['email'] ?? '';
    $password = $_POST['password'] ?? '';
    $role = $_POST['role'] ?? 'user';

    // Basic Validation
    if (empty($fullName) || empty($email) || empty($password)) {
        echo json_encode(["status" => "error", "message" => "All fields are required"]);
        exit;
    }

    // --- AUTO-CLEANUP LOGIC ---
    // Check if the email already exists in the database
    $check = $conn->prepare("SELECT user_id, is_verified FROM users WHERE email = ?");
    $check->bind_param("s", $email);
    $check->execute();
    $result = $check->get_result();

    if ($row = $result->fetch_assoc()) {
        if ($row['is_verified'] == 1) {
            // If the user is already verified, they cannot register again
            echo json_encode(["status" => "error", "message" => "Email already verified. Please Login."]);
            exit;
        } else {
            // If the email exists but is NOT verified, delete the old record
            // so we can start fresh with a new name, password, and OTP.
            $deleteOld = $conn->prepare("DELETE FROM users WHERE email = ? AND is_verified = 0");
            $deleteOld->bind_param("s", $email);
            $deleteOld->execute();
        }
    }

    // --- USER REGISTRATION ---
    // Securely hash the password
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);
    // Generate a 6-digit OTP
    $otp = rand(100000, 999999);

    // Insert user (matches your DB column names: name, email, password, role, verification_code)
    $stmt = $conn->prepare("INSERT INTO users (name, email, password, role, verification_code, is_verified) VALUES (?, ?, ?, ?, ?, 0)");
    $stmt->bind_param("sssss", $fullName, $email, $hashed_password, $role, $otp);

    if ($stmt->execute()) {
        
        // --- SEND VERIFICATION EMAIL ---
        $mail = new PHPMailer(true);
        try {
            // Server settings
            $mail->isSMTP();
            $mail->Host       = 'smtp.gmail.com';
            $mail->SMTPAuth   = true;
            $mail->Username   = 'admingroup18fyp@gmail.com'; 
            $mail->Password   = 'jwkn vuhr uzkp sfcp'; // Replace with your Google App Password
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
            $mail->Port       = 587;

            // Recipients
            $mail->setFrom('admingroup18fyp@gmail.com', 'KSJ Maintenance');
            $mail->addAddress($email);

            // Content
            $mail->isHTML(true);
            $mail->Subject = 'Your Verification Code';
            $mail->Body    = "Hello <b>$fullName</b>,<br><br>Your verification code for KSJ Complaint System is: <b>$otp</b>";

            $mail->send();
            
            // Return success to Android
            echo json_encode([
                "status" => "success", 
                "message" => "Registration successful. Please check your email.",
                "otp" => $otp // Optional: return for debugging purposes
            ]);

        } catch (Exception $e) {
            // If email fails, the user is still saved in the DB, so we return the code for testing
            echo json_encode([
                "status" => "success", 
                "message" => "User saved, but email failed. Code is: $otp",
                "otp" => $otp
            ]);
        }

    } else {
        echo json_encode(["status" => "error", "message" => "Database error: " . $conn->error]);
    }

} else {
    echo json_encode(["status" => "error", "message" => "Invalid Request Method"]);
}

$conn->close();
ob_end_flush();
?>