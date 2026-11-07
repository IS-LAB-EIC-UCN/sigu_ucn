@echo off
set JAVA_HOME=C:\Users\enzod\.vscode\extensions\redhat.java-1.58.0-win32-x64\jre\25.0.4.1-win32-x86_64
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "c:\Proyectos-Antigravity\ProyectoTallerIGESO\sigu_ucn"

echo Compilando y ejecutando SIGU-UCN...
cmd /c mvnw.cmd exec:java -Dexec.mainClass="cl.ucn.app.main.Main"
pause
