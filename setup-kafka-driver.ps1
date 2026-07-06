# ================================================================
# setup-kafka-driver.ps1
# Script untuk mendownload kafka-clients JAR yang dibutuhkan
# oleh Katalon Studio untuk pengujian Kafka
# ================================================================
# Cara menjalankan:
#   cd KatalonProject
#   .\setup-kafka-driver.ps1
# ================================================================

param(
    [string]$KafkaVersion = "3.6.0"
)

$ErrorActionPreference = "Stop"

Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "  Setup Kafka Driver untuk Katalon Studio" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan

$driversDir = Join-Path $PSScriptRoot "Drivers"

# Buat folder Drivers jika belum ada
if (-not (Test-Path $driversDir)) {
    New-Item -ItemType Directory -Path $driversDir | Out-Null
    Write-Host "[+] Folder Drivers dibuat" -ForegroundColor Green
}

# Daftar JAR yang dibutuhkan
$jars = @(
    @{
        Name = "kafka-clients-$KafkaVersion.jar"
        Url  = "https://search.maven.org/remotecontent?filepath=org/apache/kafka/kafka-clients/$KafkaVersion/kafka-clients-$KafkaVersion.jar"
    },
    @{
        Name = "slf4j-api-1.7.36.jar"
        Url  = "https://search.maven.org/remotecontent?filepath=org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"
    },
    @{
        Name = "slf4j-simple-1.7.36.jar"
        Url  = "https://search.maven.org/remotecontent?filepath=org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar"
    }
)

foreach ($jar in $jars) {
    $destPath = Join-Path $driversDir $jar.Name
    
    if (Test-Path $destPath) {
        Write-Host "[✓] $($jar.Name) sudah ada, skip." -ForegroundColor Yellow
        continue
    }

    Write-Host "[~] Mendownload $($jar.Name)..." -ForegroundColor White
    try {
        Invoke-WebRequest -Uri $jar.Url -OutFile $destPath -UseBasicParsing
        $sizeMB = [math]::Round((Get-Item $destPath).Length / 1MB, 2)
        Write-Host "[✓] $($jar.Name) berhasil didownload ($sizeMB MB)" -ForegroundColor Green
    } catch {
        Write-Host "[!] Gagal mendownload $($jar.Name): $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "  Selesai! Langkah selanjutnya:" -ForegroundColor Cyan
Write-Host "  1. Restart Katalon Studio" -ForegroundColor White
Write-Host "  2. Jalankan: docker-compose up -d" -ForegroundColor White
Write-Host "  3. Buka Test Suite: TS_Kafka_Consumer" -ForegroundColor White
Write-Host "=================================================" -ForegroundColor Cyan
