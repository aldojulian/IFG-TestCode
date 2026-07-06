<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description>GET request untuk mendapatkan data single user dari ReqRes.in</description>
   <name>GET_Single_User</name>
   <tag></tag>
   <elementGuidId>obj-get-single-user</elementGuidId>
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
      <webElementGuid>hdr-content-type-single</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <useRegex>false</useRegex>
      <value>application/json</value>
      <webElementGuid>hdr-accept-single</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.0.0</katalonVersion>
   <migratedToApiPlugin>false</migratedToApiPlugin>
   <restRequestMethod>GET</restRequestMethod>
   <restUrl>https://reqres.in/api/users/2</restUrl>
   <serviceType>RESTful</serviceType>
   <useConfiguration>false</useConfiguration>
   <verificationScript>import static org.assertj.core.api.Assertions.*

def jsonBody = parseJson(response.getResponseBodyContent())
assertThat(response.getStatusCode()).isEqualTo(200)
assertThat(jsonBody.data.id).isEqualTo(2)
assertThat(jsonBody.data.email).isNotNull()
</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
