package com.der5101.assignment4.controller;

// standard java imports for data structures
import java.util.ArrayList;
import java.util.List;

// string utility method from apache commons
import org.apache.commons.lang.StringUtils;

// standard java loggers to log using logger instead of system.out statements
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// spring autowiring, controller, and request mapping packages
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.ui.Model;

import com.der5101.assignment4.model.Actor;
import com.der5101.assignment4.service.ISparqlService;

/***
 * 
 * The Controllers function as a go-between for the client user interface UI templates
 * and backend sparql service business logic layers passing parameters and results
 * back and forth based on user actions on the front-end.
 * 
 * The primary function is to map a form submit action e.g. /search
 * to a corresponding method that fires when that button is pressed
 * or user gives user input and triggers the workflow.
 * 
 * The annotation for controller is to ensure Spring Boot internally knows this is standard
 * request/response controller and does not treat it as a rest client behind the scenes
 * as restcontroller will handle response body differently.
 *
 */
@Controller
public class ClientController
{
	/*
	 * Autowiring is a means of Dependency Injection within the application. It basically
	 * lets Spring figure out which collaborating beans to use instead of the classic method
	 * of creating xml configuration files and then configuring spring to use the xml files.
	 * This is a standard template I use in spring projects, so we are just re-using it here for sparql client.
	 */
	@Autowired
	private ISparqlService sparqlService; 
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/*
	 * Main free from actor search input handling method to get initia list of films for actor.
	 * 
	 * Path based on thymeleaf form template at UI layer, so when user click search after entering
	 * Actor search string, this method checks if search was fired, and then inspects the parameter
	 * query string via RequestParam annotation. The Model is used for thymeleaf data model backing
	 * purposes, so that we can use setter/getter methods if needed on the Actor class; a simple
	 * data model class that allows us bind sparql requests/responses to our application domain.
	 */
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchDbpediaRepository(@RequestParam(value="query") String query, Model model)
	{
		// check for bad input by user and redirect home again if empty input to avoid spring boot whitelabel standard errors to UI
		if( query.isEmpty() ) 
		{
			return "client";
		}
		else 
		{
			// try and get the results of any sparql request by calling sparql business logic service layer
			List<String> results = new ArrayList<String>();
			try
			{
				results = sparqlService.getFilmsByActorSparqlQuery(query);
				log.info(results.toString());
			} 
			catch (Exception e)
			{
				log.info("Error calling getFilmsByActor method:" + e);
			}
		
			// initialize actor information based on query: this just gets actor first name and lastname
			String actorFirstName = getFormattedFirstName(query);
			String actorLastName = getFormattedLastName(query);

			/*
			 * Bind the query data model to the controller so create a simple actor data model class.
			 * Set the first and last name from the input query from user. Set the list of films based
			 * on the response from the sparql query in service layer.
			 */
			Actor actor = new Actor();
			actor.setFirstName(actorFirstName);
			actor.setLastName(actorLastName);
			actor.setFilms(results);
	    
			/*
			 * add it to the model and send user to result.html template: from there, they can load actor
			 * profiles if they wish later during data enrichment step to new sparql query. 
			 */
			model.addAttribute("actor", actor);

			// this just tells web app which view to call inside src/main/resources/templates directory
			return "result";
		}
	}
	
	/*
	 * This maps to data enrichment option for loading actor profile information: this method
	 * calls a federated sparql query in the service layer which retrieves actor biography, and
	 * actor award information. In the prototype, we just have awards for Tom Hanks as wikidata
	 * parameter was fixed in sparql query but this can be extended if needed. The Thymeleaf UI
	 * template will do a simple check and if Tom Hanks, displays awards, otherwise it hides them
	 * from the template.
	 * 
	 * In addition, this method calls another sparql (non-federated) query at the service layer
	 * that retrieves the RDF data for the image for an actor. We use this in the Thymeleaf template
	 * UI to dynamically update the image for a given actor. No image files themselves are ever
	 * stored locally; we simply update the img tag in the Thymeleaf template based on sparql
	 * response and if an image link is returned via sparql request we use it in the image.html
	 * Thymeleaf template UI view.
	 */
	@RequestMapping(value="/loadprofile", method=RequestMethod.GET)
	public String searchActorImage(@RequestParam(value="firstName") String firstName, @RequestParam(value="lastName") String lastName, Model model)
	{
		/*
		 * The following code is just for handling space as some sparql queries expect actorFirstName_lastName
		 * like the previous Controller method, but this one expects actorFirstName + space + actorLastName.
		 */
		String SPACE = " ";
		String query = firstName
				.concat(SPACE)
				.concat(lastName);
		String imageLink = "";
		
		// we get the image link first; calls service layer which uses NON-federated query
		try
		{
			imageLink = sparqlService.getImageLinkByActorSparqlQuery(query);
			log.info(imageLink);
		} 
		catch (Exception e)
		{
			log.info("Error calling searchActorImage method:" + e);
		}
		
		// bind the query data model to the controller
    	Actor actor = new Actor();
		
    	// fetch the awards and biography data from federated backend sparql service layer: this
    	// uses a Federated query primarily for demo and practice purposes for ourselves; we could
    	// use other simple federated query like in our lecture slides e.g. get actor name in one
    	// place and date of birth in another so a non-list entity is returned instead. This returns
    	// a list of awards like labels, film they got the award for, and biography per line returned.
		try
		{
			// initialize actor based on data returned from sparql service
			actor = sparqlService.getBiographyAndAwardsByActorSparqlQuery(query);
		}
		catch (Exception e)
		{
			log.info("Error calling getBiographyAndAwardsByActorSparqlQuery method:" + e);
		}
		
		// load up the actor data from our sparql services RDF responses and update our actor class
	    actor.setFirstName(firstName);
	    actor.setLastName(lastName);
	    actor.setImageLink(imageLink);
	    actor.setAwardLabels(actor.getAwardLabels());
	    actor.setAwardWorks(actor.getAwardWorks());
	    
	    // add it to the model and send user to image.html template
	    model.addAttribute("actor", actor);
	    
	    // loggers for debugging
	    log.info(actor.getFirstName());
	    log.info(actor.getAwardWorks().toString());

	    // send user to image.html which contains bio, photo, and awards information
		return "image";
	}
	
	/*
	 * Formatting function for user input queries: this takes a user input string
	 * which is in first name and last name of Actor and splits it, adds an under
	 * score separator and checks firstname and lastname have Uppercase on first
	 * character of firstname and first character of lastname.
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
