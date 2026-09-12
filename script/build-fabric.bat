@echo off
setlocal EnableExtensions
rem Build Fabric seulement (1.21.1) + collecte jar.
rem Usage: script\build-fabric.bat

call "%~dp0_env.bat"
if errorlevel 1 exit /b 1

echo.
echo === Build Fabric 1.21.1 ===
echo.

call gradlew.bat :fabric:1.21.1:buildAndCollect
set "ERR=%ERRORLEVEL%"
if not "%ERR%"=="0" (
  echo.
  echo BUILD FAILED ^(exit %ERR%^)
  exit /b %ERR%
)

echo.
echo BUILD OK — jar:
echo   build\libs\*\fabric\personnalworld-fabric-*.jar
exit /b 0
