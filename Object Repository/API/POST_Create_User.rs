<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description>POST request untuk membuat user baru di ReqRes.in</description>
   <name>POST_Create_User</name>
   <tag></tag>
   <elementGuidId>obj-post-create-user</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRelativeImagePath>false</useRelativeImagePath>
   <connectionTimeout>0</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody>{"name": "John Doe", "job": "QA Engineer"}</httpBody>
   <httpBodyContent>{"name": "John Doe", "job": "QA Engineer"}</httpBodyContent>
   <httpBodyType>JSON</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <useRegex>false</useRegex>
      <value>application/json</value>
      <webElementGuid>hdr-content-type-post</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <useRegex>false</useRegex>
      <value>application/json</value>
      <webElementGuid>hdr-accept-post</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.0.0</katalonVersion>
   <migratedToApiPlugin>false</migratedToApiPlugin>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>https://reqres.in/api/users</restUrl>
   <serviceType>RESTful</serviceType>
   <useConfiguration>false</useConfiguration>
   <verificationScript>import static org.assertj.core.api.Assertions.*

def jsonBody = parseJson(response.getResponseBodyContent())
assertThat(response.getStatusCode()).isEqualTo(201)
assertThat(jsonBody.name).isEqualTo("John Doe")
assertThat(jsonBody.id).isNotNull()
assertThat(jsonBody.createdAt).isNotNull()
</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
