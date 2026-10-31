@echo off
title College Admission Portal - OOPS Project
color 0b
echo ====================================================================
echo             COLLEGE ADMISSION PORTAL - OOPS PROJECT
echo ====================================================================
echo.
echo [1/3] Preparing build environment...
cd /d "%~dp0"
if not exist "build" mkdir "build"

echo [2/3] Compiling Java backend classes...
javac -d build backend\*.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check if JDK is installed and on PATH.
    pause
    exit /b %errorlevel%
)

echo [3/3] Launching Web Server...
echo.
echo ====================================================================
echo Server is running at: http://localhost:8080/
echo Opening default web browser automatically...
echo To stop the server at any time, press Ctrl+C in this window.
echo ====================================================================
echo.

start "" cmd /c "timeout /t 1 /nobreak >nul & start http://localhost:8080/"

java -cp build Main
pause
