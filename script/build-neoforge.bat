@echo off
setlocal EnableExtensions
rem Build NeoForge seulement (1.21.1) + collecte jar.
rem Usage: script\build-neoforge.bat

call "%~dp0_env.bat"
if errorlevel 1 exit /b 1

echo.
echo === Build NeoForge 1.21.1 ===
echo.

call gradlew.bat :neoforge:1.21.1:buildAndCollect
set "ERR=%ERRORLEVEL%"
if not "%ERR%"=="0" (
  echo.
  echo BUILD FAILED ^(exit %ERR%^)
  exit /b %ERR%
)

echo.
echo BUILD OK — jar:
echo   build\libs\*\neoforge\personnalworld-neoforge-*.jar
exit /b 0
