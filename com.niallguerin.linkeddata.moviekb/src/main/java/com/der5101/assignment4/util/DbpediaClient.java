package com.der5101.assignment4.util;

/*
 * Author: Niall Guerin based on SPARQL scripts provided by Marta and Pauline.
 *
 * Based on tom_hanks single actor, get list of films sparql script from Marta, Pauline.
 * 
 * This retrieves list of films for Tom Hanks and dumps them to the console in the main driver program.
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

public class DbpediaClient {
	
	public static void main (String args []) {
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.init();
		RepositoryConnection conn = repo.getConnection();
		
		try {
		    StringBuilder qb = new StringBuilder();
		    qb.append("PREFIX dbo: <http://dbpedia.org/ontology/> \n");    
		    qb.append("PREFIX dbr: <http://dbpedia.org/resource/> \n");
		    qb.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n");
		    qb.append("SELECT ?biography \n");
		    qb.append("WHERE {BIND(IRI(CONCAT(\"http://dbpedia.org/resource/\",REPLACE(\"Tom Hanks\",\" \",\"_\"))) as ?actor) { ?actor dbo:abstract ?biography . FILTER (LANG(?biography)='en')}} \n");

		    TupleQueryResult result = 
		         conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 

		    while(result.hasNext()) {
		         BindingSet bs = result.next();
		         Value biography = bs.getValue("biography");
		         System.out.println("biography = " + biography.stringValue());
		    }
		 }
		 finally {
		    conn.close();
		 }
	}
}