<?php
header('Content-Type: application/json');
include 'db.php'; 

$mode = isset($_GET['mode']) ? $_GET['mode'] : 'monthly';

if ($mode == 'analyze') {
    // --- MODE: YEAR-TO-DATE HISTORICAL TREND ---
    $currentYear = date('Y');
    $sql = "SELECT 
                DATE_FORMAT(created_at, '%b\\n%Y') as lbl,
                DATE_FORMAT(created_at, '%Y-%m') as ym,
                COUNT(*) as total,
                SUM(CASE WHEN status IN ('in progress', 'in_progress') THEN 1 ELSE 0 END) as open,
                SUM(CASE WHEN status IN ('pending') THEN 1 ELSE 0 END) as pending,
                SUM(CASE WHEN status IN ('resolved', 'completed') THEN 1 ELSE 0 END) as resolved
            FROM complaints 
            WHERE YEAR(created_at) = '$currentYear'
            GROUP BY ym
            ORDER BY ym ASC";
    
    $res = mysqli_query($conn, $sql);
    $trend_total = []; $trend_open = []; $trend_pending = []; $trend_resolved = [];

    while($r = mysqli_fetch_assoc($res)) {
        $trend_total[] = array("label" => $r['lbl'], "count" => (int)$r['total']);
        $trend_open[] = array("label" => $r['lbl'], "count" => (int)$r['open']);
        $trend_pending[] = array("label" => $r['lbl'], "count" => (int)$r['pending']);
        $trend_resolved[] = array("label" => $r['lbl'], "count" => (int)$r['resolved']);
    }

    echo json_encode([
        "trend_total" => $trend_total,
        "trend_open" => $trend_open,
        "trend_pending" => $trend_pending,
        "trend_resolved" => $trend_resolved
    ]);
    exit;
}

// --- MODE: SELECTED MONTH (Task #171 & #172 Optimized) ---
$month = isset($_GET['month']) ? $_GET['month'] : date('m');
$year = isset($_GET['year']) ? $_GET['year'] : date('Y');

// 1. Get Totals
$sum_sql = "SELECT 
    COUNT(*) as total,
    SUM(CASE WHEN status IN ('resolved', 'completed') THEN 1 ELSE 0 END) as res,
    SUM(CASE WHEN status IN ('in progress', 'in_progress') THEN 1 ELSE 0 END) as open,
    SUM(CASE WHEN status IN ('pending') THEN 1 ELSE 0 END) as pend
    FROM complaints WHERE MONTH(created_at) = '$month' AND YEAR(created_at) = '$year'";
$summary = mysqli_fetch_assoc(mysqli_query($conn, $sum_sql));

// 2. Get Daily Trends (Optimized to 1 Query instead of 3)
$daily_sql = "SELECT 
    DAY(created_at) as day,
    COUNT(*) as total,
    SUM(CASE WHEN status IN ('in progress', 'in_progress') THEN 1 ELSE 0 END) as open,
    SUM(CASE WHEN status IN ('pending') THEN 1 ELSE 0 END) as pending,
    SUM(CASE WHEN status IN ('resolved', 'completed') THEN 1 ELSE 0 END) as resolved
    FROM complaints 
    WHERE MONTH(created_at) = '$month' AND YEAR(created_at) = '$year'
    GROUP BY day ORDER BY day ASC";

$daily_res = mysqli_query($conn, $daily_sql);
$t_total = []; $t_open = []; $t_pending = []; $t_resolved = [];

while($r = mysqli_fetch_assoc($daily_res)) {
    $d = (int)$r['day'];
    $t_total[] = ["day" => $d, "count" => (int)$r['total']];
    $t_open[] = ["day" => $d, "count" => (int)$r['open']];
    $t_pending[] = ["day" => $d, "count" => (int)$r['pending']];
    $t_resolved[] = ["day" => $d, "count" => (int)$r['resolved']];
}

echo json_encode([
    "total" => (string)($summary['total'] ?? 0),
    "resolved_rate" => ($summary['total'] > 0) ? round(($summary['res'] / $summary['total']) * 100) . "%" : "0%",
    "open" => (string)($summary['open'] ?? 0),
    "overdue" => (string)($summary['pend'] ?? 0),
    "trend_total" => $t_total,
    "trend_open" => $t_open,
    "trend_pending" => $t_pending,
    "trend_resolved" => $t_resolved
]);
?>