@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ============================================
echo Running Automated Verification Suite...
echo ============================================

java -cp bin TestVerificationRunner
pause
