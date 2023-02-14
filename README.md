# Library REST 2023

### The project from 2016 refactored as a REST API and updated to the following versions of:
  - Java 19,
  - Spring Boot 2.5.14,
  - Spring Security 5.5.8,
  - Spring MVC 5.3.2,
  - Spring Data JPA 2.5.14,
  - SpringFox Swagger 2.9.2,
  - JUnit 5.7.2,
  - Gradle 8.0
  
### Prerequisites
    - Java 19 JRE or JDK installed
    - MySQL installed with created schema 'library' or PostgreSQL, if not - the application can be started
     with built-in H2 in-memory DB. You can use any DB you want, but the corresponding dependency must be
     added to 'build.gradle'.
        
### Environment variables

By default, the H2 in-memory DB will be used.
The application rely on environment variables in order to run with DB of your choice.
These need to be set up in your environment or in the Run Configuration of your IDE:

    - LIBRARY.DB.URL (default is 'sa')
    - LIBRARY.DB.USERNAME (default is 'sa')
    - LIBRARY.DB.PASSWORD - examples: H2: 'jdbc:h2:mem:test' (default),
                                 MySQL: 'jdbc:mysql://localhost/library',
                                 PostgreSQL: 'jdbc:postgresql://localhost:5432/postgres'
    - LIBRARY.DDL (default is 'create')
  
### Application can be expanded without recompiling

The application ability to export data as a file can be expanded. Any implementation of Exporter interface 
(see package 'exporter') can be placed as a compiled class in the 'pluginClasses' directory, whose location
is defined in application.properties and will be loaded via classloader when the application is started.
For the moment 'pluginClasses' contains XmlFileExporter.class.

### Swagger UI

When running locally the Swagger UI is available on http://localhost:8080/library/api/swagger-ui.html

### Security

    When starting the application, the database table 'users' will be populated with 2 records -> 
    see 'data.sql' in 'resources' directory.
    All endpoints are secured. Access is granted with Basic Authentication to: 
    - user: admin, password: admin (ROLE_ADMIN)
    - user: user, password: user (ROLE_USER)

    Users with ROLE_ADMIN can use all endpoints. Users with ROLE_USER cannot access '/users' endpoint, 
    delete and export operations. 
    
### ETags

    The API supports shallow ETags on GET requests. The value of response ETag header can be set to 
    request header 'If-None-Match' value. If the requested resource is not modified the response status
    will be: 304 Not Modified and no response body will be present.
  

  
  
  
 
