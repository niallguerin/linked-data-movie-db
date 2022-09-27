package com.der5101.assignment4.util;

/*
 * Author: Niall, Mara, Pauline.
 *
 * Single sparql query based on sparql script prototoype from Marta, Pauline.
 * 
 * This is a Wikidata client, not dbepedia.
 * 
 * This retrieves, award, awardWorks(films) and biography as a custom federated query modelled
 * on original raw sparql script from team. Testing it in isolation first as standalone client
 * before integrating as method into the SparqlService layer of web app. 
 * 
 */

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class WikidataFederationClientActorBioAwards {
	
	public static void main (String args []) {
		Repository repo = new SPARQLRepository("https://query.wikidata.org/sparql");
		repo.init();
		RepositoryConnection conn = repo.getConnection();
		
		// use DISTINCT to avoid duplicate records coming back here on the FEDERATED QUERY; we fetch from wikidata AND dbpedia here
		try {
		    StringBuilder qb = new StringBuilder();
		    qb.append("PREFIX dbo: <http://dbpedia.org/ontology/> \n");    
		    qb.append("PREFIX dbr: <http://dbpedia.org/resource/> \n");
		    qb.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n");
		    qb.append("SELECT DISTINCT ?awardLabel ?awardWorkLabel ?biography \n" +
	                "WHERE\n" +
	                "{\n" +
	                "    				wd:Q2263 p:P166 ?awardStat .\n" +
	                "    				?awardStat ps:P166 ?award .\n" +
	                "  			        ?award wdt:P31 wd:Q19020 .	\n" +
	                "\n" +
	                "\n" +
	                "  ?awardStat pq:P1686 ?awardWork . # The work they have been awarded for\n" +
	                "  	SERVICE wikibase:label {            # ... include the labels\n" +
	                "		bd:serviceParam wikibase:language \"en\" .\n" +
	                "	}	\n" +
	                "BIND(IRI(CONCAT(\"http://dbpedia.org/resource/\",REPLACE(\"Tom Hanks\",\" \",\"_\"))) as ?actor) \n" +
	                "SERVICE <http://dbpedia.org/sparql>{ \n" +
	                "?actor dbo:abstract ?biography . FILTER (LANG(?biography)='en')} \n" +
	                "}");


		    TupleQueryResult result = 
		         conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 

		    while(result.hasNext()) {
		         BindingSet bs = result.next();
		         Value awardWorkLabel = bs.getValue("awardWorkLabel");
		         System.out.println("awardWorkLabel = " + awardWorkLabel.stringValue());
		         Value awardLabel = bs.getValue("awardLabel");
		         System.out.println("awardLabel = " + awardLabel.stringValue());
		         Value biography = bs.getValue("biography");
		         System.out.println("biography = " + biography.stringValue());
		    }
		 }
		 finally {
		    conn.close();
		 }
	}
}