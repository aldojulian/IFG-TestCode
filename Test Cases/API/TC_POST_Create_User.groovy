import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import groovy.json.JsonSlurper

/**
 * TC_POST_Create_User
 * Pengujian POST endpoint untuk membuat pengguna baru di ReqRes.in
 * Katalon sebagai Producer: Mengirim payload JSON berisi data user baru
 * Katalon sebagai Consumer: Memvalidasi response bahwa user berhasil dibuat
 */

// ===================== PRODUCER: Kirim POST Request dengan payload =====================
WS.comment("=== PRODUCER: Mengirimkan POST Request ke /users dengan body JSON ===")

def response = WS.sendRequest(findTestObject('Object Repository/API/POST_Create_User'))

// ===================== CONSUMER: Validasi Response =====================
WS.comment("=== CONSUMER: Memvalidasi response pembuatan user baru ===")

WS.verifyResponseStatusCode(response, 201)
WS.comment("✅ Status Code: 201 Created")

def jsonSlurper = new JsonSlurper()
def responseBody = jsonSlurper.parseText(response.getResponseBodyContent())

// Validasi data yang dibuat
assert responseBody.name == "John Doe" : "Nama user tidak sesuai"
assert responseBody.job == "QA Engineer" : "Job tidak sesuai"
assert responseBody.id != null : "ID user baru harus ada dalam response"
assert responseBody.createdAt != null : "Timestamp createdAt harus ada dalam response"

// Validasi format createdAt (ISO 8601)
assert responseBody.createdAt =~ /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/ : "Format createdAt tidak valid"

WS.comment("✅ User berhasil dibuat dengan ID: " + responseBody.id)
WS.comment("✅ Nama: " + responseBody.name)
WS.comment("✅ Job: " + responseBody.job)
WS.comment("✅ Created At: " + responseBody.createdAt)
WS.comment("=== TEST CASE POST_Create_User BERHASIL ===")
