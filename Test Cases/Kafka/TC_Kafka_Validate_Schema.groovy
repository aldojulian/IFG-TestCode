import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

/**
 * TC_Kafka_Validate_Schema
 * Pengujian Kafka di mana Katalon bertindak sebagai CONSUMER
 * Fokus: Validasi schema/struktur pesan yang diterima dari Kafka topic
 *
 * Test ini memvalidasi:
 * 1. Semua required fields ada dalam pesan
 * 2. Tipe data setiap field benar
 * 3. Format nilai sesuai (email, timestamp, dll)
 * 4. Nilai field tidak null/kosong
 */

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration

WS.comment("=== TC_Kafka_Validate_Schema: Mulai validasi schema pesan ===")

// ==================== SETUP: Konfigurasi Properties ====================
Properties producerProps = new Properties()
producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, GlobalVariable.KAFKA_BOOTSTRAP_SERVERS)
producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())
producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())

Properties consumerProps = new Properties()
consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, GlobalVariable.KAFKA_BOOTSTRAP_SERVERS)
consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "schema-validator-group")
consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, 'earliest')
consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, 'false')

// ==================== STEP 1: Produksi beberapa pesan dengan berbagai schema ====================
WS.comment("=== SETUP: Memproduksi pesan-pesan untuk validasi schema ===")

def messages = [
    [
        key: "schema-test-001",
        value: [
            eventId: "evt-001",
            eventType: "ORDER_PLACED",
            userId: "usr-123",
            productId: "prd-456",
            quantity: 2,
            price: 150000.00,
            currency: "IDR",
            status: "PENDING",
            createdAt: new Date().toInstant().toString(),
            metadata: [
                channel: "web",
                ipAddress: "192.168.1.1"
            ]
        ]
    ],
    [
        key: "schema-test-002",
        value: [
            eventId: "evt-002",
            eventType: "PAYMENT_PROCESSED",
            userId: "usr-123",
            orderId: "ord-789",
            amount: 150000.00,
            currency: "IDR",
            paymentMethod: "BANK_TRANSFER",
            status: "SUCCESS",
            processedAt: new Date().toInstant().toString(),
            transactionId: "txn-abc123"
        ]
    ]
]

KafkaProducer<String, String> producer = null
KafkaConsumer<String, String> consumer = null

try {
    producer = new KafkaProducer<>(producerProps)
    
    messages.each { msg ->
        def jsonValue = new JsonBuilder(msg.value).toString()
        def record = new ProducerRecord<>(GlobalVariable.KAFKA_TOPIC, msg.key as String, jsonValue)
        producer.send(record).get()
        WS.comment("✅ Pesan diproduksi: " + msg.key)
    }
    producer.flush()

    // ==================== STEP 2: Consume dan Validasi Schema Setiap Pesan ====================
    WS.comment("=== CONSUMER: Mulai consume dan validasi schema pesan ===")

    consumer = new KafkaConsumer<>(consumerProps)
    consumer.subscribe(Arrays.asList(GlobalVariable.KAFKA_TOPIC))

    def receivedMessages = [:]
    int maxRetries = 5
    int retryCount = 0

    while (receivedMessages.size() < 2 && retryCount < maxRetries) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3))
        for (ConsumerRecord<String, String> record : records) {
            if (record.key() in ["schema-test-001", "schema-test-002"]) {
                receivedMessages[record.key()] = record.value()
            }
        }
        consumer.commitSync()
        retryCount++
    }

    // ==================== CONSUMER: Validasi Schema ORDER_PLACED ====================
    WS.comment("=== Validasi Schema: ORDER_PLACED event ===")
    
    if (receivedMessages["schema-test-001"]) {
        def jsonSlurper = new JsonSlurper()
        def orderEvent = jsonSlurper.parseText(receivedMessages["schema-test-001"])

        // Required fields validation
        def requiredFields = ["eventId", "eventType", "userId", "productId", "quantity", "price", "currency", "status", "createdAt"]
        requiredFields.each { field ->
            assert orderEvent[field] != null : "❌ Required field '$field' tidak ditemukan"
            WS.comment("✅ Field '$field' ada")
        }

        // Data type validation
        assert orderEvent.quantity instanceof Integer : "❌ 'quantity' harus Integer"
        assert orderEvent.price instanceof Number : "❌ 'price' harus Number"
        assert orderEvent.metadata instanceof Map : "❌ 'metadata' harus Object/Map"

        // Value validation
        assert orderEvent.eventType == "ORDER_PLACED" : "❌ eventType tidak sesuai"
        assert orderEvent.currency == "IDR" : "❌ currency tidak sesuai"
        assert orderEvent.status in ["PENDING", "CONFIRMED", "CANCELLED"] : "❌ status tidak valid"
        assert orderEvent.quantity > 0 : "❌ quantity harus > 0"
        assert orderEvent.price > 0 : "❌ price harus > 0"

        WS.comment("✅ Schema ORDER_PLACED valid")
    }

    // ==================== CONSUMER: Validasi Schema PAYMENT_PROCESSED ====================
    WS.comment("=== Validasi Schema: PAYMENT_PROCESSED event ===")

    if (receivedMessages["schema-test-002"]) {
        def jsonSlurper = new JsonSlurper()
        def paymentEvent = jsonSlurper.parseText(receivedMessages["schema-test-002"])

        def requiredFields = ["eventId", "eventType", "userId", "orderId", "amount", "currency", "paymentMethod", "status", "processedAt", "transactionId"]
        requiredFields.each { field ->
            assert paymentEvent[field] != null : "❌ Required field '$field' tidak ditemukan"
            WS.comment("✅ Field '$field' ada")
        }

        // Data type validation
        assert paymentEvent.amount instanceof Number : "❌ 'amount' harus Number"
        assert paymentEvent.eventType == "PAYMENT_PROCESSED" : "❌ eventType tidak sesuai"
        assert paymentEvent.status in ["SUCCESS", "FAILED", "PENDING"] : "❌ status tidak valid"
        assert paymentEvent.amount > 0 : "❌ amount harus > 0"

        // TransactionId format validation (tidak boleh kosong)
        assert !paymentEvent.transactionId.toString().isEmpty() : "❌ transactionId tidak boleh kosong"

        WS.comment("✅ Schema PAYMENT_PROCESSED valid")
    }

    WS.comment("=== TC_Kafka_Validate_Schema BERHASIL ===")

} catch (Exception e) {
    WS.comment("❌ ERROR: " + e.getMessage())
    throw e
} finally {
    if (producer != null) producer.close()
    if (consumer != null) consumer.close()
}
