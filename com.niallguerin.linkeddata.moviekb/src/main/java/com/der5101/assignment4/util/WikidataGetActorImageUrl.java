package com.der5101.assignment4.util;

/*
 * Author: Marta, Pauline, Niall
 *
 * Single sparql query based on sparql script prototoype from Marta, Pauline.
 * 
 * This is a wikidata client and retrieves images for the actor. It is fixed on
 * Tom Hanks, but later service layer refactors that to use dynamic variable filter
 * for actor so we can query for any actor in the web app client. Sample debug console
 * output added for own reference at end for troubleshooting service layer.
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

public class WikidataGetActorImageUrl {
	
	public static void main (String args []) {
		Repository repo = new SPARQLRepository("https://query.wikidata.org/sparql");
		repo.init();
		RepositoryConnection conn = repo.getConnection();
		
		try {
		    StringBuilder qb = new StringBuilder();
//		    qb.append("PREFIX wdt: <http://www.wikidata.org/prop/direct/>");
//		    qb.append("PREFIX wd: <http://www.wikidata.org/entity/>");
//		    qb.append("PREFIX wikibase: <https://www.wikidata.org/ontology#>");
		    qb.append("SELECT ?image\n" +
		    		"WHERE {\n" +
		    		"?actor wdt:P106 wd:Q10800557 . \n" +
		    		"?actor rdfs:label \"Tom Hanks\"@en .\n" +
		    		"?actor wdt:P18 ?image .\n" +
		    		"}");

		    TupleQueryResult result = 
		         conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 

		    while(result.hasNext()) {
		         BindingSet bs = result.next();
		         Value imageProperty = bs.getValue("image");
		         System.out.println("imageProperty = " + imageProperty.stringValue());

		    }
		 }
		 finally {
		    conn.close();
		 }
	}
}

//Response sample from console: line 171 comment shows required link and full URL so we can embed it in UI
//	13:24:05.836 [main] DEBUG o.e.r.rio.DatatypeHandlerRegistry - Registered service class org.eclipse.rdf4j.rio.datatypes.XMLSchemaDatatypeHandler
//	13:24:05.841 [main] DEBUG o.e.r.rio.DatatypeHandlerRegistry - Registered service class org.eclipse.rdf4j.rio.datatypes.RDFDatatypeHandler
//	13:24:05.842 [main] DEBUG o.e.r.rio.DatatypeHandlerRegistry - Registered service class org.eclipse.rdf4j.rio.datatypes.DBPediaDatatypeHandler
//	13:24:05.842 [main] DEBUG o.e.r.rio.DatatypeHandlerRegistry - Registered service class org.eclipse.rdf4j.rio.datatypes.VirtuosoGeometryDatatypeHandler
//	13:24:05.843 [main] DEBUG o.e.r.rio.DatatypeHandlerRegistry - Registered service class org.eclipse.rdf4j.rio.datatypes.GeoSPARQLDatatypeHandler
//	13:24:05.845 [main] DEBUG o.e.r.rio.LanguageHandlerRegistry - Registered service class org.eclipse.rdf4j.rio.languages.RFC3066LanguageHandler
//	13:24:05.847 [main] DEBUG o.e.r.rio.LanguageHandlerRegistry - Registered service class org.eclipse.rdf4j.rio.languages.BCP47LanguageHandler
//	13:24:05.864 [main] DEBUG o.e.r.q.r.TupleQueryResultParserRegistry - Registered service class org.eclipse.rdf4j.query.resultio.binary.BinaryQueryResultParserFactory
//	13:24:05.864 [main] DEBUG o.e.r.q.r.TupleQueryResultParserRegistry - Registered service class org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONParserFactory
//	13:24:05.865 [main] DEBUG o.e.r.q.r.TupleQueryResultParserRegistry - Registered service class org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLParserFactory
//	13:24:05.866 [main] DEBUG o.e.r.q.r.TupleQueryResultParserRegistry - Registered service class org.eclipse.rdf4j.query.resultio.text.csv.SPARQLResultsCSVParserFactory
//	13:24:05.867 [main] DEBUG o.e.r.q.r.TupleQueryResultParserRegistry - Registered service class org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVParserFactory
//	13:24:05.883 [main] DEBUG o.a.h.c.protocol.RequestAddCookies - CookieSpec selected: default
//	13:24:05.896 [main] DEBUG o.a.h.c.protocol.RequestAuthCache - Auth cache not set in the context
//	13:24:05.897 [main] DEBUG o.a.h.i.c.PoolingHttpClientConnectionManager - Connection request: [route: {s}->https://query.wikidata.org:443][total kept alive: 0; route allocated: 0 of 5; total allocated: 0 of 10]
//	13:24:05.916 [main] DEBUG o.a.h.i.c.PoolingHttpClientConnectionManager - Connection leased: [id: 0][route: {s}->https://query.wikidata.org:443][total kept alive: 0; route allocated: 1 of 5; total allocated: 1 of 10]
//	13:24:05.917 [main] DEBUG o.a.h.impl.execchain.MainClientExec - Opening connection {s}->https://query.wikidata.org:443
//	13:24:05.929 [main] DEBUG o.a.h.i.c.DefaultHttpClientConnectionOperator - Connecting to query.wikidata.org/91.198.174.192:443
//	13:24:05.929 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory - Connecting socket to query.wikidata.org/91.198.174.192:443 with timeout 0
//	13:24:06.034 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory - Enabled protocols: [TLSv1, TLSv1.1, TLSv1.2]
//	13:24:06.034 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory - Enabled cipher suites:[TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384, TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384, TLS_RSA_WITH_AES_256_CBC_SHA256, TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384, TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384, TLS_DHE_RSA_WITH_AES_256_CBC_SHA256, TLS_DHE_DSS_WITH_AES_256_CBC_SHA256, TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA, TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA, TLS_RSA_WITH_AES_256_CBC_SHA, TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA, TLS_ECDH_RSA_WITH_AES_256_CBC_SHA, TLS_DHE_RSA_WITH_AES_256_CBC_SHA, TLS_DHE_DSS_WITH_AES_256_CBC_SHA, TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256, TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256, TLS_RSA_WITH_AES_128_CBC_SHA256, TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256, TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256, TLS_DHE_RSA_WITH_AES_128_CBC_SHA256, TLS_DHE_DSS_WITH_AES_128_CBC_SHA256, TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA, TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, TLS_RSA_WITH_AES_128_CBC_SHA, TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA, TLS_ECDH_RSA_WITH_AES_128_CBC_SHA, TLS_DHE_RSA_WITH_AES_128_CBC_SHA, TLS_DHE_DSS_WITH_AES_128_CBC_SHA, TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384, TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, TLS_RSA_WITH_AES_256_GCM_SHA384, TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384, TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384, TLS_DHE_RSA_WITH_AES_256_GCM_SHA384, TLS_DHE_DSS_WITH_AES_256_GCM_SHA384, TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, TLS_RSA_WITH_AES_128_GCM_SHA256, TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256, TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256, TLS_DHE_RSA_WITH_AES_128_GCM_SHA256, TLS_DHE_DSS_WITH_AES_128_GCM_SHA256, TLS_EMPTY_RENEGOTIATION_INFO_SCSV]
//	13:24:06.034 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory - Starting handshake
//	13:24:06.170 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory - Secure session established
//	13:24:06.170 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory -  negotiated protocol: TLSv1.2
//	13:24:06.170 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory -  negotiated cipher suite: TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384
//	13:24:06.170 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory -  peer principal: CN=*.wikipedia.org, O="Wikimedia Foundation, Inc.", L=San Francisco, ST=California, C=US
//	13:24:06.171 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory -  peer alternative names: [*.wikipedia.org, wikimedia.org, mediawiki.org, wikibooks.org, wikidata.org, wikinews.org, wikiquote.org, wikisource.org, wikiversity.org, wikivoyage.org, wiktionary.org, wikimediafoundation.org, w.wiki, wmfusercontent.org, *.m.wikipedia.org, *.wikimedia.org, *.m.wikimedia.org, *.planet.wikimedia.org, *.mediawiki.org, *.m.mediawiki.org, *.wikibooks.org, *.m.wikibooks.org, *.wikidata.org, *.m.wikidata.org, *.wikinews.org, *.m.wikinews.org, *.wikiquote.org, *.m.wikiquote.org, *.wikisource.org, *.m.wikisource.org, *.wikiversity.org, *.m.wikiversity.org, *.wikivoyage.org, *.m.wikivoyage.org, *.wiktionary.org, *.m.wiktionary.org, *.wikimediafoundation.org, *.wmfusercontent.org, wikipedia.org]
//	13:24:06.171 [main] DEBUG o.a.h.c.s.SSLConnectionSocketFactory -  issuer principal: CN=GlobalSign Organization Validation CA - SHA256 - G2, O=GlobalSign nv-sa, C=BE
//	13:24:06.175 [main] DEBUG o.a.h.i.c.DefaultHttpClientConnectionOperator - Connection established 10.209.25.124:65473<->91.198.174.192:443
//	13:24:06.176 [main] DEBUG o.a.h.impl.execchain.MainClientExec - Executing request GET /sparql?query=SELECT+%3Fimage%0AWHERE+%7B%0A%3Factor+wdt%3AP106+wd%3AQ10800557+.+%0A%3Factor+rdfs%3Alabel+%22Tom+Hanks%22%40en+.%0A%3Factor+wdt%3AP18+%3Fimage+.%0A%7D HTTP/1.1
//	13:24:06.176 [main] DEBUG o.a.h.impl.execchain.MainClientExec - Target auth state: UNCHALLENGED
//	13:24:06.177 [main] DEBUG o.a.h.impl.execchain.MainClientExec - Proxy auth state: UNCHALLENGED
//	13:24:06.179 [main] DEBUG org.apache.http.headers - http-outgoing-0 >> GET /sparql?query=SELECT+%3Fimage%0AWHERE+%7B%0A%3Factor+wdt%3AP106+wd%3AQ10800557+.+%0A%3Factor+rdfs%3Alabel+%22Tom+Hanks%22%40en+.%0A%3Factor+wdt%3AP18+%3Fimage+.%0A%7D HTTP/1.1
//	13:24:06.179 [main] DEBUG org.apache.http.headers - http-outgoing-0 >> Accept: text/csv;q=0.8, application/sparql-results+json;q=0.8, application/json;q=0.8, application/sparql-results+xml, application/xml, text/tab-separated-values;q=0.8, application/x-binary-rdf-results-table;q=0.8
//	13:24:06.179 [main] DEBUG org.apache.http.headers - http-outgoing-0 >> Host: query.wikidata.org
//	13:24:06.179 [main] DEBUG org.apache.http.headers - http-outgoing-0 >> Connection: Keep-Alive
//	13:24:06.179 [main] DEBUG org.apache.http.headers - http-outgoing-0 >> User-Agent: Apache-HttpClient/4.5.1 (Java/1.8.0_181)
//	13:24:06.179 [main] DEBUG org.apache.http.headers - http-outgoing-0 >> Accept-Encoding: gzip,deflate
//	13:24:06.179 [main] DEBUG org.apache.http.wire - http-outgoing-0 >> "GET /sparql?query=SELECT+%3Fimage%0AWHERE+%7B%0A%3Factor+wdt%3AP106+wd%3AQ10800557+.+%0A%3Factor+rdfs%3Alabel+%22Tom+Hanks%22%40en+.%0A%3Factor+wdt%3AP18+%3Fimage+.%0A%7D HTTP/1.1[\r][\n]"
//	13:24:06.179 [main] DEBUG org.apache.http.wire - http-outgoing-0 >> "Accept: text/csv;q=0.8, application/sparql-results+json;q=0.8, application/json;q=0.8, application/sparql-results+xml, application/xml, text/tab-separated-values;q=0.8, application/x-binary-rdf-results-table;q=0.8[\r][\n]"
//	13:24:06.179 [main] DEBUG org.apache.http.wire - http-outgoing-0 >> "Host: query.wikidata.org[\r][\n]"
//	13:24:06.179 [main] DEBUG org.apache.http.wire - http-outgoing-0 >> "Connection: Keep-Alive[\r][\n]"
//	13:24:06.179 [main] DEBUG org.apache.http.wire - http-outgoing-0 >> "User-Agent: Apache-HttpClient/4.5.1 (Java/1.8.0_181)[\r][\n]"
//	13:24:06.179 [main] DEBUG org.apache.http.wire - http-outgoing-0 >> "Accept-Encoding: gzip,deflate[\r][\n]"
//	13:24:06.179 [main] DEBUG org.apache.http.wire - http-outgoing-0 >> "[\r][\n]"
//	13:24:06.579 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "HTTP/1.1 200 OK[\r][\n]"
//	13:24:06.579 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Date: Sat, 13 Apr 2019 12:23:36 GMT[\r][\n]"
//	13:24:06.579 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Content-Type: application/sparql-results+xml;charset=utf-8[\r][\n]"
//	13:24:06.579 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Content-Length: 234[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Connection: keep-alive[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Server: nginx/1.13.6[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "X-Served-By: wdqs1004[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Access-Control-Allow-Origin: *[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Cache-Control: public, max-age=300[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Content-Encoding: gzip[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Vary: Accept, Accept-Encoding[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "X-Varnish: 993432986, 180042328, 179416433[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Via: 1.1 varnish (Varnish/5.1), 1.1 varnish (Varnish/5.1), 1.1 varnish (Varnish/5.1)[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Age: 0[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "X-Cache: cp1075 pass, cp3032 miss, cp3033 miss[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "X-Cache-Status: miss[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Server-Timing: cache;desc="miss"[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Strict-Transport-Security: max-age=106384710; includeSubDomains; preload[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Set-Cookie: WMF-Last-Access=13-Apr-2019;Path=/;HttpOnly;secure;Expires=Wed, 15 May 2019 12:00:00 GMT[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Set-Cookie: WMF-Last-Access-Global=13-Apr-2019;Path=/;Domain=.wikidata.org;HttpOnly;secure;Expires=Wed, 15 May 2019 12:00:00 GMT[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "X-Analytics: https=1;nocookies=1[\r][\n]"
//	13:24:06.580 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "X-Client-IP: 51.171.102.17[\r][\n]"
//	13:24:06.581 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "Accept-Ranges: bytes[\r][\n]"
//	13:24:06.581 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "[\r][\n]"
//	13:24:06.581 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "[0x1f][0x8b][0x8][0x0][0x0][0x0][0x0][0x0][0x0][0x3]U[0x8f][0xcf]N[0xc3]0[0xc][0x87][0xcf][0xed]ST[0xe2][0x90][0xd3][0xe2][0x2]B[0x82])[0xed]n[0x13][0xe2][0x84][0xc4]x[0x0][0xaf][0xb5]Zo[0xf9]S[0x92]n[0xe1][0xf1]i[0xb3][0x80][0xc4])[0xbf][0xc8][0x9f][0xed][0xcf]j[0xf7]mtu%[0x1f][0xd8][0xd9]F[0xdc][0xcb]ZTd;[0xd7][0xb3][0x1d][0x1a][0xf1]y[0xd8]o[0x9e][0xc5][0xae]-U[0x98][0xd0][0xe9]j[0x81]mh[0xc4]8[0xcf][0xd3][0x16] [0xc6]([0xe3][0xa3]t~[0x80][0x87][0xba]~[0x82][0x1b][0xb4][0xf1][0x14].z[0xe]w[0xa2]-[0xb]5[0x12][0xf6][0xcb][[0xa8]+z[0xc6][0xa3][0xa6][0xca][0xa2][0xa1]F[0xb0][0xc1][0x81][0x4][0xac][0x8]dF[0xe5][0xc6][0x84][0xdf][0xf2][0x1a][0xb]ud[0xbb][0xfa][0xfc][0xeb]L[0x95]B]<[0xb7][0xd9][0xa6]s[0xc6]8[0x1b]d[0xe4]3[0x1b][0xea][0x19][0x93][0xd8][0xfa][0x83][0x8f][0x89]:F[0xbd][0xdd][0xb3][0xa6]w[0x9c]G88[0xf3][0x8a][0xf6][0x1c][0xde][0xd0].[0xe6]/[0xf2]4[\r][\n]"
//	13:24:06.581 [main] DEBUG org.apache.http.wire - http-outgoing-0 << "[0xd6]Yi[0x1f][0xe4][0x85]I[0x4][0xfe]L~[0xe3]"[0xa8][0xf2][0xad]m[0xf9][0x3][0xfb]p"YA[0x1][0x0][0x0]"
//	13:24:06.584 [main] DEBUG org.apache.http.headers - http-outgoing-0 << HTTP/1.1 200 OK
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Date: Sat, 13 Apr 2019 12:23:36 GMT
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Content-Type: application/sparql-results+xml;charset=utf-8
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Content-Length: 234
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Connection: keep-alive
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Server: nginx/1.13.6
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << X-Served-By: wdqs1004
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Access-Control-Allow-Origin: *
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Cache-Control: public, max-age=300
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Content-Encoding: gzip
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Vary: Accept, Accept-Encoding
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << X-Varnish: 993432986, 180042328, 179416433
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Via: 1.1 varnish (Varnish/5.1), 1.1 varnish (Varnish/5.1), 1.1 varnish (Varnish/5.1)
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Age: 0
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << X-Cache: cp1075 pass, cp3032 miss, cp3033 miss
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << X-Cache-Status: miss
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Server-Timing: cache;desc="miss"
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Strict-Transport-Security: max-age=106384710; includeSubDomains; preload
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Set-Cookie: WMF-Last-Access=13-Apr-2019;Path=/;HttpOnly;secure;Expires=Wed, 15 May 2019 12:00:00 GMT
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Set-Cookie: WMF-Last-Access-Global=13-Apr-2019;Path=/;Domain=.wikidata.org;HttpOnly;secure;Expires=Wed, 15 May 2019 12:00:00 GMT
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << X-Analytics: https=1;nocookies=1
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << X-Client-IP: 51.171.102.17
//	13:24:06.585 [main] DEBUG org.apache.http.headers - http-outgoing-0 << Accept-Ranges: bytes
//	13:24:06.589 [main] DEBUG o.a.h.impl.execchain.MainClientExec - Connection can be kept alive indefinitely
//	13:24:06.597 [main] WARN  o.a.h.c.p.ResponseProcessCookies - Invalid cookie header: "Set-Cookie: WMF-Last-Access=13-Apr-2019;Path=/;HttpOnly;secure;Expires=Wed, 15 May 2019 12:00:00 GMT". Invalid 'expires' attribute: Wed, 15 May 2019 12:00:00 GMT
//	13:24:06.598 [main] WARN  o.a.h.c.p.ResponseProcessCookies - Invalid cookie header: "Set-Cookie: WMF-Last-Access-Global=13-Apr-2019;Path=/;Domain=.wikidata.org;HttpOnly;secure;Expires=Wed, 15 May 2019 12:00:00 GMT". Invalid 'expires' attribute: Wed, 15 May 2019 12:00:00 GMT
//	13:24:06.600 [main] DEBUG o.e.r.h.c.SharedHttpClientSessionManager$2 - reponse MIME type is application/sparql-results+xml
//	13:24:06.671 [rdf4j-sesameclientimpl-0] DEBUG org.xml.sax.XMLReader - XMLReader initialized using JAXP: com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl$JAXPSAXParser@201e3d0e
//	imageProperty = http://commons.wikimedia.org/wiki/Special:FilePath/TomHanksJan2009.jpg
//	13:24:06.685 [rdf4j-sesameclientimpl-0] DEBUG o.a.h.i.c.PoolingHttpClientConnectionManager - Connection [id: 0][route: {s}->https://query.wikidata.org:443] can be kept alive indefinitely
//	13:24:06.685 [rdf4j-sesameclientimpl-0] DEBUG o.a.h.i.c.PoolingHttpClientConnectionManager - Connection released: [id: 0][route: {s}->https://query.wikidata.org:443][total kept alive: 1; route allocated: 1 of 5; total allocated: 1 of 10]
