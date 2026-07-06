import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable

WS.comment("=== PRODUCER: Mengirimkan DELETE Request ke /users/2 ===")

def response = WS.sendRequest(findTestObject('Object Repository/API/DELETE_User'))

WS.comment("=== CONSUMER: Memvalidasi response penghapusan user ===")


WS.verifyResponseStatusCode(response, 204)
WS.comment("Status Code: 204 No Content")


def responseBody = response.getResponseBodyContent()
assert responseBody == null || responseBody.trim().isEmpty() : "Body response DELETE harus kosong"

WS.comment("Response body kosong (sesuai standar REST DELETE)")
WS.comment("User dengan ID 2 berhasil dihapus")
WS.comment("=== TEST CASE DELETE_User BERHASIL ===")
