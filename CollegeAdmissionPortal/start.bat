@echo off
title College Admission Portal - Server Manager
color 0b
echo ====================================================================
echo             COLLEGE ADMISSION PORTAL - START SERVER
echo ====================================================================
echo.

set "PORTAL_DIR=%~dp0"
if exist "%~dp0CollegeAdmissionPortal\backend" (
    set "PORTAL_DIR=%~dp0CollegeAdmissionPortal\"
)
cd /d "%PORTAL_DIR%"

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo [INFO] Freeing port 8080 held by PID %%a...
    taskkill /F /PID %%a >nul 2>&1
)

if not exist "build" mkdir "build"

echo [1/3] Compiling Java backend classes...
javac -d build backend\*.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check if JDK is installed and on PATH.
    pause
    exit /b %errorlevel%
)

echo [2/3] Launching Web Server (Backend APIs + Frontend Web Portal)...
start "College Admission Portal Server" cmd /k "title College Admission Portal Server && cd /d "%PORTAL_DIR%" && java -cp build Main"

echo [3/3] Opening Web Portal in Default Browser...
start "" cmd /c "ping 127.0.0.1 -n 3 >nul && start http://localhost:8080/"

echo.
echo ====================================================================
echo Web portal and backend are now RUNNING at: http://localhost:8080/
echo Both Backend REST APIs and Frontend web pages are being served.
echo To stop the portal, double-click 'stop.bat'.
echo ====================================================================
ping 127.0.0.1 -n 3 >nul
exit /b 0
