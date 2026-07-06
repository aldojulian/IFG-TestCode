# Katalon Project - RESTful API & Kafka Testing

Proyek Katalon Studio untuk pengujian **RESTful API** dan **Apache Kafka Consumer**.

---

## 📁 Struktur Proyek

```
KatalonProject/
├── .project                          # Katalon project descriptor
├── docker-compose.yml                # Setup Kafka dengan Docker
├── README.md                         # Dokumentasi ini
│
├── Profiles/
│   └── default.glbl                  # Global Variables (BASE_URL, KAFKA_*)
│
├── Test Cases/
│   ├── API/
│   │   ├── TC_GET_List_Users.groovy   # GET daftar pengguna
│   │   ├── TC_GET_Single_User.groovy  # GET satu pengguna
│   │   ├── TC_POST_Create_User.groovy # POST buat pengguna baru
│   │   ├── TC_PUT_Update_User.groovy  # PUT update pengguna
│   │   ├── TC_DELETE_User.groovy      # DELETE hapus pengguna
│   │   └── TC_POST_Login.groovy       # POST login & validasi token
│   └── Kafka/
│       ├── TC_Kafka_Consume_Message.groovy  # Consume & validasi pesan Kafka
│       └── TC_Kafka_Validate_Schema.groovy  # Validasi schema pesan Kafka
│
├── Test Suites/
│   ├── TS_RESTful_API.ts             # Suite untuk semua API test
│   └── TS_Kafka_Consumer.ts          # Suite untuk semua Kafka test
│
├── Object Repository/
│   └── API/
│       ├── GET_List_Users.rs          # Request object GET /users
│       ├── GET_Single_User.rs         # Request object GET /users/2
│       ├── POST_Create_User.rs        # Request object POST /users
│       ├── PUT_Update_User.rs         # Request object PUT /users/2
│       ├── DELETE_User.rs             # Request object DELETE /users/2
│       └── POST_Login.rs              # Request object POST /login
│
├── Keywords/
│   └── katalon/keywords/
│       ├── KafkaKeywords.groovy       # Custom keywords untuk Kafka
│       └── APIKeywords.groovy         # Custom keywords untuk API
│
├── Data Files/
│   ├── users_data.csv                 # Dataset pengguna
│   └── kafka_messages_data.csv        # Dataset pesan Kafka
│
└── Drivers/                           # ⚠️ Tempatkan kafka-clients JAR di sini
    └── (kafka-clients-3.6.0.jar)     # Download dari Maven Central
```

---

## 🚀 Quick Start

### 1. Prasyarat

- **Katalon Studio** versi 8.0.0 atau lebih baru
- **Docker Desktop** (untuk Kafka)
- **Java 11+**

### 2. Buka Project di Katalon Studio

1. Buka Katalon Studio
2. Pilih **File → Open Project**
3. Arahkan ke folder `KatalonProject/`
4. Klik **Open**

---

## 🔌 Part 1: RESTful API Testing

**API yang digunakan:** [ReqRes.in](https://reqres.in) (free public API)

### Konsep Producer & Consumer

| Peran Katalon | Penjelasan |
|---------------|-----------|
| **Producer** | Katalon mengirimkan HTTP request (GET/POST/PUT/DELETE) ke server |
| **Consumer** | Katalon menerima dan memvalidasi response dari server |

### Test Cases

| Test Case | Method | Endpoint | Deskripsi |
|-----------|--------|----------|-----------|
| `TC_GET_List_Users` | GET | `/api/users?page=1` | Ambil daftar user, validasi pagination & struktur data |
| `TC_GET_Single_User` | GET | `/api/users/2` | Ambil satu user, validasi semua field |
| `TC_POST_Create_User` | POST | `/api/users` | Buat user baru, validasi status 201 & data response |
| `TC_PUT_Update_User` | PUT | `/api/users/2` | Update user, validasi status 200 & data baru |
| `TC_DELETE_User` | DELETE | `/api/users/2` | Hapus user, validasi status 204 & body kosong |
| `TC_POST_Login` | POST | `/api/login` | Login, validasi token autentikasi diterima |

### Cara Menjalankan

1. Di Katalon, buka **Test Suites/TS_RESTful_API**
2. Klik **Run** (tidak perlu setup tambahan, langsung terhubung internet)

---

## 📨 Part 2: Kafka Consumer Testing

### Cara Setup Kafka dengan Docker

```bash
# 1. Pastikan Docker Desktop berjalan
# 2. Buka terminal di folder KatalonProject
cd KatalonProject

# 3. Jalankan Kafka
docker-compose up -d

# 4. Cek status (tunggu sampai semua healthy)
docker-compose ps

# 5. Verifikasi topics telah dibuat
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

**Kafka UI tersedia di:** http://localhost:8080

### Menambahkan kafka-clients JAR ke Katalon

1. Download `kafka-clients-3.6.0.jar` dari [Maven Central](https://search.maven.org/artifact/org.apache.kafka/kafka-clients/3.6.0/jar)
2. Tempatkan file JAR di folder `Drivers/` dalam project
3. Restart Katalon Studio

### Test Cases Kafka

| Test Case | Deskripsi |
|-----------|-----------|
| `TC_Kafka_Consume_Message` | Publish satu pesan, consume dari topic, validasi konten |
| `TC_Kafka_Validate_Schema` | Publish beberapa event, validasi schema setiap tipe event |

### Cara Menjalankan

1. Pastikan Kafka berjalan: `docker-compose up -d`
2. Di Katalon, buka **Test Suites/TS_Kafka_Consumer**
3. Klik **Run**

---

## ⚙️ Global Variables

Konfigurasi dapat diubah di **Profiles/default.glbl**:

| Variable | Default Value | Keterangan |
|----------|---------------|-----------|
| `BASE_URL` | `https://reqres.in/api` | Base URL untuk ReqRes.in |
| `JSON_PLACEHOLDER_URL` | `https://jsonplaceholder.typicode.com` | URL JSON Placeholder (opsional) |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Alamat Kafka broker |
| `KAFKA_TOPIC` | `test-topic` | Nama Kafka topic utama |
| `KAFKA_GROUP_ID` | `katalon-consumer-group` | Consumer group ID |

---

## 📦 Kafka Topics yang Dibuat

| Topic | Partisi | Deskripsi |
|-------|---------|-----------|
| `test-topic` | 3 | Topic utama untuk testing |
| `user-events` | 3 | Events terkait user |
| `order-events` | 3 | Events terkait order |
| `payment-events` | 3 | Events terkait pembayaran |

---

## 🛠️ Custom Keywords

### KafkaKeywords

```groovy
// Consume pesan dari topic
List messages = CustomKeywords.'katalon.keywords.KafkaKeywords.consumeMessage'(
    'localhost:9092', 'test-topic', 'my-group', 5, 10
)

// Produce pesan ke topic
Map result = CustomKeywords.'katalon.keywords.KafkaKeywords.produceMessage'(
    'localhost:9092', 'test-topic', 'key-001', '{"id": "1", "name": "Test"}'
)

// Validasi schema pesan
boolean valid = CustomKeywords.'katalon.keywords.KafkaKeywords.validateMessageSchema'(
    messageValue, ['id', 'name', 'email'], ['action': 'USER_CREATED']
)

// Tunggu pesan dengan key tertentu
String message = CustomKeywords.'katalon.keywords.KafkaKeywords.waitForMessage'(
    'localhost:9092', 'test-topic', 'my-group', 'expected-key', 30
)
```

### APIKeywords

```groovy
// Validasi fields dalam response
boolean valid = CustomKeywords.'katalon.keywords.APIKeywords.validateResponseFields'(
    responseBody, ['data.id', 'data.email', 'support.url']
)

// Log response body
CustomKeywords.'katalon.keywords.APIKeywords.logResponseBody'(responseBody, "API Response")

// Validasi format email
boolean isValidEmail = CustomKeywords.'katalon.keywords.APIKeywords.validateEmailFormat'(email)
```

---

## 🧹 Cleanup

```bash
# Hentikan dan hapus semua container Kafka
docker-compose down

# Hapus juga volumes (data Kafka)
docker-compose down -v
```

---

## 📄 Referensi

- [ReqRes.in API Documentation](https://reqres.in)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Katalon Studio Documentation](https://docs.katalon.com)
- [kafka-clients Maven Central](https://search.maven.org/artifact/org.apache.kafka/kafka-clients)
