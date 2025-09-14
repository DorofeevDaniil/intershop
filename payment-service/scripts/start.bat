@echo off
setlocal

if "%~2"=="" (
        echo Usage: start.bat KEYCLOAK_HOST KEYCLOAK_PORT
        echo Example: start.bat localhost 8181
        exit /b 1
    )

set "KEYCLOAK_HOST=%~1"
set "KEYCLOAK_PORT=%~2"

echo Starting application...

set KEYCLOAK_HOST=%KEYCLOAK_HOST%
set KEYCLOAK_PORT=%KEYCLOAK_PORT%

set JAR_NAME=${JAR_NAME}
echo Starting %JAR_NAME%...

start "storefront-app" java -jar "%~dp0%JAR_NAME%"
timeout /t 5 > nul

for /f "tokens=1,*" %%a in ('jps -lv ^| findstr /i "%JAR_NAME%"') do (
    echo %%a > "%~dp0app.pid"
    echo Application started with PID %%a
    goto end
)

echo Failed to find running application matching %JAR_NAME%

:end
endlocal