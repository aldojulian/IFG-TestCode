import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import groovy.json.JsonSlurper

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
import groovy.json.JsonBuilder

WS.comment("=== TC_Kafka_Consume_Message: Mulai pengujian ===")

// ==================== SETUP: Konfigurasi Kafka Consumer ====================
Properties consumerProps = new Properties()
consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, GlobalVariable.KAFKA_BOOTSTRAP_SERVERS)
consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GlobalVariable.KAFKA_GROUP_ID)
consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, 'earliest')
consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, 'false')
consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, '10')

// ==================== STEP 1: Publish test message (setup data) ====================
WS.comment("=== SETUP: Memproduksi pesan test ke Kafka topic ===")

Properties producerProps = new Properties()
producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, GlobalVariable.KAFKA_BOOTSTRAP_SERVERS)
producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())
producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())
producerProps.put(ProducerConfig.ACKS_CONFIG, 'all')

def testMessage = new JsonBuilder([
    id: "user-001",
    name: "John Doe",
    email: "johndoe@example.com",
    action: "USER_CREATED",
    timestamp: new Date().toInstant().toString()
]).toString()

KafkaProducer<String, String> producer = null
KafkaConsumer<String, String> consumer = null

try {
    // Produce test message
    producer = new KafkaProducer<>(producerProps)
    def record = new ProducerRecord<>(GlobalVariable.KAFKA_TOPIC, "test-key-001", testMessage)
    def metadata = producer.send(record).get()
    producer.flush()
    WS.comment("Pesan berhasil diproduksi ke topic: " + metadata.topic())
    WS.comment("Partition: " + metadata.partition() + ", Offset: " + metadata.offset())

    // ==================== STEP 2: CONSUMER - Consume dan validasi pesan ====================
    WS.comment("=== CONSUMER: Mulai mengonsumsi pesan dari Kafka topic ===")

    consumer = new KafkaConsumer<>(consumerProps)
    consumer.subscribe(Arrays.asList(GlobalVariable.KAFKA_TOPIC))

    boolean messageFound = false
    int maxRetries = 5
    int retryCount = 0
    def receivedMessage = null

    while (!messageFound && retryCount < maxRetries) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3))

        if (!records.isEmpty()) {
            for (ConsumerRecord<String, String> msg : records) {
                WS.comment("📨 Pesan diterima:")
                WS.comment("   - Topic: " + msg.topic())
                WS.comment("   - Partition: " + msg.partition())
                WS.comment("   - Offset: " + msg.offset())
                WS.comment("   - Key: " + msg.key())
                WS.comment("   - Value: " + msg.value())

                if (msg.key() == "test-key-001") {
                    receivedMessage = msg.value()
                    messageFound = true
                    break
                }
            }
            consumer.commitSync()
        }
        retryCount++
    }

    // ==================== CONSUMER: Validasi pesan yang diterima ====================
    WS.comment("=== CONSUMER: Memvalidasi konten pesan ===")

    assert messageFound : "Pesan dengan key 'test-key-001' tidak ditemukan dalam topic"
    WS.comment("Pesan berhasil diterima")

    def jsonSlurper = new JsonSlurper()
    def parsedMessage = jsonSlurper.parseText(receivedMessage)

    // Validasi field-field dalam pesan
    assert parsedMessage.id == "user-001" : "Field 'id' tidak sesuai"
    assert parsedMessage.name == "John Doe" : "Field 'name' tidak sesuai"
    assert parsedMessage.email == "johndoe@example.com" : "Field 'email' tidak sesuai"
    assert parsedMessage.action == "USER_CREATED" : "Field 'action' tidak sesuai"
    assert parsedMessage.timestamp != null : "Field 'timestamp' tidak ditemukan"

    WS.comment("Field 'id' valid: " + parsedMessage.id)
    WS.comment("Field 'name' valid: " + parsedMessage.name)
    WS.comment("Field 'email' valid: " + parsedMessage.email)
    WS.comment("Field 'action' valid: " + parsedMessage.action)
    WS.comment("Field 'timestamp' valid: " + parsedMessage.timestamp)
    WS.comment("=== TC_Kafka_Consume_Message BERHASIL ===")

} catch (Exception e) {
    WS.comment("ERROR: " + e.getMessage())
    throw e
} finally {
    if (producer != null) producer.close()
    if (consumer != null) consumer.close()
    WS.comment("=== Kafka Producer & Consumer telah ditutup ===")
}
