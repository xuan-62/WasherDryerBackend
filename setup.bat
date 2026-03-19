@echo off
setlocal

set MAVEN_VERSION=3.9.9
set TOMCAT_VERSION=10.1.52
set SCRIPT_DIR=%~dp0

if not exist "%SCRIPT_DIR%maven" (
    echo Downloading Maven %MAVEN_VERSION%...
    powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%SCRIPT_DIR%maven.zip'"
    powershell -Command "Expand-Archive -Path '%SCRIPT_DIR%maven.zip' -DestinationPath '%SCRIPT_DIR%maven_tmp' -Force"
    move "%SCRIPT_DIR%maven_tmp\apache-maven-%MAVEN_VERSION%" "%SCRIPT_DIR%maven"
    rmdir /s /q "%SCRIPT_DIR%maven_tmp"
    del "%SCRIPT_DIR%maven.zip"
    echo Maven installed.
) else (
    echo Maven already present, skipping.
)

if not exist "%SCRIPT_DIR%tomcat" (
    echo Downloading Tomcat %TOMCAT_VERSION%...
    powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/tomcat/tomcat-10/v%TOMCAT_VERSION%/bin/apache-tomcat-%TOMCAT_VERSION%.zip' -OutFile '%SCRIPT_DIR%tomcat.zip'"
    powershell -Command "Expand-Archive -Path '%SCRIPT_DIR%tomcat.zip' -DestinationPath '%SCRIPT_DIR%tomcat_tmp' -Force"
    move "%SCRIPT_DIR%tomcat_tmp\apache-tomcat-%TOMCAT_VERSION%" "%SCRIPT_DIR%tomcat"
    rmdir /s /q "%SCRIPT_DIR%tomcat_tmp"
    del "%SCRIPT_DIR%tomcat.zip"
    echo Tomcat installed.
) else (
    echo Tomcat already present, skipping.
)

if not exist "%SCRIPT_DIR%.env" (
    copy "%SCRIPT_DIR%.env.example" "%SCRIPT_DIR%.env"
    echo .env created -- fill in your credentials before starting.
)

echo Setup complete.
endlocal
