<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description>GET request untuk mendapatkan daftar pengguna dari ReqRes.in</description>
   <name>GET_List_Users</name>
   <tag></tag>
   <elementGuidId>obj-get-list-users</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRelativeImagePath>false</useRelativeImagePath>
   <connectionTimeout>0</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent></httpBodyContent>
   <httpBodyType></httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <useRegex>false</useRegex>
      <value>application/json</value>
      <webElementGuid>hdr-content-type-get</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <useRegex>false</useRegex>
      <value>application/json</value>
      <webElementGuid>hdr-accept-get</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.0.0</katalonVersion>
   <migratedToApiPlugin>false</migratedToApiPlugin>
   <restParameters>
      <isSelected>true</isSelected>
      <name>page</name>
      <value>1</value>
   </restParameters>
   <restRequestMethod>GET</restRequestMethod>
   <restUrl>https://reqres.in/api/users</restUrl>
   <serviceType>RESTful</serviceType>
   <useConfiguration>false</useConfiguration>
   <verificationScript>import static org.assertj.core.api.Assertions.*

import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.util.JsonUtil

def response = response // available for verification

def jsonBody = parseJson(response.getResponseBodyContent())
assertThat(response.getStatusCode()).isEqualTo(200)
assertThat(jsonBody.page).isNotNull()
assertThat(jsonBody.data).isNotNull()
</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
