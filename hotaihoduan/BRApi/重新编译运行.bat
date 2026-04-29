@echo off
echo ========================================
echo 重新编译并运行 BRApi
echo ========================================
echo.

echo [1/3] 清理旧的编译文件...
call mvn clean
if %errorlevel% neq 0 (
    echo 清理失败！
    pause
    exit /b 1
)
echo.

echo [2/3] 编译项目...
call mvn compile
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)
echo.

echo [3/3] 启动服务...
echo 服务将在 http://localhost:8080/api 启动
echo 按 Ctrl+C 停止服务
echo.
call mvn spring-boot:run

pause
