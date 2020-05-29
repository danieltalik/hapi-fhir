# HealthCare Data Interchange
This repo contains 3 projects demonstrating simple usage of HAPI FHIR for data interchange between systems.
1. HDI-HUB: HAPI FHIR Server.
2. PMS: HAPI FHIR Client sends patient data to HDI-HUB.
3. HAPI-FHIR-UI: Simple Angular app consumes patient data from HDI-HUB and diaplay it.

![HDI Architecture](HDI.jpg?raw=true "HDI")



# Installation
1. 'mvn clean install' at hdi-hub/
2. 'mvn clean install' at pms/
3. 'npm install' at hapi-fhir-ui/

# Run
1. 'mvn jetty:run' at hdi-hub/
2. 'mvn spring-boot:run' at pms/
3. 'npm start' at hapi-fhir-ui/
4.  http://localhost:4200
