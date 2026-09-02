$env:PATH = "C:\Program Files\Java\jdk1.8.0_202\bin;" + $env:PATH
Write-Host "Compiling Smart Bank Management System..." -ForegroundColor Cyan
if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }
& javac -d bin -cp src (Get-Item "src\*.java")
if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Compilation successful!" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Compilation failed." -ForegroundColor Red
}
