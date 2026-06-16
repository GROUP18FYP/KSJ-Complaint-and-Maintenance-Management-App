<?php
// Database configuration
include 'db.php'; 

$history = array();

// 1. Determine the SQL Query
// We use LEFT JOIN to get staff details even if no staff is assigned yet
$base_sql = "SELECT c.*, 
                    u.name AS staff_name, 
                    u.phone_number AS staff_phone 
             FROM complaints c 
             LEFT JOIN users u ON c.assigned_staff_id = u.user_id";

// 2. Check if filtering by a specific student (user_id)
if (isset($_POST['user_id']) && !empty($_POST['user_id'])) {
    $userId = $_POST['user_id'];
    $sql = $base_sql . " WHERE c.user_id = ? ORDER BY c.created_at DESC";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $result = $stmt->get_result();
} else {
    // Admin view: Fetch ALL complaints for all students
    $sql = $base_sql . " ORDER BY c.created_at DESC";
    $result = $conn->query($sql);
}

// 3. Fetch results
if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        // Ensure keys match what your Java code expects
        $history[] = $row;
    }
}

// 4. Return as JSON
header('Content-Type: application/json');
echo json_encode($history);

if (isset($stmt)) {
    $stmt->close();
}
$conn->close();
?>