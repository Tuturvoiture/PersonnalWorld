@echo off
setlocal EnableExtensions EnableDelayedExpansion

rem Libere verrous / caches laisses par runClient (Loom) qui bloquent Minecraft.
rem Usage :
rem   script\fix-minecraft-cache.bat
rem   script\fix-minecraft-cache.bat --purge-loom
rem   script\fix-minecraft-cache.bat --kill-launcher-mc
rem   script\fix-minecraft-cache.bat --clean-build
rem   script\fix-minecraft-cache.bat --gradle-stop
rem   script\fix-minecraft-cache.bat --dry-run
rem   script\fix-minecraft-cache.bat --help

rem Avant tout shift : %~dp0 doit etre preserve
set "SCRIPT_DIR=%~dp0"

set "DRY_RUN=0"
set "PURGE_LOOM=0"
set "KILL_LAUNCHER_MC=0"
set "CLEAN_BUILD=0"
set "GRADLE_STOP=0"

:parse_args
if "%~1"=="" goto args_done
if /i "%~1"=="--dry-run" set "DRY_RUN=1" & shift & goto parse_args
if /i "%~1"=="--purge-loom" set "PURGE_LOOM=1" & shift & goto parse_args
if /i "%~1"=="--kill-launcher-mc" set "KILL_LAUNCHER_MC=1" & shift & goto parse_args
if /i "%~1"=="--clean-build" set "CLEAN_BUILD=1" & shift & goto parse_args
if /i "%~1"=="--gradle-stop" set "GRADLE_STOP=1" & shift & goto parse_args
if /i "%~1"=="--help" goto help
if /i "%~1"=="-h" goto help
echo Option inconnue: %~1 (voir --help)
exit /b 2

:help
echo.
echo fix-minecraft-cache.bat — debloque cache / session.lock apres runClient zombie
echo.
echo Options :
echo   --purge-loom         Supprime .gradle\loom-cache (et cible junction si present)
echo   --kill-launcher-mc   Tue aussi javaw CurseForge / launcher officiel
echo   --clean-build        Supprime les dossiers build\ versions
echo   --gradle-stop        Appelle gradlew --stop
echo   --dry-run            Affiche sans tuer ni supprimer
echo   -h / --help          Aide
echo.
exit /b 0

:args_done
call "%SCRIPT_DIR%_env.bat"
if errorlevel 1 exit /b 1

echo == fix-minecraft-cache ==
echo root=%CD%
if "%DRY_RUN%"=="1" echo (dry-run)
if "%PURGE_LOOM%"=="1" echo purge-loom=oui
if "%KILL_LAUNCHER_MC%"=="1" echo kill-launcher-mc=oui
if "%CLEAN_BUILD%"=="1" echo clean-build=oui
if "%GRADLE_STOP%"=="1" echo gradle-stop=oui
echo.

rem --- 1) Gradle daemons (optionnel) ---
if "%GRADLE_STOP%"=="1" (
  echo -- Gradle daemons ^(gradlew --stop^) --
  if "%DRY_RUN%"=="1" (
    echo [dry-run] gradlew.bat --stop
  ) else (
    if exist "gradlew.bat" (
      call gradlew.bat --stop
    ) else (
      echo (pas de gradlew.bat — skip^)
    )
  )
  echo.
) else (
  echo -- Gradle daemons --
  echo   (skip gradlew --stop ; JVM projet tuees ci-dessous^)
  echo   (ajoute --gradle-stop si besoin^)
  echo.
)

rem --- 2) Processus Java projet / launcher ---
echo -- Processus Java --
set "FOUND=0"
for /f "skip=1 tokens=*" %%L in ('wmic process where "name='java.exe' or name='javaw.exe'" get ProcessId^,CommandLine /FORMAT:LIST 2^>nul') do (
  set "LINE=%%L"
  if defined LINE (
    if /i "!LINE:~0,10!"=="CommandLine" (
      set "CMD=!LINE:~11!"
    )
    if /i "!LINE:~0,9!"=="ProcessId" (
      set "PID=!LINE:~10!"
      call :ClassifyAndMaybeKill
      set "CMD="
      set "PID="
    )
  )
)
if "%FOUND%"=="0" echo   (aucun processus liste)
if "%DRY_RUN%"=="0" timeout /t 2 /nobreak >nul
echo.

rem --- 3) session.lock orphelins (run/saves du projet) ---
echo -- session.lock --
set "LOCK_FOUND=0"
for /r "%CD%\versions" %%F in (session.lock) do (
  if exist "%%F" (
    set "LOCK_FOUND=1"
    echo   remove %%F
    if "%DRY_RUN%"=="0" del /f /q "%%F" >nul 2>&1
  )
)
for /r "%CD%\run" %%F in (session.lock) do (
  if exist "%%F" (
    set "LOCK_FOUND=1"
    echo   remove %%F
    if "%DRY_RUN%"=="0" del /f /q "%%F" >nul 2>&1
  )
)
if "%KILL_LAUNCHER_MC%"=="1" (
  if defined APPDATA if exist "%APPDATA%\.minecraft" (
    for /r "%APPDATA%\.minecraft" %%F in (session.lock) do (
      if exist "%%F" (
        set "LOCK_FOUND=1"
        echo   remove %%F
        if "%DRY_RUN%"=="0" del /f /q "%%F" >nul 2>&1
      )
    )
  )
  if defined USERPROFILE if exist "%USERPROFILE%\curseforge\minecraft\Instances" (
    for /r "%USERPROFILE%\curseforge\minecraft\Instances" %%F in (session.lock) do (
      if exist "%%F" (
        set "LOCK_FOUND=1"
        echo   remove %%F
        if "%DRY_RUN%"=="0" del /f /q "%%F" >nul 2>&1
      )
    )
  )
)
if "%LOCK_FOUND%"=="0" echo   (aucun session.lock)
echo.

rem --- 4) build dirs (optionnel) ---
if "%CLEAN_BUILD%"=="1" (
  echo -- build dirs --
  call :RemoveDir "versions\1.21.1\build"
  call :RemoveDir "fabric\versions\1.21.1\build"
  call :RemoveDir "neoforge\versions\1.21.1\build"
  call :RemoveDir "fabric\build"
  call :RemoveDir "build\loom-cache"
  echo.
)

rem --- 5) loom-cache (optionnel) ---
if "%PURGE_LOOM%"=="1" (
  echo -- loom-cache --
  set "LOOM=%CD%\.gradle\loom-cache"
  if exist "!LOOM!" (
    rem Si junction, supprimer aussi la cible (C:\Temp\... typique)
    for /f "delims=" %%T in ('dir /AL "!LOOM!" 2^>nul ^| findstr /i "JUNCTION SYMLINK"') do (
      for /f "tokens=2 delims=[]" %%P in ("%%T") do (
        echo   junction -^> %%P
        echo   remove target %%P
        if "%DRY_RUN%"=="0" (
          rmdir /s /q "%%P" 2>nul
          if exist "%%P" rd /s /q "%%P" 2>nul
        )
      )
    )
    echo   remove !LOOM!
    if "%DRY_RUN%"=="0" (
      rmdir /s /q "!LOOM!" 2>nul
      if exist "!LOOM!" rd /s /q "!LOOM!" 2>nul
    )
  ) else (
    echo   (absent^)
  )
  if exist "%CD%\.gradle" (
    for /d %%Z in ("%CD%\.gradle\zipfstmp*") do (
      echo   remove %%Z
      if "%DRY_RUN%"=="0" rmdir /s /q "%%Z" 2>nul
    )
    for /d %%Z in ("%CD%\.gradle\*\zipfstmp*") do (
      echo   remove %%Z
      if "%DRY_RUN%"=="0" rmdir /s /q "%%Z" 2>nul
    )
  )
  echo.
)

echo OK — tu peux relancer Minecraft (launcher) ou script\run-client.bat
if "%PURGE_LOOM%"=="1" echo Note: --purge-loom force un re-download/remap Loom au prochain build (plus lent).
exit /b 0

:ClassifyAndMaybeKill
if not defined PID goto :eof
if not defined CMD goto :eof
set "TAG="
echo !CMD! | findstr /i /c:"PersonnalWorld" /c:"fabric-loom" /c:"runClient" /c:"GradleDaemon" /c:"org.gradle" >nul
if not errorlevel 1 set "TAG=dev"
if not defined TAG (
  echo !CMD! | findstr /i /c:"curseforge" /c:"BootstrapLauncher" /c:"--gameDir" /c:"java-runtime" /c:"minecraft\runtime" >nul
  if not errorlevel 1 set "TAG=launcher"
)
if not defined TAG goto :eof
set "FOUND=1"
rem Snippet court sans dump cmdline complete
set "SNIP=!TAG! pid=!PID!"
if /i "!TAG!"=="dev" (
  echo   kill [dev] !SNIP!
  if "%DRY_RUN%"=="0" taskkill /F /PID !PID! >nul 2>&1
) else if /i "!TAG!"=="launcher" (
  if "%KILL_LAUNCHER_MC%"=="1" (
    echo   kill [launcher] !SNIP!
    if "%DRY_RUN%"=="0" taskkill /F /PID !PID! >nul 2>&1
  ) else (
    echo   leave [launcher] !SNIP! (ajoute --kill-launcher-mc pour tuer^)
  )
)
goto :eof

:RemoveDir
set "D=%~1"
if exist "%CD%\%D%" (
  echo   remove %D%
  if "%DRY_RUN%"=="0" (
    rmdir /s /q "%CD%\%D%" 2>nul
    if exist "%CD%\%D%" rd /s /q "%CD%\%D%" 2>nul
  )
)
goto :eof
