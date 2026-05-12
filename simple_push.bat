cd /d C:\Users\ADMIN\Downloads\BT_Java-master\BT_Java-master
git add src/main/resources/config.properties.example
git add .gitignore
git add DEMO_GUIDE.md
git add README.md
git add prepare_git_push.bat
git add push_to_github.bat
git rm --cached src/main/resources/config.properties 2>nul
git commit -m "Security: Remove config.properties from git tracking and add documentation"
git push origin master
