package katalon.keywords

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil

import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import java.time.Duration

/**
 * KafkaKeywords.groovy
 * Custom Keywords untuk operasi Kafka di Katalon Studio
 *
 * Cara penggunaan:
 *   CustomKeywords.'katalon.keywords.KafkaKeywords.consumeMessage'(...)
 */
class KafkaKeywords {

    /**
     * Consume pesan dari Kafka topic dan kembalikan sebagai List<Map>
     *
     * @param bootstrapServers - Alamat Kafka broker (contoh: 'localhost:9092')
     * @param topic - Nama Kafka topic
     * @param groupId - Consumer group ID
     * @param timeoutSeconds - Waktu tunggu polling dalam detik
     * @param maxMessages - Jumlah maksimal pesan yang diambil
     * @return List<Map> berisi pesan-pesan yang diterima
     */
    @Keyword
    static List<Map> consumeMessage(String bootstrapServers, String topic, String groupId,
                                     int timeoutSeconds = 5, int maxMessages = 10) {
        KeywordUtil.logInfo("=== KafkaKeywords.consumeMessage: Mulai consume dari topic: $topic ===")

        Properties props = new Properties()
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, 'earliest')
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, 'false')
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxMessages.toString())

        List<Map> messages = []
        KafkaConsumer<String, String> consumer = null

        try {
            consumer = new KafkaConsumer<>(props)
            consumer.subscribe(Arrays.asList(topic))

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(timeoutSeconds))

            for (ConsumerRecord<String, String> record : records) {
                messages.add([
                    topic    : record.topic(),
                    partition: record.partition(),
                    offset   : record.offset(),
                    key      : record.key(),
                    value    : record.value(),
                    timestamp: record.timestamp()
                ])
                KeywordUtil.logInfo("📨 Pesan diterima - Key: ${record.key()}, Offset: ${record.offset()}")
            }
            consumer.commitSync()

            KeywordUtil.logInfo("✅ Total pesan diterima: ${messages.size()}")
        } catch (Exception e) {
            KeywordUtil.markFailed("❌ Gagal consume pesan: ${e.getMessage()}")
            throw e
        } finally {
            if (consumer != null) consumer.close()
        }

        return messages
    }

    /**
     * Produce pesan ke Kafka topic
     *
     * @param bootstrapServers - Alamat Kafka broker
     * @param topic - Nama Kafka topic
     * @param key - Message key
     * @param value - Message value (JSON string)
     * @return Map berisi metadata produksi (topic, partition, offset)
     */
    @Keyword
    static Map produceMessage(String bootstrapServers, String topic, String key, String value) {
        KeywordUtil.logInfo("=== KafkaKeywords.produceMessage: Mengirim pesan ke topic: $topic ===")

        Properties props = new Properties()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())
        props.put(ProducerConfig.ACKS_CONFIG, 'all')
        props.put(ProducerConfig.RETRIES_CONFIG, '3')

        KafkaProducer<String, String> producer = null
        Map result = [:]

        try {
            producer = new KafkaProducer<>(props)
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value)
            RecordMetadata metadata = producer.send(record).get()
            producer.flush()

            result = [
                topic    : metadata.topic(),
                partition: metadata.partition(),
                offset   : metadata.offset()
            ]

            KeywordUtil.logInfo("✅ Pesan berhasil diproduksi ke topic: ${metadata.topic()}")
            KeywordUtil.logInfo("✅ Partition: ${metadata.partition()}, Offset: ${metadata.offset()}")
        } catch (Exception e) {
            KeywordUtil.markFailed("❌ Gagal produce pesan: ${e.getMessage()}")
            throw e
        } finally {
            if (producer != null) producer.close()
        }

        return result
    }

    /**
     * Validasi pesan JSON dari Kafka terhadap schema yang diharapkan
     *
     * @param messageValue - String JSON dari Kafka message
     * @param requiredFields - List field yang harus ada
     * @param expectedValues - Map field->value yang nilainya harus sesuai (opsional)
     * @return boolean true jika validasi berhasil
     */
    @Keyword
    static boolean validateMessageSchema(String messageValue, List<String> requiredFields,
                                          Map<String, Object> expectedValues = [:]) {
        KeywordUtil.logInfo("=== KafkaKeywords.validateMessageSchema: Memvalidasi schema pesan ===")

        def jsonSlurper = new JsonSlurper()
        def parsedMessage

        try {
            parsedMessage = jsonSlurper.parseText(messageValue)
        } catch (Exception e) {
            KeywordUtil.markFailed("❌ Message bukan JSON valid: ${e.getMessage()}")
            return false
        }

        // Cek required fields
        for (String field : requiredFields) {
            if (parsedMessage[field] == null) {
                KeywordUtil.markFailed("❌ Required field '$field' tidak ada dalam pesan")
                return false
            }
            KeywordUtil.logInfo("✅ Field '$field' ada: ${parsedMessage[field]}")
        }

        // Cek expected values
        for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
            def actualValue = parsedMessage[entry.key]
            if (actualValue != entry.value) {
                KeywordUtil.markFailed("❌ Field '${entry.key}' tidak sesuai. Expected: ${entry.value}, Actual: $actualValue")
                return false
            }
            KeywordUtil.logInfo("✅ Field '${entry.key}' sesuai: $actualValue")
        }

        KeywordUtil.logInfo("✅ Validasi schema berhasil")
        return true
    }

    /**
     * Tunggu sampai pesan dengan key tertentu diterima dari topic
     *
     * @param bootstrapServers - Alamat Kafka broker
     * @param topic - Nama topic
     * @param groupId - Consumer group ID
     * @param expectedKey - Key pesan yang diharapkan
     * @param maxWaitSeconds - Waktu tunggu maksimal (default: 30 detik)
     * @return String value dari pesan yang ditemukan, atau null jika tidak ditemukan
     */
    @Keyword
    static String waitForMessage(String bootstrapServers, String topic, String groupId,
                                  String expectedKey, int maxWaitSeconds = 30) {
        KeywordUtil.logInfo("=== KafkaKeywords.waitForMessage: Menunggu pesan dengan key '$expectedKey' ===")

        Properties props = new Properties()
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, 'earliest')
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, 'false')

        KafkaConsumer<String, String> consumer = null
        String foundValue = null
        long startTime = System.currentTimeMillis()

        try {
            consumer = new KafkaConsumer<>(props)
            consumer.subscribe(Arrays.asList(topic))

            while (foundValue == null && (System.currentTimeMillis() - startTime) < (maxWaitSeconds * 1000)) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3))

                for (ConsumerRecord<String, String> record : records) {
                    if (record.key() == expectedKey) {
                        foundValue = record.value()
                        KeywordUtil.logInfo("✅ Pesan dengan key '$expectedKey' ditemukan")
                        break
                    }
                }
                consumer.commitSync()
            }

            if (foundValue == null) {
                KeywordUtil.logInfo("⚠️ Pesan dengan key '$expectedKey' tidak ditemukan dalam ${maxWaitSeconds} detik")
            }
        } catch (Exception e) {
            KeywordUtil.markFailed("❌ Error saat menunggu pesan: ${e.getMessage()}")
            throw e
        } finally {
            if (consumer != null) consumer.close()
        }

        return foundValue
    }
}
