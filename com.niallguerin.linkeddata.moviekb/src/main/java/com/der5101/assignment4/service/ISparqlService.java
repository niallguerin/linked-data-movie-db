package com.der5101.assignment4.service;

import java.util.List;

import com.der5101.assignment4.model.Actor;

/*
 * Interface for listing methods to be used by sparql client service layer. This
 * has three main methods mapping to the three main tasks of the sparql client web app:
 * 1. Get a list of films for a given actor (individual sparql query).
 * 2. Get biography and awards for a given actor (federated sparql query).
 * 3. Get image data for actor by fetching link (individual sparql query).
 */
public interface ISparqlService {
	// Individual query to remote dbpedia endpoint based on user input query
	List<String> getFilmsByActorSparqlQuery(String query) throws Exception;
	
	// Federated  query to both dbpedia and wikidata endpoints based on user linked-data expansion query
	Actor getBiographyAndAwardsByActorSparqlQuery(String actor) throws Exception;
	
	// Get image query to remote dbpedia endpoint as data enrichment step for actor profile screen
	String getImageLinkByActorSparqlQuery(String query) throws Exception;
}
