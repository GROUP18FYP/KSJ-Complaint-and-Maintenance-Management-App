# KSJ Complaint and Maintenance System

An Android application for managing complaints and maintenance tasks, featuring a PHP backend.

## Project Structure
- **/app**: Contains the Android Studio project (Java).
- **/backend**: Contains the PHP API files and database connection logic.

## Getting Started

### Backend Setup (PHP & MySQL)
1. Move the contents of the `backend` folder to your local server (e.g., `C:/xampp/htdocs/backend/`).
2. Import the database SQL file (if available) into phpMyAdmin.
3. Configure `db_connect.php` with your database credentials.

### Frontend Setup (Android)
1. Open the project in Android Studio.
2. Navigate to `app/src/main/java/com/example/ksjcomplaintnmaintenance/Config.java`.
3. Update the `BASE_URL` with your local IP address (e.g., `http://192.168.x.x/backend/`).
4. Build and run the application on an emulator or physical device.

## Technologies Used
- **Android**: Java, Volley (Networking), Glide (Image loading), MPAndroidChart (Data visualization).
- **Backend**: PHP, MySQL.
