package com.der5101.assignment4.model;

import java.util.List;

/***
 * 
 * Actor holds linked data values from dbpedia and wikidata endpoints relevant
 * to Actors in Movies. This lets us bind the responses from SPARQL queries
 * to our own data model and focus on the data points of interest for Actors.
 * 
 * It holds the actor name details, their bio, (film is a test variable only), 
 * imagelink for image property, awardlabels for type of award, and award works i.e.
 * which film they got the award for. 
 * 
 * This class is also intended to allow future extension i.e. if we might want to use rdf4j
 * to handle the responses and customize a CRUD application that writes to a local
 * RDF4J sail repository in future OR use a remote one like Stardog, which can be
 * wired into Spring Boot.
 *
 */
public class Actor {
	
	private String firstName;
	private String lastName;
	private String biography;
	private String film;
	private String imageLink;
	private List<String> films;
	private List<String> awardLabels;
	private List<String> awardWorks;
	
	// getters and setters
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getBiography() {
		return biography;
	}
	public void setBiography(String biography) {
		this.biography = biography;
	}
	public String getFilm() {
		return film;
	}
	public void setFilm(String film) {
		this.film = film;
	}
	public String getImageLink() {
		return imageLink;
	}
	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}
	public List<String> getFilms() {
		return films;
	}
	public void setFilms(List<String> films) {
		this.films = films;
	}
	public List<String> getAwardLabels() {
		return awardLabels;
	}
	public void setAwardLabels(List<String> awardLabels) {
		this.awardLabels = awardLabels;
	}
	public List<String> getAwardWorks() {
		return awardWorks;
	}
	public void setAwardWorks(List<String> awardWorks) {
		this.awardWorks = awardWorks;
	}
	@Override
	public String toString() {
		return "Actor [firstName=" + firstName + ", lastName=" + lastName + ", biography=" + biography + ", film="
				+ film + ", imageLink=" + imageLink + ", films=" + films + ", awardLabels=" + awardLabels
				+ ", awardWorks=" + awardWorks + "]";
	}
	
}
