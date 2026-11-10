@echo off
setlocal
cd /d "%~dp0"
echo Compilando y ejecutando SIGU-UCN...

set "JAVA_CMD=java"
if defined JAVA_HOME set "JAVA_CMD=%JAVA_HOME%\bin\java"

set "JAVA_VERSION="
for /f "tokens=3" %%v in ('call "%JAVA_CMD%" -version 2^>^&1 ^| findstr /i version') do (
    if not defined JAVA_VERSION set "JAVA_VERSION=%%~v"
)

set "JAVA_MAJOR=0"
for /f "delims=.-" %%m in ("%JAVA_VERSION%") do set "JAVA_MAJOR=%%m"

if %JAVA_MAJOR% LSS 21 (
    echo.
    echo Se necesita Java 21 o superior para ejecutar SIGU-UCN.
    if defined JAVA_VERSION (echo Version detectada: %JAVA_VERSION%) else (echo No se encontro Java en JAVA_HOME ni en el PATH.)
    echo Instale Java 21 y defina JAVA_HOME apuntando a esa instalacion.
    goto :fin
)

if exist jte-classes rmdir /s /q jte-classes

call "%~dp0mvnw.cmd" -q compile exec:exec "-Dexec.executable=%JAVA_CMD%" "-Dexec.args=-cp %%%%classpath cl.ucn.app.main.Main"

:fin
pause
