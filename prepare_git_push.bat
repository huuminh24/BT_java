@echo off
REM Script chuẩn bị code để push lên GitHub
REM Script này sẽ:
REM 1. Kiểm tra xem config.properties có trong git history không
REM 2. Nếu có, xóa khỏi index và hướng dẫn rotate API key
REM 3. Commit các thay đổi mới
REM 4. Push lên GitHub

cd /d "%~dp0"

echo ========================================
echo  Chuẩn bị push code len GitHub
echo ========================================
echo.

echo [1] Kiem tra trang thai git...
git status
echo.

echo [2] Kiem tra xem config.properties co trong git index khong...
git ls-files src\main\resources\config.properties 2>nul
if %errorlevel% == 0 (
    echo WARNING: config.properties dang duoc track boi git!
    echo Day la NGUY HIEM vi file nay chua API key that.
    echo.
    echo [3] Xoa config.properties khoi git index (khong xoa file local)...
    git rm --cached src\main\resources\config.properties
    echo Da xoa khoi git index.
    echo.
    echo QUAN TRONG: Ban can ROTATE API KEY tai:
    echo https://aistudio.google.com/app/apikey
    echo Vi API key cu da bi leak trong git history neu da commit.
    echo.
) else (
    echo GOOD: config.properties khong duoc track boi git.
    echo.
)

echo [4] Them config.properties.example vao git...
git add src\main\resources\config.properties.example
echo.

echo [5] Them .gitignore cap nhat vao git...
git add .gitignore
echo.

echo [6] Kiem tra trang thai truoc khi commit...
git status
echo.

echo [7] Commit thay doi...
git commit -m "Security: Remove config.properties from git tracking and add .gitignore for sensitive files"
echo.

echo [8] Push len GitHub...
git push origin master
echo.

echo ========================================
echo  HOAN TAT!
echo ========================================
echo.
echo Da push code len GitHub thanh cong.
echo.
echo LAM VIEC NHAP:
echo 1. config.properties khong duoc push (co trong .gitignore)
echo 2. config.properties.example duoc push lam mau
echo 3. Ban can giu config.properties local de chay app
echo.
echo NEU config.properties da duoc commit truoc do:
echo - API key co the da bi leak trong git history
echo - Hay consider rotate API key tai Google AI Studio
echo - Hoac dung git-filter-repo de xoa khoi history (canh trong)
echo.

pause
