<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description>PUT request untuk mengupdate data user di ReqRes.in</description>
   <name>PUT_Update_User</name>
   <tag></tag>
   <elementGuidId>obj-put-update-user</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRelativeImagePath>false</useRelativeImagePath>
   <connectionTimeout>0</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody>{"name": "Jane Doe", "job": "Senior QA Engineer"}</httpBody>
   <httpBodyContent>{"name": "Jane Doe", "job": "Senior QA Engineer"}</httpBodyContent>
   <httpBodyType>JSON</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <useRegex>false</useRegex>
      <value>application/json</value>
      <webElementGuid>hdr-content-type-put</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <useRegex>false</useRegex>
      <value>application/json</value>
      <webElementGuid>hdr-accept-put</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.0.0</katalonVersion>
   <migratedToApiPlugin>false</migratedToApiPlugin>
   <restRequestMethod>PUT</restRequestMethod>
   <restUrl>https://reqres.in/api/users/2</restUrl>
   <serviceType>RESTful</serviceType>
   <useConfiguration>false</useConfiguration>
   <verificationScript>import static org.assertj.core.api.Assertions.*

def jsonBody = parseJson(response.getResponseBodyContent())
assertThat(response.getStatusCode()).isEqualTo(200)
assertThat(jsonBody.name).isEqualTo("Jane Doe")
assertThat(jsonBody.updatedAt).isNotNull()
</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
