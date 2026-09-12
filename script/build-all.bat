@echo off
setlocal EnableExtensions
rem Build Fabric + NeoForge (1.21.1) et collecte les jars dans build\libs\<version>\
rem Usage: script\build-all.bat

call "%~dp0_env.bat"
if errorlevel 1 exit /b 1

echo.
echo === Build ALL loaders (Fabric + NeoForge) ===
echo.

call gradlew.bat chiseledBuild
set "ERR=%ERRORLEVEL%"
if not "%ERR%"=="0" (
  echo.
  echo BUILD FAILED ^(exit %ERR%^)
  exit /b %ERR%
)

echo.
echo BUILD OK — jars:
echo   build\libs\*\fabric\personnalworld-fabric-*.jar
echo   build\libs\*\neoforge\personnalworld-neoforge-*.jar
exit /b 0
