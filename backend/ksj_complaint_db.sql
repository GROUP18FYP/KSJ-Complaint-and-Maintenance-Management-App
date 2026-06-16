-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 16, 2026 at 09:27 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ksj_complaint_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `complaints`
--

CREATE TABLE `complaints` (
  `complaint_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `name` varchar(150) DEFAULT NULL,
  `phone_number` varchar(15) NOT NULL,
  `date_prefer_service` varchar(20) DEFAULT NULL,
  `room_number` varchar(20) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `complaint_type` varchar(100) DEFAULT NULL,
  `status` enum('pending','in_progress','resolved') DEFAULT 'pending',
  `priority` enum('low','medium','high') DEFAULT 'medium',
  `image_url` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `assigned_staff_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `complaints`
--

INSERT INTO `complaints` (`complaint_id`, `user_id`, `name`, `phone_number`, `date_prefer_service`, `room_number`, `description`, `complaint_type`, `status`, `priority`, `image_url`, `created_at`, `assigned_staff_id`) VALUES
(4, 12, 'Tariq', '0142233445', '2026-05-26', 'B2-72', 'I smelt burnt smell from my socket', 'Electrical', 'in_progress', 'high', 'uploads/IMG_1778645728.jpg', '2026-05-13 04:15:28', 29),
(5, 1, 'Nabihah', '0148760877', '2026-05-26', 'A2-72', 'the tap keeps leaking', 'Plumbing', 'resolved', 'medium', 'uploads/IMG_1778730203.jpg', '2026-05-14 03:43:23', 27),
(6, 24, 'filla', '0132589676', '2026-06-03', 'A2-75', 'the shower drain is blocked', 'Plumbing', 'resolved', 'high', 'uploads/IMG_1780085059.jpg', '2026-05-29 20:04:19', 28),
(7, 1, 'Nabihah', '0148760877', '2026-06-06', 'A2-72', 'saw someone suspicious at block A', 'Security', 'resolved', 'high', '', '2026-06-05 15:57:20', 29),
(8, 1, 'Nabihah', '0148760877/', '2026-06-06', 'A2-72', 'The court are so noisy', 'Noise', 'in_progress', 'low', '', '2026-06-05 16:29:37', 29),
(9, 24, 'sofea', '0132589676', '2026-06-22', 'A3-72', 'the construction makes too much noise', 'noise', 'resolved', 'low', 'uploads/img_1780854419_4479.jpg', '2026-06-07 17:46:59', 29),
(10, 24, 'sofea', '0132589676', '2026-06-22', 'A3-72', 'the construction makes too much noise', 'noise', 'in_progress', 'low', 'uploads/img_1780854423_7525.jpg', '2026-06-07 17:47:03', 29),
(11, 30, 'Hilmiyyah', '0148760877', '2026-06-12', 'A2-67', 'The lift at block A has been out of service since this morning and not responding', 'Facilities', 'in_progress', 'medium', 'uploads/IMG_1781205545.jpg', '2026-06-11 19:19:05', 27),
(12, 1, 'Nafisah Najwa', '0169627297', '2026-06-17', 'A2-64', 'the bathroom sink tap is leaking continously', 'Plumbing', 'resolved', 'medium', 'uploads/IMG_1781447330.jpg', '2026-06-14 14:28:50', 28),
(13, 30, 'miyya', '0122510877', '2026-06-14', 'A2-67', 'water leaking', 'Plumbing', 'in_progress', 'medium', 'uploads/IMG_1781448091.jpg', '2026-06-14 14:41:31', 28),
(14, 1, 'Nur Nabihah Mirza', '0123456789', '2026 - 6 - 15', 'A1 - 72', 'Theres a male in female block', 'security', 'resolved', 'medium', '', '2026-06-14 14:46:23', 29),
(15, 37, 'Luwman', '0122520877', '2026-06-15', 'A2-55', 'water leaking', 'Plumbing', 'pending', 'high', 'uploads/IMG_1781451329.jpg', '2026-06-14 15:35:29', 28),
(16, 1, 'Nabihah', '01225208766', '2026-06-17', 'A2-56', 'water leaking from my sink', 'Plumbing', 'resolved', 'medium', 'uploads/IMG_1781491452.jpg', '2026-06-15 02:44:12', 28),
(17, 1, 'Nabihah', '0148760877', '2026-06-18', 'A2-62', 'Water is leaking from my pipe', 'Plumbing', 'resolved', 'medium', 'uploads/IMG_1781493161.jpg', '2026-06-15 03:12:41', 28);

-- --------------------------------------------------------

--
-- Table structure for table `feedback`
--

CREATE TABLE `feedback` (
  `feedback_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL CHECK (`rating` between 1 and 5),
  `message` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `staff_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `feedback`
--

INSERT INTO `feedback` (`feedback_id`, `user_id`, `rating`, `message`, `created_at`, `staff_id`) VALUES
(6, 1, 4, 'love the fast service!!', '2026-06-05 07:24:29', 28),
(7, 29, 5, 'love the fast services!!', '2026-06-05 15:04:46', 28),
(12, 1, 5, 'Thank you for your service', '2026-06-05 15:23:20', 29),
(13, 1, 5, 'love the fast services', '2026-06-05 15:51:21', 27),
(14, 1, 5, 'excellent service and commitment', '2026-06-05 16:00:46', 29),
(15, 1, 4, 'terbaik', '2026-06-14 15:16:17', 27),
(16, 37, 5, 'tq', '2026-06-14 15:38:51', 28),
(17, 1, 5, 'Thank youu', '2026-06-15 02:56:19', 28),
(18, 1, 5, 'Tq for your service', '2026-06-15 03:20:40', 28);

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `notification_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `complaint_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`notification_id`, `user_id`, `complaint_id`, `message`, `is_read`, `created_at`) VALUES
(1, 24, 6, 'Your complaint #6 has been updated to: pending', 1, '2026-05-29 20:36:53'),
(2, 24, 6, 'Your complaint #6 has been updated to: pending', 1, '2026-05-29 20:37:19'),
(3, 24, 6, 'Your complaint #6 has been updated to: pending', 1, '2026-05-29 20:37:54'),
(4, 24, 6, 'Your complaint #6 has been updated to: pending', 1, '2026-05-29 20:47:37'),
(5, 24, 6, 'Your complaint #6 has been updated to: pending', 1, '2026-05-29 20:48:08'),
(6, 24, 6, 'Your complaint #6 has been updated to: pending', 1, '2026-05-29 20:49:53'),
(7, 24, 6, 'Your complaint #6 has been updated to: pending', 1, '2026-05-29 20:50:12'),
(8, 24, 6, 'Your complaint #6 has been updated to: pending', 1, '2026-05-30 00:15:45'),
(9, 24, 6, 'Update: Your complaint #6 status has been changed to In Progress.', 1, '2026-05-30 01:10:07'),
(10, 24, 6, 'Update: Your complaint #6 status has been changed to In Progress.', 1, '2026-05-30 01:10:23'),
(11, 24, 6, 'Your complaint #6 has been updated to: in progress', 1, '2026-05-30 01:12:43'),
(12, 24, 6, 'Your complaint #6 has been updated to: in progress', 1, '2026-05-30 01:49:18'),
(13, 24, 6, 'Your complaint #6 has been updated to: in progress', 1, '2026-05-30 02:09:05'),
(14, 24, 6, 'Your complaint #6 has been updated to: In Progress', 1, '2026-05-30 02:17:39'),
(15, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:18:10'),
(16, 24, 6, 'Your complaint #6 has been updated to: In Progress', 1, '2026-05-30 02:23:32'),
(17, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:23:34'),
(18, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:23:35'),
(19, 24, 6, 'Your complaint #6 has been updated to: In Progress', 1, '2026-05-30 02:23:45'),
(20, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:23:47'),
(21, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:23:48'),
(22, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:23:51'),
(23, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:23:53'),
(24, 24, 6, 'Your complaint #6 has been updated to: In Progress', 1, '2026-05-30 02:24:03'),
(25, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:24:06'),
(26, 24, 6, 'Your complaint #6 has been updated to: In Progress', 1, '2026-05-30 02:25:40'),
(27, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:25:41'),
(28, 24, 6, 'Your complaint #6 has been updated to: In Progress', 1, '2026-05-30 02:29:34'),
(29, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:29:42'),
(30, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:29:47'),
(31, 24, 6, 'Your complaint #6 has been updated to: Pending', 1, '2026-05-30 02:29:53'),
(32, 24, 6, 'Your complaint #6 has been updated to: In Progress', 1, '2026-05-30 02:30:11'),
(33, 24, 6, 'Your complaint #6 has been updated to: in progress', 1, '2026-05-30 02:36:31'),
(34, 1, 5, 'Your complaint #5 has been updated to: in progress', 1, '2026-05-30 02:38:01'),
(35, 1, 5, 'Your complaint #5 has been updated to: pending', 1, '2026-05-30 02:38:09'),
(36, 1, 5, 'Your complaint #5 has been updated to: completed', 1, '2026-05-30 02:38:13'),
(37, 24, 6, 'Complaint #6 update: Status changed to In Progress', 1, '2026-05-30 02:41:57'),
(38, 24, 6, 'Complaint #6 update: Status changed to In Progress', 1, '2026-05-30 02:42:09'),
(39, 1, 5, 'Complaint #5 update: Status changed to Pending', 1, '2026-05-30 02:43:15'),
(40, 1, 5, 'Complaint #5 update: Status changed to In Progress', 1, '2026-05-30 02:43:24'),
(41, 24, 6, 'Complaint #6 update: Status changed to Completed', 1, '2026-05-30 02:43:36'),
(42, 1, 5, 'Complaint #5 update: Status changed to Pending', 1, '2026-05-30 02:47:14'),
(43, 24, 6, 'Complaint #6 update: Status changed to Pending', 1, '2026-05-30 02:47:18'),
(44, 24, 6, 'Complaint #6 has been updated to In Progress.', 1, '2026-05-30 02:56:32'),
(45, 24, 6, 'Complaint #6 has been updated to In Progress.', 1, '2026-05-30 02:56:40'),
(46, 24, 6, 'Complaint #6 has been updated to Completed.', 1, '2026-05-30 02:57:02'),
(47, 24, 6, 'Complaint #6 has been updated to Pending.', 1, '2026-05-30 02:57:16'),
(48, 24, 6, 'Complaint #6 status has been updated to in progress.', 1, '2026-05-30 03:05:14'),
(49, 24, 6, 'Complaint #6 status has been updated to completed.', 1, '2026-05-30 03:05:25'),
(50, 24, 6, 'Update: Your complaint #6 status has been changed to completed.', 1, '2026-05-30 03:08:04'),
(51, 24, 6, 'Update: Your complaint #6 status has been changed to in progress.', 1, '2026-05-30 03:08:17'),
(52, 24, 6, 'Complaint #6 status has been updated to in progress.', 1, '2026-05-30 03:12:55'),
(53, 24, 6, 'Complaint #6 status has been updated to in progress.', 1, '2026-05-30 03:15:04'),
(54, 24, 6, 'Complaint #6 status has been updated to pending.', 1, '2026-05-30 03:15:12'),
(55, 24, 6, 'Update: Your complaint #6 is now Inprogress.', 1, '2026-05-30 03:20:18'),
(56, 24, 6, 'Update: Your complaint #6 is now Inprogress.', 1, '2026-05-30 03:20:18'),
(57, 24, 6, 'Update: Your complaint #6 is now Inprogress.', 1, '2026-05-30 03:21:04'),
(58, 24, 6, 'Update: Your complaint #6 is now In Progress.', 1, '2026-05-30 03:33:28'),
(59, 24, 6, 'Update: Your complaint #6 is now In Progress.', 1, '2026-05-30 03:33:36'),
(60, 24, 6, 'Update: Your complaint #6 is now In Progress.', 1, '2026-05-30 03:33:40'),
(61, 24, 6, 'Complaint #6 status updated to: In Progress', 1, '2026-05-30 03:42:30'),
(62, 24, 6, 'Complaint #6 status updated to: In Progress', 1, '2026-05-30 03:42:30'),
(63, 24, 6, 'Complaint #6 status updated to: In Progress', 1, '2026-05-30 03:42:35'),
(64, 24, 6, 'Update: Your complaint #6 status has been changed to resolved.', 1, '2026-05-30 03:44:35'),
(65, 12, 4, 'Complaint #4 status updated to: Pending', 0, '2026-06-05 15:40:25'),
(66, 12, 4, 'Complaint #4 status updated to: Pending', 0, '2026-06-05 15:40:31'),
(67, 1, 5, 'Complaint #5 status updated to: In Progress', 1, '2026-06-05 15:40:53'),
(68, 1, 5, 'Complaint #5 status updated to: Resolved', 1, '2026-06-05 15:50:52'),
(69, 1, 7, 'Complaint #7 status updated to: Pending', 1, '2026-06-05 15:58:14'),
(70, 1, 7, 'Complaint #7 status updated to: In Progress', 1, '2026-06-05 15:58:25'),
(71, 1, 7, 'Complaint #7 status updated to: Resolved', 1, '2026-06-05 15:58:47'),
(72, 1, 8, 'Complaint #8 status updated to: Pending', 1, '2026-06-05 16:30:25'),
(73, 1, 8, 'Complaint #8 status updated to: In Progress', 1, '2026-06-05 16:30:40'),
(74, 30, 11, 'Complaint #11 status updated to: Pending', 1, '2026-06-11 19:29:53'),
(75, 30, 11, 'Complaint #11 status updated to: Resolved', 0, '2026-06-11 19:57:22'),
(76, 30, 11, 'Complaint #11 status updated to: Resolved', 0, '2026-06-11 19:57:26'),
(77, 30, 11, 'Complaint #11 status updated to: Pending', 0, '2026-06-11 19:57:30'),
(78, 30, 11, 'Complaint #11 status updated to: Pending', 0, '2026-06-11 19:57:31'),
(79, 30, 11, 'Complaint #11 status updated to: Pending', 0, '2026-06-11 19:57:41'),
(80, 30, 11, 'Complaint #11 status updated to: Pending', 0, '2026-06-11 19:57:42'),
(81, 30, 11, 'Complaint #11 status updated to: Pending', 0, '2026-06-11 19:57:44'),
(82, 30, 11, 'Complaint #11 status updated to: Pending', 0, '2026-06-11 19:57:45'),
(83, 30, 11, 'Update: Your complaint #11 status has been changed to in_progress.', 0, '2026-06-11 19:58:48'),
(84, 1, 12, 'Complaint #12 status updated to: In Progress', 1, '2026-06-14 14:31:02'),
(85, 1, 12, 'Complaint #12 status updated to: Pending', 1, '2026-06-14 14:31:25'),
(86, 1, 12, 'Complaint #12 status updated to: In Progress', 1, '2026-06-14 14:31:42'),
(87, 1, 12, 'Complaint #12 status updated to: Pending', 1, '2026-06-14 14:31:50'),
(88, 1, 12, 'Complaint #12 status updated to: In Progress', 1, '2026-06-14 14:32:27'),
(89, 1, 12, 'Complaint #12 status updated to: In Progress', 1, '2026-06-14 14:33:24'),
(90, 1, 12, 'Complaint #12 status updated to: In Progress', 1, '2026-06-14 14:33:30'),
(91, 1, 12, 'Complaint #12 status updated to: In Progress', 0, '2026-06-14 14:34:42'),
(92, 1, 12, 'Complaint #12 status updated to: Pending', 0, '2026-06-14 14:35:35'),
(93, 1, 12, 'Complaint #12 status updated to: In Progress', 1, '2026-06-14 14:35:39'),
(94, 1, 12, 'Complaint #12 status updated to: In Progress', 0, '2026-06-14 14:36:22'),
(95, 1, 12, 'Complaint #12 status updated to: In Progress', 0, '2026-06-14 14:37:33'),
(96, 1, 12, 'Complaint #12 status updated to: In Progress', 1, '2026-06-14 14:37:41'),
(97, 30, 13, 'Complaint #13 status updated to: In Progress', 0, '2026-06-14 14:42:28'),
(98, 30, 13, 'Complaint #13 status updated to: In Progress', 0, '2026-06-14 14:42:32'),
(99, 30, 13, 'Complaint #13 status updated to: Pending', 0, '2026-06-14 14:43:44'),
(100, 1, 14, 'Complaint #14 status updated to: Pending', 0, '2026-06-14 14:47:06'),
(101, 24, 10, 'Complaint #10 status updated to: Pending', 0, '2026-06-14 14:59:02'),
(102, 24, 9, 'Complaint #9 status updated to: Pending', 0, '2026-06-14 14:59:50'),
(103, 30, 0, 'Update: Your complaint #13 status has been changed to resolved.', 0, '2026-06-14 15:11:06'),
(104, 30, 0, 'Update: Your complaint #13 status has been changed to in progress.', 0, '2026-06-14 15:11:24'),
(105, 24, 10, 'Complaint #10 status updated to: In Progress', 0, '2026-06-14 15:19:26'),
(106, 24, 10, 'Complaint #10 status updated to: Pending', 0, '2026-06-14 15:20:12'),
(107, 24, 10, 'Complaint #10 status updated to: In Progress', 0, '2026-06-14 15:20:55'),
(108, 24, 0, 'Update: Your complaint #9 status has been changed to resolved.', 0, '2026-06-14 15:25:23'),
(109, 37, 15, 'Complaint #15 status updated to: Pending', 0, '2026-06-14 15:36:24'),
(110, 37, 15, 'Complaint #15 status updated to: Pending', 0, '2026-06-14 15:36:32'),
(111, 37, 15, 'Complaint #15 status updated to: Pending', 0, '2026-06-14 15:36:43'),
(112, 37, 0, 'Update: Your complaint #15 status has been changed to resolved.', 0, '2026-06-14 15:38:05'),
(113, 37, 15, 'Complaint #15 status updated to: In Progress', 0, '2026-06-15 00:24:34'),
(114, 37, 15, 'Complaint #15 status updated to: Pending', 0, '2026-06-15 00:24:58'),
(115, 37, 15, 'Complaint #15 status updated to: Pending', 0, '2026-06-15 00:35:02'),
(116, 24, 6, 'Complaint #6 status updated to: Resolved', 0, '2026-06-15 00:39:51'),
(117, 1, 12, 'Complaint #12 status updated to: Resolved', 0, '2026-06-15 00:40:03'),
(118, 1, 16, 'Complaint #16 status updated to: Pending', 0, '2026-06-15 02:45:29'),
(119, 1, 16, 'Complaint #16 status updated to: Pending', 0, '2026-06-15 02:45:48'),
(120, 1, 0, 'Update: Your complaint #16 status has been changed to in progress.', 0, '2026-06-15 02:55:16'),
(121, 1, 0, 'Update: Your complaint #16 status has been changed to resolved.', 0, '2026-06-15 02:55:44'),
(122, 1, 17, 'Complaint #17 status updated to: Pending', 0, '2026-06-15 03:13:39'),
(123, 1, 17, 'Complaint #17 status updated to: Pending', 0, '2026-06-15 03:13:55'),
(124, 1, 0, 'Update: Your complaint #17 status has been changed to in progress.', 0, '2026-06-15 03:17:19'),
(125, 1, 0, 'Update: Your complaint #17 status has been changed to resolved.', 0, '2026-06-15 03:17:51');

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `staff_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `fullname` varchar(100) NOT NULL,
  `staff_role` varchar(50) NOT NULL,
  `phone_number` varchar(15) DEFAULT NULL,
  `tasks_completed` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`staff_id`, `user_id`, `fullname`, `staff_role`, `phone_number`, `tasks_completed`) VALUES
(1, 27, 'anastasia', 'Technician', '0132589676', 1),
(6, 28, 'fazrul', 'Plumber', '01156626896', 5),
(8, 29, 'Joshua', 'Security', '0148760877', 2),
(9, 31, 'Jay', 'Janitor', '0123456789', 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('user','staff','admin') DEFAULT 'user',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_verified` int(1) DEFAULT 0,
  `verification_code` varchar(10) DEFAULT NULL,
  `phone_number` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `email`, `password`, `role`, `created_at`, `is_verified`, `verification_code`, `phone_number`) VALUES
(1, 'Nur Nabihah Mirza', 'nurnabihahmirza@graduate.utm.my', '$2y$10$gQehf9iGlhGW2FxhlyKPaOLpr7G6IFcwci7ueL2ZaG3teFlJujseG', 'user', '2026-05-12 10:29:55', 1, NULL, '0148760877'),
(2, 'admin', 'admingroup18fyp@gmail.com', '$argon2id$v=19$m=19456,t=2,p=1$QKounf17zWnWfiWmu+zd5g$4VYJA5B9mbIBY3nv1NXPlc8jtKpPCkQJhUoYKFe8pPc', 'admin', '2026-05-14 02:23:00', 1, NULL, NULL),
(12, 'Tariq Huda', 'muhammadtariqhuda@graduate.utm.my', '$2y$10$fAEJB8gz5O/ydjxtH9GoHO0yoUQWoZ9ByCTpS3yV88yrXa37Z3AQm', 'user', '2026-05-13 04:19:43', 1, '661053', '0142233445'),
(24, 'Sofea', 'nurfillasofea@graduate.utm.my', '$2y$10$Fr4ekYZewAblAV476cDyseqnZIda82UDeF9VUkCPm6hKjnMZc1KmK', 'user', '2026-05-14 01:30:15', 1, NULL, '0132589676'),
(27, 'anastasia', 'sofeaaazlann@gmail.com', '$2y$10$5y2mDz6qP1orv1HIsbW3jeluzKD0Sof0BzoNmkEosiU/NV20QmyEC', 'staff', '2026-05-29 20:06:49', 0, NULL, '0132589676'),
(28, 'fazrul', 'outxcmo@gmail.com', '$2y$10$Nwd6tfN6fNTKtgeog48mnuy48hT51PvjhJ/GLt2FeNmdyr1eYbjLW', 'staff', '2026-05-29 23:58:03', 0, NULL, '01156626896'),
(29, 'Joshua', 'miisunoya@gmail.com', '$2y$10$VoKHb2.lD6Fvb0Y5BqzbeOgerZWt9n.9yX4HHZ8MDhwL1IxHu6hdK', 'staff', '2026-05-30 00:07:18', 0, NULL, '0148760877'),
(30, 'Hilmiyyah', 'hilmiyyah@graduate.utm.my', '$2y$10$NOspAdnzcHs1RzuaSizmbuLkPqt9sYrH5.WvBAmrvYI7ebNYWO0w2', 'user', '2026-06-11 19:12:18', 1, '934875', '0122510877'),
(31, 'Jay', 'jay@gmail.com', '$2y$10$Ig6NElejucF93Zof6haB/O3O2ELJsbmnwW7Bz5c453tnCcn0Qmg3W', 'staff', '2026-06-13 15:36:56', 0, NULL, '0123456789'),
(37, 'Luqman Kamil', 'luqmankamil@graduate.utm.my', '$2y$10$.sGAOKXC8FliAQ.v5rHoEe2JXx5sgXrRulTTP26OWttpknDTGTEQ.', 'user', '2026-06-14 15:33:30', 1, '778550', '0122520827'),
(39, 'Nafisah', 'nafisahnajwa@graduate.utm.my', '$2y$10$QqmMekJ5XsIXS.gEx.Dfv.SNJzTUjA/BmQnykQA0fhCBzxXtXgU.q', 'user', '2026-06-15 03:09:59', 0, '278839', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `complaints`
--
ALTER TABLE `complaints`
  ADD PRIMARY KEY (`complaint_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`feedback_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `complaint_id` (`complaint_id`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`staff_id`),
  ADD UNIQUE KEY `user_id_2` (`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `complaints`
--
ALTER TABLE `complaints`
  MODIFY `complaint_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `feedback`
--
ALTER TABLE `feedback`
  MODIFY `feedback_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=126;

--
-- AUTO_INCREMENT for table `staff`
--
ALTER TABLE `staff`
  MODIFY `staff_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
