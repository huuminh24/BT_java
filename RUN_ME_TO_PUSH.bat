@echo off
echo Dang push code len GitHub...
echo.
cd /d "%~dp0"
git add src/main/resources/config.properties.example
git add .gitignore
git add DEMO_GUIDE.md
git add README.md
git add prepare_git_push.bat
git add push_to_github.bat
git add simple_push.bat
git rm --cached src/main/resources/config.properties 2>nul
git commit -m "Security: Remove config.properties from git tracking and add documentation"
git push origin master
echo.
echo ========================================
echo  DA HOAN TAT!
echo ========================================
echo.
echo QUAN TRONG: Ban nen ROTATE API key tai:
echo https://aistudio.google.com/app/apikey
echo.
pause
