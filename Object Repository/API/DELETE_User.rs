<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description>DELETE request untuk menghapus user di ReqRes.in</description>
   <name>DELETE_User</name>
   <tag></tag>
   <elementGuidId>obj-delete-user</elementGuidId>
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
      <webElementGuid>hdr-content-type-delete</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.0.0</katalonVersion>
   <migratedToApiPlugin>false</migratedToApiPlugin>
   <restRequestMethod>DELETE</restRequestMethod>
   <restUrl>https://reqres.in/api/users/2</restUrl>
   <serviceType>RESTful</serviceType>
   <useConfiguration>false</useConfiguration>
   <verificationScript>import static org.assertj.core.api.Assertions.*

assertThat(response.getStatusCode()).isEqualTo(204)
</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
