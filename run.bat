@echo off
setlocal

set SRC=%~dp0
if "%SRC:~-1%"=="\" set SRC=%SRC:~0,-1%
set LIB=%SRC%\target\dependency
set CLASSES=%SRC%\target\classes

echo ==========================================
echo   Hostel Management System - Build Script
echo ==========================================

if not exist %CLASSES% mkdir %CLASSES%
if not exist %LIB% mkdir %LIB%

if not exist %LIB%\mysql-connector-j-9.2.0.jar (
    echo Downloading MySQL Connector...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.2.0/mysql-connector-j-9.2.0.jar' -OutFile '%LIB%\mysql-connector-j-9.2.0.jar'"
    if errorlevel 1 (
        echo Failed to download MySQL Connector. Please check your internet connection.
        pause
        exit /b 1
    )
)

if not exist %LIB%\json-20250107.jar (
    echo Downloading JSON library...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/json/json/20250107/json-20250107.jar' -OutFile '%LIB%\json-20250107.jar'"
    if errorlevel 1 (
        echo Failed to download JSON library. Please check your internet connection.
        pause
        exit /b 1
    )
)

echo Compiling Java files...
dir /s /b %SRC%\src\main\java\*.java > %SRC%\sources.txt 2>nul
javac -cp "%LIB%\*" -d %CLASSES% @%SRC%\sources.txt
if errorlevel 1 (
    echo.
    echo Compilation failed! Please ensure JDK 25 is installed and in your PATH.
    del %SRC%\sources.txt 2>nul
    pause
    exit /b 1
)
del %SRC%\sources.txt 2>nul

echo Copying resources...
if exist %SRC%\src\main\resources (
    xcopy /s /i /y %SRC%\src\main\resources\* %CLASSES%\ >nul 2>&1
)

echo.
echo ==========================================
echo   Starting Hostel Management System...
echo ==========================================
echo.
echo Default Login: admin / admin123
echo.

java -cp "%CLASSES%;%LIB%\*" org.example.hostelsystem.HostelSystemApplication

if errorlevel 1 (
    echo.
    echo Application exited with an error.
    pause
)

endlocal
