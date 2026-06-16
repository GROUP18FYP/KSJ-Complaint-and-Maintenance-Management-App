<?php 
header('Content-Type: application/json');
include 'db.php';

// 1. Get and CLEAN the data (Remove hidden spaces and force lowercase email)
$fullname_input = trim($_POST['fullname'] ?? "");      
$email          = strtolower(trim($_POST['email'] ?? "")); 
$password       = $_POST['password'] ?? "";      
$phone          = trim($_POST['phone_number'] ?? "");  
$role           = strtolower(trim($_POST['role'] ?? "user")); 
$staff_job      = trim($_POST['staff_role'] ?? "");    

if (empty($fullname_input) || empty($email) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "Missing fields"]);
    exit();
}

$hashed_password = password_hash($password, PASSWORD_DEFAULT);

// 2. Check if email exists
$check = $conn->prepare("SELECT user_id FROM users WHERE email = ?"); 
$check->bind_param("s", $email); 
$check->execute(); 
if($check->get_result()->num_rows > 0) { 
    echo json_encode(["status" => "error", "message" => "Email already registered"]);
    exit();
}

// 3. INSERT INTO USERS table (Uses column 'name')
$sql_user = "INSERT INTO users (email, password, role, name, phone_number) VALUES (?, ?, ?, ?, ?)";
$stmt_user = $conn->prepare($sql_user);
$stmt_user->bind_param("sssss", $email, $hashed_password, $role, $fullname_input, $phone);

if ($stmt_user->execute()) { 
    $new_user_id = $conn->insert_id;

    // 4. INSERT INTO STAFF table (Uses column 'fullname')
    if ($role === 'staff') {
        $sql_staff = "INSERT INTO staff (user_id, fullname, staff_role, phone_number) VALUES (?, ?, ?, ?)";
        $stmt_staff = $conn->prepare($sql_staff);
        if ($stmt_staff) {
            $stmt_staff->bind_param("isss", $new_user_id, $fullname_input, $staff_job, $phone);
            $stmt_staff->execute();
        }
    }
    echo "success"; 
} else { 
    echo json_encode(["status" => "error", "message" => $stmt_user->error]);
}
$conn->close(); 
?>