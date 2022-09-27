package com.der5101.assignment4.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.der5101.assignment4.model.Actor;

/*
 * Service layer for sparql client requests. The clients in the methods could be further
 * refactored to single client class we instantiate per method; for iteration speed
 * cloned the original standalone rdf4j .util package clients used for command-line
 * prototypes directly into the methods for sake of development time.
 * 
 * This is where the SPARQL scripts created by Marta and Pauline are wired into
 * an RDF4J java client method per main query methods for webapp in the ISparqlService layer.
 * 
 * The class also contains two utility internal formatting functions which were used
 * during debugging, so can be removed here probably as not using the methods.
 */
@Service
public class SparqlService implements ISparqlService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	// sparql direct query service to get films for actor searched by user
	public List<String> getFilmsByActorSparqlQuery(String q) throws Exception {
		
		// split and format the input query so clean input goes to sparql query on remote endpoints
		String actorName = splitAndFormatUserInput(q);
		
		// tell rdf4j what repository we are going to use for sparql requests
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		
		// initialize the rdf4j repo endpoint
		repo.init();
		
		// get a connection from it as in multithread application other clients may want a connection
		RepositoryConnection conn = repo.getConnection();

		// placeholder for movielist for actor being queried in sparql request
		List<String> movieList = new ArrayList<String>();

		// try to get a connection as we may fail to remote endpoint so need to catch any failed connection attempt
		try {
			// specify the query string object
			StringBuilder qb = new StringBuilder();
			
			// add the prefixes specified in team sparql scripts
			qb.append("PREFIX dbo: <http://dbpedia.org/ontology/> \n");
			qb.append("PREFIX dbr: <http://dbpedia.org/resource/> \n");
			qb.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n");
			
			// select the films based on dynamic actorName input entity so we only fetch films for that actor
			qb.append("SELECT ?film \n");
			qb.append("WHERE {?film rdf:type dbo:Film . ?film dbo:starring dbr:" + actorName + " .} \n");

			// sparql select returns a tuplequeryresult; this is from rdf4j documentation
			// Web Reference: http://docs.rdf4j.org/programming/#_evaluating_a_tuple_query
			// below code initializes result variable, preps connection, converts query to a string
			// and calls evaluate method
			TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate();

			// from rdf4j programming guide documentation; this loops over resultset as long as it has values
			while (result.hasNext()) {
				// bindingset is an rdf4j data structure to handle the resultset, which can be iterated over
				BindingSet bs = result.next();
				// value is an rdf4j data structure that lets us get at the rdf value in resultset
				Value film = bs.getValue("film");
				
				// log if issues here only at debug level
				log.debug("film = " + film.stringValue());
				
				// to get the string value and not another format unknown to our local data structures, we
				// have to call .stringValue() method to extract the actual data contents into a string format
				// from the sparql query response
				movieList.add(film.stringValue());
			}
		}
		 // close connection so we don't leak resources per rdf4j guidelines
		finally
		{
			conn.close();
		}

		// log output here to check we are returning valid data back to Controller from service layer
		log.info("Movie List for Actor Search Response is: " + movieList.toString());
		return movieList;
	}

	// federated sparql service to obtain the biography and awards information for actor retrieved earlier by user
	@Override
	public Actor getBiographyAndAwardsByActorSparqlQuery(String actorInput) throws Exception {
		
		// same as before specify repository: this time the main one is wikidata NOT debpedia as
		// this will be a federated sparql query
		Repository repo = new SPARQLRepository("https://query.wikidata.org/sparql");
		repo.init();
		RepositoryConnection conn = repo.getConnection();
		
		// the sparql script from team has quite a bit of character escaping so while below can probably
		// be a cleaner it was done late at night and allowed me append required string format to still
		// allow dynamic variable for actorFilter rather than single fixed actor we originally envisaged.
		String escapeChars = "(\"";
		String actorName = actorInput;
		String actorFilter = escapeChars.concat(actorName);
		
	    // set up a simple actor object as we are going to add biography and awards to this object and
		// return it back ultimately to ClientController, so it can get rendered in the image.html template
	    Actor actor = new Actor();
		List<String> awardLabels = new ArrayList<String>();
		List<String> awardWorks = new ArrayList<String>();
		
		// 1. we fetch from wikidata AND 
		// 2. we fetch biography data from dbpedia
		// This is more to demonstrate federated request/response; in real case, we may not have a need for biography
		// unless we are making a further request to only get 1 award of interest later and still obtain biography for actor.
		try {
		    StringBuilder qb = new StringBuilder();
		    
		    // set up the prefixes, so as root repo sparql endpoint is wikidata not dbpedia and we will add
		    // a SERVICE here to retrieve the secondary information from dbpedia endpoint
		    // NOTE: this will always return awards for tom hanks in prototype, so wd:Q2263 has to be dynamic.
		    // Biography will also be specific to actor as that query fires dynamically according to actorFilter
		    // using SERVICE endpoint to dbpedia
		    qb.append("PREFIX dbo: <http://dbpedia.org/ontology/> \n");    
		    qb.append("PREFIX dbr: <http://dbpedia.org/resource/> \n");
		    qb.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n");
			// use DISTINCT to avoid duplicate records coming back
		    qb.append("SELECT DISTINCT ?awardLabel ?awardWorkLabel ?biography \n" +
	                "WHERE\n" +
	                "{\n" +
	                "    				wd:Q2263 p:P166 ?awardStat .\n" + // the wd:Q2263 is hardcoded in this prototype
	                "    				?awardStat ps:P166 ?award .\n" +
	                "  			        ?award wdt:P31 wd:Q19020 .	\n" +
	                "\n" +
	                "\n" +
	                "  ?awardStat pq:P1686 ?awardWork . # The work they have been awarded for\n" +
	                "  	SERVICE wikibase:label {        # ... include the labels\n" +
	                "		bd:serviceParam wikibase:language \"en\" .\n" +
	                "	}	\n" +
	                "BIND(IRI(CONCAT(\"http://dbpedia.org/resource/\",REPLACE" + actorFilter + "\",\" \",\"_\"))) as ?actor) \n" +
	                "SERVICE <http://dbpedia.org/sparql>{ \n" +
	                "?actor dbo:abstract ?biography . FILTER (LANG(?biography)='en')} \n" +
	                "}");

		    // as before, rdf4j tuplequeryresult data structure to handle sparql response result data
		    TupleQueryResult result = 
		         conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
		    
		    // loop over the response: this time we populate a few variables into the actor object
		    // awardlabels, awardworks, and biography are all extracted from the tuplequeryresult
		    // note the bindingset.getValue string MUST match the sparql query variable names e.g. ?awardLabel
		    while(result.hasNext()) {
		         BindingSet bs = result.next();
		         Value awardWorkLabel = bs.getValue("awardWorkLabel");
		         awardWorks.add(awardWorkLabel.stringValue());
		         System.out.println("awardWorkLabel = " + awardWorkLabel.stringValue());
		         Value awardLabel = bs.getValue("awardLabel");
		         awardLabels.add(awardLabel.stringValue());
		         System.out.println("awardLabel = " + awardLabel.stringValue());
		         Value biography = bs.getValue("biography");
		         System.out.println("biography = " + biography.stringValue());
		         actor.setAwardWorks(awardWorks);
		         actor.setAwardLabels(awardLabels);
		         actor.setBiography(biography.stringValue());
		    }
		 }
		 // close connection so we don't leak resources per rdf4j guidelines
		 finally {
		    conn.close();
		 }
		
		// send actor variable back to the ClientController
		return actor;
	}

	// sparql service to obtain the image property (if any) so we can load it as part of actor profile view
	@Override
	public String getImageLinkByActorSparqlQuery(String actorInput) throws Exception {

		// initialize sparql repository as endpoint for rdf4j client - wikidata so we fetch image
		Repository repo = new SPARQLRepository("https://query.wikidata.org/sparql");
		repo.init();
		RepositoryConnection conn = repo.getConnection();
		
		// rdf image property; we get this back from sparql query and use it in UI template to render image
		String actorImageLink = "";
		
		// utility for allowing for dynamic actorFilter again so we can extend web app to search any actor
		String escapeChars = "\"";
		String actorName = actorInput;
		String actorFilter = escapeChars.concat(actorName);
		
		// prepare query string and specify actor dynamically based on user input
		try {
		    StringBuilder qb = new StringBuilder();
		    qb.append("SELECT ?image\n" +
		    		"WHERE {\n" +
		    		"?actor wdt:P106 wd:Q10800557 . \n" +
		    		"?actor rdfs:label" + actorFilter + "\"@en .\n" +
		    		"?actor wdt:P18 ?image .\n" +
		    		"}");

		    // configure rdf4j tuplequeryresult as normal to parse sparql query response
		    TupleQueryResult result = 
		         conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 

		    // get the .jpg image file link via rdf response data in sparql query
		    while(result.hasNext()) {
		         BindingSet bs = result.next();
		         Value imageProperty = bs.getValue("image");
		         System.out.println("imageProperty = " + imageProperty.stringValue());
		         actorImageLink = imageProperty.stringValue();
		    }
		 }
		 // close connection so we don't leak resources per rdf4j guidelines
		 finally {
		    conn.close();
		 }
		
		return actorImageLink;
	}
	
	/*
	 * Utility function for user input queries: this takes a user input string
	 * which is in first name and last name of Actor and splits it, adds an under
	 * score separator and checks firstname and lastname have Uppercase on first
	 * character of firstname and first character of lastname.
	 * 
	 * This should be pushed out of Sparql service layer long term and moved to
	 * general utility classes in util package.
	 */
	public String splitAndFormatUserInput(String inputQuery ) {
		log.info("Input Query String is: " + inputQuery);

		String underscore = "_";
		String splitQuery[] = StringUtils.split(inputQuery);
		String formattedQuery = "";
		
		// handle capitalization of first letter of firstname and lastname; user should not care in UI layer
		String uppercaseFirstName = splitQuery[0].substring(0, 1).toUpperCase() + splitQuery[0].substring(1);
		String uppercaseLastName = splitQuery[1].substring(0, 1).toUpperCase() + splitQuery[1].substring(1);
		formattedQuery = uppercaseFirstName.concat(underscore).concat(uppercaseLastName);
		
		// log output to console to keep track of all test cases
		log.info("Formatted Query String is: " + formattedQuery);
		
		return formattedQuery;
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// The following methods were used during debugging but can be removed. //
	//
	///////////////////////////////////////////////////////////////////////////	
		
	/*
	 * Formatting function to retrieve only first name.
	 */
	public String getFormattedFirstName(String inputQuery ) {
		log.info("Input Query String is: " + inputQuery);

		String splitQuery[] = StringUtils.split(inputQuery);
		String formattedQuery = "";
		
		// handle capitalization of first letter of firstname, first letter
		String uppercaseFirstName = splitQuery[0].substring(0, 1).toUpperCase() + splitQuery[0].substring(1);
		formattedQuery = uppercaseFirstName;
		
		// log output to console to keep track of all test cases
		log.info("Formatted Query String is: " + formattedQuery);
		
		return formattedQuery;
	}
	
	/*
	 * Formatting function to retrieve only last name.
	 */
	public String getFormattedLastName(String inputQuery ) {
		log.info("Input Query String is: " + inputQuery);

		String splitQuery[] = StringUtils.split(inputQuery);
		String formattedQuery = "";
		
		// handle capitalization of lastname, first letter
		String uppercaseLastName = splitQuery[1].substring(0, 1).toUpperCase() + splitQuery[1].substring(1);
		formattedQuery = uppercaseLastName;
		
		// log output to console to keep track of all test cases
		log.info("Formatted Query String is: " + formattedQuery);
		
		return formattedQuery;
	}
	
}
