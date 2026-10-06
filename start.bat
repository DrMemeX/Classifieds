@echo off
setlocal

cd /d "%~dp0"

echo ========================================
echo Starting Classifieds project
echo ========================================
echo.

docker compose up -d --build

if errorlevel 1 (
    echo.
    echo ========================================
    echo ERROR: Docker Compose startup failed
    echo ========================================
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Waiting for databases
echo ========================================

:wait_main_postgres
set "MAIN_DB_STATUS="

for /f "delims=" %%i in ('docker inspect -f "{{.State.Health.Status}}" classifieds-postgres 2^>nul') do (
    set "MAIN_DB_STATUS=%%i"
)

if not "%MAIN_DB_STATUS%"=="healthy" (
    echo Waiting for classifieds-postgres...
    timeout /t 2 /nobreak >nul
    goto wait_main_postgres
)

echo classifieds-postgres is healthy.

:wait_audit_postgres
set "AUDIT_DB_STATUS="

for /f "delims=" %%i in ('docker inspect -f "{{.State.Health.Status}}" classifieds-audit-postgres 2^>nul') do (
    set "AUDIT_DB_STATUS=%%i"
)

if not "%AUDIT_DB_STATUS%"=="healthy" (
    echo Waiting for classifieds-audit-postgres...
    timeout /t 2 /nobreak >nul
    goto wait_audit_postgres
)

echo classifieds-audit-postgres is healthy.

echo.
echo ========================================
echo Containers status
echo ========================================
echo.

docker compose ps

echo.
echo ========================================
echo Classifieds project is started
echo ========================================
echo.
echo Main application:  http://localhost:8080
echo Main PostgreSQL:   localhost:4444
echo Audit PostgreSQL:  localhost:6767
echo Kafka:             localhost:9092
echo.

pause