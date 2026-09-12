@echo off
setlocal EnableExtensions EnableDelayedExpansion
rem Lance le client de dev (Minecraft 1.21.1) via Gradle.
rem Usage:
rem   script\run-client.bat                 (rapide : incremental, pas de clean build/)
rem   script\run-client.bat --rebuild       (force : --stop + wipe build/ puis runClient)
rem   script\run-client.bat --offline
rem   script\run-client.bat :neoforge:1.21.1:runClient
rem   script\run-client.bat --rebuild --offline
rem   script\run-client-rebuild.bat         (alias de --rebuild)

call "%~dp0_env.bat"
if errorlevel 1 exit /b 1

set "DO_REBUILD=0"
set "GRADLE_ARGS="

:ParseArgs
if "%~1"=="" goto AfterParse
if /I "%~1"=="--rebuild" (
  set "DO_REBUILD=1"
  shift
  goto ParseArgs
)
if /I "%~1"=="--clean" (
  set "DO_REBUILD=1"
  shift
  goto ParseArgs
)
if defined GRADLE_ARGS (
  set "GRADLE_ARGS=!GRADLE_ARGS! %~1"
) else (
  set "GRADLE_ARGS=%~1"
)
shift
goto ParseArgs

:AfterParse
if "!DO_REBUILD!"=="1" (
  echo Mode rebuild: arret daemon + nettoyage build/
  call gradlew.bat --stop >nul 2>&1
  call :UnlockBuildDirs
) else (
  echo Mode rapide: incremental, sans wipe build/
  echo Si processResources / OneDrive bloque, relance avec --rebuild
)

if not defined GRADLE_ARGS (
  call gradlew.bat :fabric:1.21.1:runClient
) else (
  echo.!GRADLE_ARGS!| findstr /b ":" >nul
  if not errorlevel 1 (
    call gradlew.bat !GRADLE_ARGS!
  ) else (
    call gradlew.bat :fabric:1.21.1:runClient !GRADLE_ARGS!
  )
)
exit /b %ERRORLEVEL%

:UnlockBuildDirs
rem OneDrive / ancien daemon Gradle peut verrouiller build/ (processResources stale outputs).
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
