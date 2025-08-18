@echo off
setlocal
echo Starting application...
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