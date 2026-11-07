@echo off
cd /d "%~dp0"
echo Compilando y ejecutando SIGU-UCN...
cmd /c mvnw.cmd exec:java -Dexec.mainClass="cl.ucn.app.main.Main"
pause
