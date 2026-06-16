<?php 
include 'db.php';
$id = $_POST['user_id'];
$sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $id);
$stmt->execute();
$res = $stmt->get_result();
$list = [];
while($row = $res->fetch_assoc()) $list[] = $row;
echo json_encode($list);
?>