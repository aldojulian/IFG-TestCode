package katalon.keywords

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

/**
 * APIKeywords.groovy
 * Custom Keywords untuk operasi RESTful API di Katalon Studio
 */
class APIKeywords {

    /**
     * Validasi response body mengandung semua field yang diharapkan
     *
     * @param responseBody - String JSON dari response
     * @param requiredFields - List field yang harus ada (support dot notation: "data.id")
     * @return boolean true jika semua field ada
     */
    @Keyword
    static boolean validateResponseFields(String responseBody, List<String> requiredFields) {
        KeywordUtil.logInfo("=== APIKeywords.validateResponseFields ===")

        def jsonSlurper = new JsonSlurper()
        def parsed = jsonSlurper.parseText(responseBody)

        for (String fieldPath : requiredFields) {
            def value = getNestedValue(parsed, fieldPath.split('\\.'))
            if (value == null) {
                KeywordUtil.markFailed("❌ Field '$fieldPath' tidak ditemukan dalam response")
                return false
            }
            KeywordUtil.logInfo("✅ Field '$fieldPath': $value")
        }

        return true
    }

    /**
     * Helper: ambil nilai dari nested map menggunakan path array
     */
    private static Object getNestedValue(def obj, String[] pathParts) {
        def current = obj
        for (String part : pathParts) {
            if (current == null) return null
            if (current instanceof Map) {
                current = current[part]
            } else {
                return null
            }
        }
        return current
    }

    /**
     * Validasi response body terhadap expected values
     *
     * @param responseBody - String JSON dari response
     * @param expectedValues - Map field->value yang nilainya harus sesuai
     * @return boolean true jika semua nilai sesuai
     */
    @Keyword
    static boolean validateResponseValues(String responseBody, Map<String, Object> expectedValues) {
        KeywordUtil.logInfo("=== APIKeywords.validateResponseValues ===")

        def jsonSlurper = new JsonSlurper()
        def parsed = jsonSlurper.parseText(responseBody)

        for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
            def actualValue = getNestedValue(parsed, entry.key.split('\\.'))
            if (actualValue != entry.value) {
                KeywordUtil.markFailed("❌ Field '${entry.key}' tidak sesuai. Expected: ${entry.value}, Actual: $actualValue")
                return false
            }
            KeywordUtil.logInfo("✅ Field '${entry.key}' sesuai: $actualValue")
        }

        return true
    }

    /**
     * Format dan log response body untuk debugging
     *
     * @param responseBody - String JSON dari response
     * @param label - Label untuk logging
     */
    @Keyword
    static void logResponseBody(String responseBody, String label = "Response Body") {
        try {
            def jsonSlurper = new JsonSlurper()
            def parsed = jsonSlurper.parseText(responseBody)
            KeywordUtil.logInfo("=== $label ===\n" + JsonOutput.prettyPrint(responseBody))
        } catch (Exception e) {
            KeywordUtil.logInfo("=== $label (raw) ===\n" + responseBody)
        }
    }

    /**
     * Validasi format email dalam response
     *
     * @param email - String email yang akan divalidasi
     * @return boolean true jika format email valid
     */
    @Keyword
    static boolean validateEmailFormat(String email) {
        def emailRegex = /^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/
        boolean isValid = email ==~ emailRegex
        if (isValid) {
            KeywordUtil.logInfo("✅ Email '$email' valid")
        } else {
            KeywordUtil.markFailed("❌ Format email '$email' tidak valid")
        }
        return isValid
    }

    /**
     * Validasi response time dalam batas yang ditentukan
     *
     * @param responseTime - Response time dalam ms
     * @param maxResponseTimeMs - Batas maksimal response time
     * @return boolean true jika response time dalam batas
     */
    @Keyword
    static boolean validateResponseTime(long responseTime, long maxResponseTimeMs = 5000) {
        boolean isValid = responseTime <= maxResponseTimeMs
        if (isValid) {
            KeywordUtil.logInfo("✅ Response time: ${responseTime}ms (dalam batas ${maxResponseTimeMs}ms)")
        } else {
            KeywordUtil.markFailed("❌ Response time: ${responseTime}ms melebihi batas ${maxResponseTimeMs}ms")
        }
        return isValid
    }
}
