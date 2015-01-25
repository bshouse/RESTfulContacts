# RESTfulContacts
A simple REST application for basic contact management

## Framework Configuration
* Stripes Framework
* JQuery
* Jetty
* Hibernate
* HSQLDB


## Build/Compile
ant build

## Run Test Database
ant TestServer

## Run Production Database
ant ProdServer

## Web-based Client
1. Start either the Test or Production server
2. Launch you web browser and go to- http://localhost:8080/

## Run Server Tests
1. Start the Test Server- ant TestServer 
2. Start the JUnit tests- ant ContactApiTest

(Note: All 'ant' commands should be run where the build.xml file is located.)