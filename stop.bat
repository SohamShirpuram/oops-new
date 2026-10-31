@echo off
title College Admission Portal - Stop Server
color 0c
echo ====================================================================
echo             COLLEGE ADMISSION PORTAL - STOP SERVER
echo ====================================================================
echo.

set "STOPPED=0"

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo [INFO] Stopping server process PID %%a on port 8080...
    taskkill /F /PID %%a >nul 2>&1
    set "STOPPED=1"
)

taskkill /FI "WINDOWTITLE eq College Admission Portal Server*" /T /F >nul 2>&1

if "%STOPPED%"=="1" (
    echo.
    echo [SUCCESS] College Admission Portal server has been stopped successfully.
) else (
    echo [INFO] No running server found on port 8080. Server is already stopped.
)

echo.
echo ====================================================================
ping 127.0.0.1 -n 3 >nul
exit /b 0
