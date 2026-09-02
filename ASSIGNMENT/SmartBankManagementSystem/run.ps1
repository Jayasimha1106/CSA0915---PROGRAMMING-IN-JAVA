$env:PATH = "C:\Program Files\Java\jdk1.8.0_202\bin;" + $env:PATH
Write-Host "Launching Smart Bank Management System GUI..." -ForegroundColor Cyan
& java -cp bin SmartBankManagementSystem
