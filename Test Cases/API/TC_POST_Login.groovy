import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import groovy.json.JsonSlurper

/**
 * TC_POST_Login
 * Pengujian POST endpoint untuk autentikasi (login) di ReqRes.in
 * Katalon sebagai Producer: Mengirim kredensial user (email + password)
 * Katalon sebagai Consumer: Memvalidasi token autentikasi yang diterima
 */

// ===================== PRODUCER: Kirim POST Login Request =====================
WS.comment("=== PRODUCER: Mengirimkan POST Request ke /login dengan kredensial ===")

def response = WS.sendRequest(findTestObject('Object Repository/API/POST_Login'))

// ===================== CONSUMER: Validasi Response Token =====================
WS.comment("=== CONSUMER: Memvalidasi token autentikasi yang diterima ===")

WS.verifyResponseStatusCode(response, 200)
WS.comment("✅ Status Code: 200 OK")

def jsonSlurper = new JsonSlurper()
def responseBody = jsonSlurper.parseText(response.getResponseBodyContent())

// Validasi token ada dalam response
assert responseBody.token != null : "Token autentikasi tidak ditemukan dalam response"
assert !responseBody.token.toString().isEmpty() : "Token tidak boleh kosong"
assert responseBody.token.toString().length() > 5 : "Token terlalu pendek, mungkin tidak valid"

WS.comment("✅ Login berhasil")
WS.comment("✅ Token diterima: " + responseBody.token)
WS.comment("✅ Token valid dengan panjang: " + responseBody.token.toString().length() + " karakter")
WS.comment("=== TEST CASE POST_Login BERHASIL ===")
