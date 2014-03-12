oauth
=====

Defined segregate  "provider" and "resource" servers using Spring Oauth. The servers are integrated via a derby database. 

Prerequisites:

Setup and run a networked derby database on port 50000:
java -jar $DERBY_HOME/lib/derbynet.jar start -p 50000

Instantiate schemas and populate the database with sample data by executing 'mvn compile flyway:migrate' from within the oauth-provider project

The resource project consists of an unsecured example web service (mapped to /message/{id}) as well as a secure proxy service (mapped to /secure/message). The proxy service is a camel servlet component configured in applicationContext.xml; its security wrapper is configured in oauth-security.xml.

The oauth project is a realization of the spring oauth provider implementation with the addition of two custom extensions. The first customization is a service which allows for the direct creation of authorization codes without having to issue redirects to the end user (ala Hulu Plus on the Roku); the second customization adds customer ids to the output of the token service.

