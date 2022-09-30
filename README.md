# linked-data-movie-db
Data analytics group project using RDF, SPARQL, Wikidata and DBpedia online resources for querying actors and films.

This was a team project where my classmates Marta and Pauline from the MSc in Data Analytics course module - Linked Data - researched and developed the SPARQL scripts for querying the DBpedia and Wikidata online databases to allow us query actors and get attributes like their movies and awards and include federated queries. This had to be integrated into a functional web application.

My task was to integrate the team SPARQL query scripts into a functioning Java application that could fire the query requests and process the online database responses from external RDF databases. I re-used a template scaffold from my prior vendelligence project using Spring Boot, Thymeleaf, and Bootstrap for the MVC framework. For the RDF and SPARQL module components, I researched and choose the RDF4J client to process the SPARQL scripts from my team colleagues. The SPARQL queries provided by the team were adapted into standard Java business service logic to allow for future extensions.

This application includes the standalone utility clients for individual test cases which can run as individual Java programs and the main Spring Boot - RDF4J - SPARQL integrated web application.

To run it:
- Import the project into Eclipse IDE via Git import and choose Maven project with Smart Import. 
- Build the Maven project and run it as a Maven Build configuration with Goals defined as "spring-boot:run".
- Open localhost:8080/client and search for e.g. Audrey Hepburn.

Resources
- https://www.wikidata.org/wiki/Wikidata:Main_Page
- https://www.dbpedia.org/
- https://rdf4j.org/
- https://www.w3.org/standards/semanticweb/data
- https://www.w3.org/TR/sparql11-query/
- https://www.w3.org/TR/rdf11-concepts/
