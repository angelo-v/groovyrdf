package de.datenwissen.util.groovyrdf.core

interface RdfResource {
	
	String getUri()
	String getType()
	Set<String> listProperties()
}