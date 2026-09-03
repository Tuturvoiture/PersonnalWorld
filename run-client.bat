@echo off
setlocal EnableExtensions
rem Lance le client Fabric de dev (Minecraft 1.21.1) via Gradle, avec Java 21.
rem Gradle lit JAVA_HOME, pas le "java" du PATH : un JAVA_HOME=17 casse Stonecutter.
rem Usage: run-client.bat
rem        run-client.bat --offline
rem        run-client.bat :neoforge:1.21.1:runClient

cd /d "%~dp0"
if errorlevel 1 (
  echo Failed to cd to project root.
  exit /b 1
)

call :EnsureJava21
if errorlevel 1 exit /b 1

echo JAVA_HOME=%JAVA_HOME%
set "PATH=%JAVA_HOME%\bin;%PATH%"

call gradlew.bat --stop >nul 2>&1
call :UnlockBuildDirs

if "%~1"=="" (
  call gradlew.bat :fabric:1.21.1:runClient
) else (
  echo.%~1 | findstr /b ":" >nul
  if not errorlevel 1 (
    call gradlew.bat %*
  ) else (
    call gradlew.bat :fabric:1.21.1:runClient %*
  )
)
exit /b %ERRORLEVEL%

:UnlockBuildDirs
rem OneDrive / ancien daemon Gradle peut verrouiller build/ (classes, resources → processResources stale outputs).
for %%P in (
  "versions\1.21.1\build"
  "fabric\versions\1.21.1\build"
  "neoforge\versions\1.21.1\build"
) do (
  if exist %%P (
    attrib -r -s -h %%P /s /d >nul 2>&1
    rmdir /s /q %%P 2>nul
    if exist %%P (
      echo WARNING: Impossible de supprimer %%P
      echo Ferme un autre runClient / pause la synchro OneDrive sur ce dossier, puis relance.
    ) else (
      echo Pre-run: nettoyage %%P
    )
  )
)
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
