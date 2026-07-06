<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description>POST request untuk login dan mendapatkan token autentikasi dari ReqRes.in</description>
   <name>POST_Login</name>
   <tag></tag>
   <elementGuidId>obj-post-login</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRelativeImagePath>false</useRelativeImagePath>
   <connectionTimeout>0</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody>{"email": "eve.holt@reqres.in", "password": "cityslicka"}</httpBody>
   <httpBodyContent>{"email": "eve.holt@reqres.in", "password": "cityslicka"}</httpBodyContent>
   <httpBodyType>JSON</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <useRegex>false</useRegex>
      <value>application/json</value>
      <webElementGuid>hdr-content-type-login</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <useRegex>false</useRegex>
      <value>application/json</value>
      <webElementGuid>hdr-accept-login</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.0.0</katalonVersion>
   <migratedToApiPlugin>false</migratedToApiPlugin>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>https://reqres.in/api/login</restUrl>
   <serviceType>RESTful</serviceType>
   <useConfiguration>false</useConfiguration>
   <verificationScript>import static org.assertj.core.api.Assertions.*

def jsonBody = parseJson(response.getResponseBodyContent())
assertThat(response.getStatusCode()).isEqualTo(200)
assertThat(jsonBody.token).isNotNull()
</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
