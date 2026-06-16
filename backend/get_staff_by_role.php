<?php
include 'db.php';

// Get the selected role from Android (Technician, Janitor, etc.)
$role = $_POST['role'] ?? "";

if (!empty($role)) {
    // We select from the 'staff' table where names live
    $sql = "SELECT user_id, fullname FROM staff WHERE staff_role = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $role);
    $stmt->execute();
    $result = $stmt->get_result();

    $staff_list = array();
    while($row = $result->fetch_assoc()) {
        $staff_list[] = $row;
    }

    echo json_encode($staff_list);
    $stmt->close();
} else {
    echo json_encode([]);
}

$conn->close();
?>