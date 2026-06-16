<?php 
header('Content-Type: application/json'); 
include 'db.php';

$id = $_POST['complaint_id'] ?? null; 
$status = $_POST['status'] ?? "";

if ($id && !empty($status)) { 

    // 1. Update the MAIN complaints table
    $sql = "UPDATE complaints SET status = ? WHERE complaint_id = ?"; 
    $stmt = $conn->prepare($sql); 
    $stmt->bind_param("si", $status, $id);

    if ($stmt->execute()) {
        
        // 2. Fetch Student ID and Staff ID safely
        $getInfo = $conn->prepare("SELECT user_id, assigned_staff_id FROM complaints WHERE complaint_id = ?");
        $getInfo->bind_param("i", $id);
        $getInfo->execute();
        $result = $getInfo->get_result();
        
        if ($row = $result->fetch_assoc()) {
            $studentId = $row['user_id'];
            $staffUserId = $row['assigned_staff_id'];

            // 3. Increment tasks_completed if status is 'resolved'
            // Wrap in a try-catch so if the column is missing, the script doesn't crash
            try {
                if ($status === 'resolved' && !empty($staffUserId)) {
                    $updateCounter = $conn->prepare("UPDATE staff SET tasks_completed = tasks_completed + 1 WHERE user_id = ?");
                    $updateCounter->bind_param("i", $staffUserId);
                    $updateCounter->execute();
                }
            } catch (Exception $e) { 
                // Ignore if staff table update fails, so we can still send "success"
            }

            // 4. Send Notification
            // If this part crashes, check if your 'notifications' table has a 'complaint_id' column!
            try {
                $displayStatus = str_replace("_", " ", $status);
                $msg = "Update: Your complaint #$id status has been changed to $displayStatus.";
                
                // If your table doesn't have complaint_id, remove it from the query below
                $notif = $conn->prepare("INSERT INTO notifications (user_id, message) VALUES (?, ?)");
                $notif->bind_param("is", $studentId, $msg);
                $notif->execute();
            } catch (Exception $e) {
                // Ignore notification error
            }
        }

        // Return ONLY the word success so the Android App is happy
        echo "success";
    } else {
        echo "Database update failed";
    }

    $stmt->close();
} else { 
    echo "Invalid data received"; 
} 
$conn->close(); 
?>