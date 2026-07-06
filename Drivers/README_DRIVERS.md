# Drivers Folder

Folder ini digunakan untuk menempatkan library JAR eksternal yang dibutuhkan oleh proyek Katalon.

## Library yang Diperlukan untuk Kafka Testing

### 1. kafka-clients (WAJIB)

Download dari Maven Central dan tempatkan di folder ini:

- **File**: `kafka-clients-3.6.0.jar`
- **Download URL**: https://search.maven.org/remotecontent?filepath=org/apache/kafka/kafka-clients/3.6.0/kafka-clients-3.6.0.jar
- **Alternatif**: https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients/3.6.0

### 2. Dependensi tambahan (jika diperlukan)

Kafka clients mungkin membutuhkan:
- `slf4j-api-1.7.36.jar`
- `slf4j-simple-1.7.36.jar` (untuk logging)
- `lz4-java-1.8.0.jar` (untuk kompresi, opsional)
- `snappy-java-1.1.10.4.jar` (untuk kompresi, opsional)
- `zstd-jni-1.5.5-1.jar` (untuk kompresi, opsional)

## Cara Menambahkan JAR di Katalon Studio

1. Copy file JAR ke folder `Drivers/` ini
2. Di Katalon Studio, buka **Project → Project Settings → External Libraries**
3. Klik **Add** dan pilih file JAR
4. Atau cukup restart Katalon Studio (akan otomatis mendeteksi JAR di folder Drivers)

## Download Script (PowerShell)

```powershell
# Jalankan di folder KatalonProject/Drivers
$url = "https://search.maven.org/remotecontent?filepath=org/apache/kafka/kafka-clients/3.6.0/kafka-clients-3.6.0.jar"
Invoke-WebRequest -Uri $url -OutFile "kafka-clients-3.6.0.jar"
Write-Host "kafka-clients-3.6.0.jar berhasil didownload"
```

## Download Script (curl)

```bash
# Jalankan di folder KatalonProject/Drivers
curl -L -o kafka-clients-3.6.0.jar \
  "https://search.maven.org/remotecontent?filepath=org/apache/kafka/kafka-clients/3.6.0/kafka-clients-3.6.0.jar"
```
