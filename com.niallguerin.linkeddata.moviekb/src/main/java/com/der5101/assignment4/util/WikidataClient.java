package com.der5101.assignment4.util;

/*
 * Author: Niall based on SPARQL script provided by Marta and Pauline.
 *
 * Single sparql query based on sparql script prototoype from Marta, Pauline.
 * 
 * This is a Wikidata client, not dbpedia.
 * 
 * This retrieves, award, awardWorks(films) and dumps them to the console in the main driver program.
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

public class WikidataClient {
	
	public static void main (String args []) {
		Repository repo = new SPARQLRepository("https://query.wikidata.org/sparql");
		repo.init();
		RepositoryConnection conn = repo.getConnection();
		
		try {
		    StringBuilder qb = new StringBuilder();
//		    qb.append("PREFIX wdt: <http://www.wikidata.org/prop/direct/>");
//		    qb.append("PREFIX wd: <http://www.wikidata.org/entity/>");
//		    qb.append("PREFIX wikibase: <https://www.wikidata.org/ontology#>");
		    qb.append("SELECT ?awardLabel ?awardWorkLabel \n" +
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
	                "}");


		    TupleQueryResult result = 
		         conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 

		    while(result.hasNext()) {
		         BindingSet bs = result.next();
		         Value awardLabel = bs.getValue("awardLabel");
		         Value awardWorkLabel = bs.getValue("awardWorkLabel");
		         System.out.println("awardLabel = " + awardLabel.stringValue());
		         System.out.println("awardWorkLabel = " + awardWorkLabel.stringValue());

		    }
		 }
		 finally {
		    conn.close();
		 }
	}
}