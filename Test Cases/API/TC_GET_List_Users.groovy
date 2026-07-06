import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.apache.http.client.methods.HttpGet

/**
 * TC_GET_List_Users
 * Pengujian GET endpoint untuk mendapatkan daftar pengguna dari ReqRes.in
 * Katalon berperan sebagai:
 *   - Producer: Mengirimkan HTTP GET request ke server
 *   - Consumer: Menerima dan memvalidasi response dari server
 */

// ===================== PRODUCER: Kirim GET Request =====================
WS.comment("=== PRODUCER: Mengirimkan GET Request ke /users?page=1 ===")

def response = WS.sendRequest(findTestObject('Object Repository/API/GET_List_Users'))

// ===================== CONSUMER: Validasi Response ===================
WS.comment("=== CONSUMER: Memvalidasi response yang diterima ===")

// Validasi status code 200
WS.verifyResponseStatusCode(response, 200)
WS.comment("✅ Status Code: 200 OK")

// Validasi response time < 5000ms
WS.verifyElementPropertyValue(response, 'header.Content-Type', 'application/json; charset=utf-8', FailureHandling.OPTIONAL)

// Parse response body
import groovy.json.JsonSlurper
def jsonSlurper = new JsonSlurper()
def responseBody = jsonSlurper.parseText(response.getResponseBodyContent())

// Validasi struktur response
assert responseBody.page != null : "Field 'page' tidak ditemukan dalam response"
assert responseBody.per_page != null : "Field 'per_page' tidak ditemukan dalam response"
assert responseBody.total != null : "Field 'total' tidak ditemukan dalam response"
assert responseBody.data != null : "Field 'data' tidak ditemukan dalam response"
assert responseBody.data instanceof List : "Field 'data' harus berupa array"
assert responseBody.data.size() > 0 : "Data tidak boleh kosong"

// Validasi setiap item dalam data
responseBody.data.each { user ->
    assert user.id != null : "Setiap user harus memiliki 'id'"
    assert user.email != null : "Setiap user harus memiliki 'email'"
    assert user.first_name != null : "Setiap user harus memiliki 'first_name'"
    assert user.last_name != null : "Setiap user harus memiliki 'last_name'"
}

WS.comment("✅ Struktur data response valid")
WS.comment("✅ Total users pada halaman 1: " + responseBody.data.size())
WS.comment("✅ Total users keseluruhan: " + responseBody.total)
WS.comment("=== TEST CASE GET_List_Users BERHASIL ===")
