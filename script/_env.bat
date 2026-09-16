@echo off
rem Shared env for scripts under script\ — sets project root + Java 21.
rem Called via: call "%~dp0_env.bat"

set "ROOT=%~dp0.."
cd /d "%ROOT%"
if errorlevel 1 (
  echo ERROR: cannot cd to project root: %ROOT%
  exit /b 1
)

call :EnsureJava21
if errorlevel 1 exit /b 1

echo JAVA_HOME=%JAVA_HOME%
set "PATH=%JAVA_HOME%\bin;%PATH%"
exit /b 0

:EnsureJava21
echo.%JAVA_HOME% | findstr /i /c:"jdk-21" >nul
if not errorlevel 1 if exist "%JAVA_HOME%\bin\java.exe" exit /b 0

set "CAND="
if exist "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot\bin\java.exe" (
  set "CAND=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
)
if not defined CAND for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-21*") do (
  if exist "%%~D\bin\java.exe" set "CAND=%%~D"
)
if not defined CAND for /d %%D in ("C:\Program Files\Java\jdk-21*") do (
  if exist "%%~D\bin\java.exe" set "CAND=%%~D"
)
if not defined CAND for /d %%D in ("C:\Program Files\Microsoft\jdk-21*") do (
  if exist "%%~D\bin\java.exe" set "CAND=%%~D"
)
if not defined CAND for /d %%D in ("C:\Program Files\BellSoft\LibericaJDK-21*") do (
  if exist "%%~D\bin\java.exe" set "CAND=%%~D"
)
if not defined CAND for /d %%D in ("C:\Program Files\Amazon Corretto\jdk21*") do (
  if exist "%%~D\bin\java.exe" set "CAND=%%~D"
)

if not defined CAND (
  echo ERROR: Java 21 introuvable. Installe Temurin 21, ou mets JAVA_HOME sur un JDK 21.
  if defined JAVA_HOME echo JAVA_HOME actuel=%JAVA_HOME%
  exit /b 1
)

echo JAVA_HOME n'etait pas Java 21 - bascule vers %CAND%
set "JAVA_HOME=%CAND%"
exit /b 0
