<?php
include 'db.php';
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'PHPMailer/Exception.php';
require 'PHPMailer/PHPMailer.php';
require 'PHPMailer/SMTP.php';

header('Content-Type: application/json');

$email = $_POST['email'] ?? '';

if (empty($email)) {
    echo json_encode(["status" => "error", "message" => "Email is required"]);
    exit;
}

// 1. Check if user exists
$stmt = $conn->prepare("SELECT user_id FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows > 0) {
    $otp = rand(100000, 999999);

    // 2. Update OTP in database
    $update = $conn->prepare("UPDATE users SET verification_code = ? WHERE email = ?");
    $update->bind_param("ss", $otp, $email);
    
    if ($update->execute()) {
        // 3. Send Email
        $mail = new PHPMailer(true);
        try {
            $mail->isSMTP();
            $mail->Host       = 'smtp.gmail.com'; 
            $mail->SMTPAuth   = true;
            $mail->Username   = 'admingroup18fyp@gmail.com'; 
            $mail->Password   = 'jwkn vuhr uzkp sfcp';           
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
            $mail->Port       = 587;

            $mail->setFrom('YOUR_SYSTEM_EMAIL@gmail.com', 'KSJ System');
            $mail->addAddress($email); 
            $mail->isHTML(true);
            $mail->Subject = 'Your Password Reset OTP';
            $mail->Body    = "Your reset code is: <h1 style='color:#76002E;'>$otp</h1>";

            $mail->send();
            echo json_encode(["status" => "success", "message" => "OTP sent successfully"]);
        } catch (Exception $e) {
            echo json_encode(["status" => "error", "message" => "Mail Error: {$mail->ErrorInfo}"]);
        }
    }
} else {
    echo json_encode(["status" => "error", "message" => "Email not found"]);
}
?>