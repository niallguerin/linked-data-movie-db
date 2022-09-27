package com.der5101.assignment4.util;

/*
/*
 * Author: Marta, Pauline, Niall
 *
 * Single sparql query based on sparql script prototoype from Marta, Pauline.
 * 
 * This is a Wikidata client, not dbepedia.
 * 
 * This one is based on examples from wikidata to see if I can translate them to a federated query
 * in our rdf4j framework before trying to implement our own custom federated query to dbpedia
 * and wikidata.
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

public class WikidataFederationClient {
	
	public static void main (String args []) {
		Repository repo = new SPARQLRepository("https://query.wikidata.org/sparql");
		repo.init();
		RepositoryConnection conn = repo.getConnection();
		
		try {
		    StringBuilder qb = new StringBuilder();
//		    qb.append("PREFIX wdt: <http://www.wikidata.org/prop/direct/>");
//		    qb.append("PREFIX wd: <http://www.wikidata.org/entity/>");
//		    qb.append("PREFIX wikibase: <https://www.wikidata.org/ontology#>");
		    qb.append("SELECT ?workLabel WHERE { \n" +
		    "  wd:Q165257 wdt:P2799 ?id \n" +
		    "  BIND(uri(concat(\"http://data.cervantesvirtual.com/person/\", ?id)) as ?bvmcID) \n" +
		    "  SERVICE <http://data.cervantesvirtual.com/openrdf-sesame/repositories/data>{ \n" +
		    "  ?bvmcID <http://rdaregistry.info/Elements/a/otherPFCManifestationOf> ?work .\n" +
		    "  ?work rdfs:label ?workLabel \n" +
		    "  } \n" +
		    "}");


		    TupleQueryResult result = 
		         conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 

		    while(result.hasNext()) {
		         BindingSet bs = result.next();
		         Value workLabel = bs.getValue("workLabel");
		         System.out.println("workLabel = " + workLabel.stringValue());

		    }
		 }
		 finally {
		    conn.close();
		 }
	}
}