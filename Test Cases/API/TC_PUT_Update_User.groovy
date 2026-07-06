import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import groovy.json.JsonSlurper

/**
 * TC_PUT_Update_User
 * Pengujian PUT endpoint untuk mengupdate data pengguna di ReqRes.in
 * Katalon sebagai Producer: Mengirim payload JSON berisi data yang diperbarui
 * Katalon sebagai Consumer: Memvalidasi response bahwa data berhasil diperbarui
 */

// ===================== PRODUCER: Kirim PUT Request =====================
WS.comment("=== PRODUCER: Mengirimkan PUT Request ke /users/2 dengan body JSON ===")

def response = WS.sendRequest(findTestObject('Object Repository/API/PUT_Update_User'))

// ===================== CONSUMER: Validasi Response =====================
WS.comment("=== CONSUMER: Memvalidasi response update user ===")

WS.verifyResponseStatusCode(response, 200)
WS.comment("✅ Status Code: 200 OK")

def jsonSlurper = new JsonSlurper()
def responseBody = jsonSlurper.parseText(response.getResponseBodyContent())

// Validasi data yang diupdate
assert responseBody.name == "Jane Doe" : "Nama tidak sesuai setelah update"
assert responseBody.job == "Senior QA Engineer" : "Job tidak sesuai setelah update"
assert responseBody.updatedAt != null : "Timestamp updatedAt harus ada dalam response"

// Validasi format updatedAt (ISO 8601)
assert responseBody.updatedAt =~ /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/ : "Format updatedAt tidak valid"

WS.comment("✅ User berhasil diupdate")
WS.comment("✅ Nama baru: " + responseBody.name)
WS.comment("✅ Job baru: " + responseBody.job)
WS.comment("✅ Updated At: " + responseBody.updatedAt)
WS.comment("=== TEST CASE PUT_Update_User BERHASIL ===")
