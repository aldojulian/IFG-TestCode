import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import groovy.json.JsonSlurper

/**
 * TC_GET_Single_User
 * Pengujian GET endpoint untuk mendapatkan data satu pengguna dari ReqRes.in
 * Katalon sebagai Producer: Mengirim request dengan user ID
 * Katalon sebagai Consumer: Memvalidasi response user yang diterima
 */

// ===================== PRODUCER: Kirim GET Request =====================
WS.comment("=== PRODUCER: Mengirimkan GET Request ke /users/2 ===")

def response = WS.sendRequest(findTestObject('Object Repository/API/GET_Single_User'))

// ===================== CONSUMER: Validasi Response =====================
WS.comment("=== CONSUMER: Memvalidasi response single user ===")

WS.verifyResponseStatusCode(response, 200)
WS.comment("✅ Status Code: 200 OK")

def jsonSlurper = new JsonSlurper()
def responseBody = jsonSlurper.parseText(response.getResponseBodyContent())

// Validasi struktur data user
assert responseBody.data != null : "Field 'data' tidak ditemukan"
assert responseBody.data.id == 2 : "User ID harus 2"
assert responseBody.data.email != null : "Email tidak boleh null"
assert responseBody.data.first_name != null : "First name tidak boleh null"
assert responseBody.data.last_name != null : "Last name tidak boleh null"
assert responseBody.data.avatar != null : "Avatar tidak boleh null"

// Validasi support info
assert responseBody.support != null : "Field 'support' tidak ditemukan"
assert responseBody.support.url != null : "Support URL tidak boleh null"

WS.comment("✅ User ID: " + responseBody.data.id)
WS.comment("✅ Email: " + responseBody.data.email)
WS.comment("✅ Nama: " + responseBody.data.first_name + " " + responseBody.data.last_name)
WS.comment("=== TEST CASE GET_Single_User BERHASIL ===")
