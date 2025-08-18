@echo off
setlocal

set "PID_FILE=%~dp0app.pid"

if exist "%PID_FILE%" (
    for /f "usebackq delims=" %%p in ("%PID_FILE%") do (
        set "PID=%%p"
        goto readDone
    )
    echo Could not read PID from file.
    goto end
) else (
    echo No PID file found. Is the application ${JAR_NAME} running?
    goto end
)

:readDone
echo Stopping application ${JAR_NAME} with PID [%PID%]
taskkill /PID %PID% /F
del "%PID_FILE%"
echo Application stopped.

:end
endlocal