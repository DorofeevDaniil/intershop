@echo off
    setlocal
    if "%~13"=="" (
        echo Usage: start.bat DB_HOST DB_PORT DB_NAME POSTGRES_USER POSTGRES_PASSWORD REDIS_HOST REDIS_PORT PAYMENT_SERVICE_HOST PAYMENT_SERVICE_PORT KEYCLOAK_HOST KEYCLOAK_PORT KEYCLOAK_CLIENT_ID KEYCLOAK_CLIENT_SECRET
        echo Example: start.bat localhost 5432 postgres root root 127.0.0.1 6379 localhost 9192 localhost 8181 client-test qtPcKbjQchNdVk0uJjm9tav5cldZbsrN
        exit /b 1
    )

    set "DB_HOST=%~1"
    set "DB_PORT=%~2"
    set "DB_NAME=%~3"
    set "POSTGRES_USER=%~4"
    set "POSTGRES_PASSWORD=%~5"
    set "REDIS_HOST=%~6"
    set "REDIS_PORT=%~7"
    set "PAYMENT_SERVICE_HOST=%~8"
    set "PAYMENT_SERVICE_PORT=%~9"
    set "KEYCLOAK_HOST=%~10"
    set "KEYCLOAK_PORT=%~11"
    set "KEYCLOAK_CLIENT_ID=%~12"
    set "KEYCLOAK_CLIENT_SECRET=%~13"

    echo Starting application...

    set DB_HOST=%DB_HOST%
    set DB_PORT=%DB_PORT%
    set DB_NAME=%DB_NAME%
    set POSTGRES_USER=%POSTGRES_USER%
    set POSTGRES_PASSWORD=%POSTGRES_PASSWORD%
    set REDIS_HOST=%REDIS_HOST%
    set REDIS_PORT=%REDIS_PORT%
    set PAYMENT_SERVICE_HOST=%PAYMENT_SERVICE_HOST%
    set PAYMENT_SERVICE_PORT=%PAYMENT_SERVICE_PORT%
    set KEYCLOAK_HOST=%KEYCLOAK_HOST%
    set KEYCLOAK_PORT=%KEYCLOAK_PORT%
    set KEYCLOAK_CLIENT_ID=%KEYCLOAK_CLIENT_ID%
    set KEYCLOAK_CLIENT_SECRET=%KEYCLOAK_CLIENT_SECRET%

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