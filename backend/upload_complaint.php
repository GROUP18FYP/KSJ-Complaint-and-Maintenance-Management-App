<?php
include 'db.php'; 

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // 1. Get data from Android (Match your Java getParams keys exactly)
    $user_id             = isset($_POST['user_id']) ? $_POST['user_id'] : 0;
    $name                = isset($_POST['name']) ? $_POST['name'] : ""; 
    $phone               = isset($_POST['phone_number']) ? $_POST['phone_number'] : "";
    $date_prefer_service = isset($_POST['date_prefer_service']) ? $_POST['date_prefer_service'] : ""; // NEW
    $room_number         = isset($_POST['room_number']) ? $_POST['room_number'] : "";
    $type                = isset($_POST['complaint_type']) ? $_POST['complaint_type'] : "";
    $description         = isset($_POST['description']) ? $_POST['description'] : "";
    $priority            = isset($_POST['priority']) ? $_POST['priority'] : "medium";
    $encoded_img         = isset($_POST['image']) ? $_POST['image'] : ""; 

    // 2. Handle Image Upload
    $image_path = "";
    if (!empty($encoded_img)) {
        $upload_dir = "uploads/";
        if (!is_dir($upload_dir)) {
            mkdir($upload_dir, 0777, true);
        }
        $image_name = "IMG_" . time() . ".jpg";
        $image_path = $upload_dir . $image_name;
        
        // Save the decoded base64 string as an image file
        file_put_contents($image_path, base64_decode($encoded_img));
    }

    // 3. Database Insert
    // We include 'date_prefer_service' in the column list
    $sql = "INSERT INTO complaints (user_id, name, phone_number, date_prefer_service, room_number, description, complaint_type, status, priority, image_url) 
            VALUES (?, ?, ?, ?, ?, ?, ?, 'pending', ?, ?)";
    
    $stmt = $conn->prepare($sql);
    
    if ($stmt) {
        // "issssssss" = 1 integer (i), 8 strings (s)
        // Order: user_id, name, phone, date_prefer, room, desc, type, priority, image_path
        $stmt->bind_param("issssssss", 
            $user_id, 
            $name, 
            $phone, 
            $date_prefer_service, 
            $room_number, 
            $description, 
            $type, 
            $priority, 
            $image_path
        );

        if ($stmt->execute()) {
            echo "success";
        } else {
            echo "Database error: " . $stmt->error;
        }
        $stmt->close();
    } else {
        echo "Preparation error: " . $conn->error;
    }
}
$conn->close();
?>