@echo off
setlocal EnableExtensions
rem Alias : lance le client avec rebuild force (clean build/ + --stop).
rem Equivalent a : script\run-client.bat --rebuild

call "%~dp0run-client.bat" --rebuild %*
exit /b %ERRORLEVEL%
