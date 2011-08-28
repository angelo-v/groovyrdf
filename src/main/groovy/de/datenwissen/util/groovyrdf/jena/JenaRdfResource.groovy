package de.datenwissen.util.groovyrdf.jena

import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.ResourceFactory
import com.hp.hpl.jena.rdf.model.Statement
import com.hp.hpl.jena.vocabulary.RDF

import de.datenwissen.util.groovyrdf.core.RdfResource

class JenaRdfResource implements RdfResource {
	
	Resource jenaResource
	
	public JenaRdfResource(Resource resource) {
		this.jenaResource = resource
	}
	
	String getUri() {
		jenaResource.getURI()
	}
	
	String getType() {
		return jenaResource.getProperty(RDF.type).getResource().getURI()
	}
	
	def propertyMissing(String propertyUri){
		return getRdfProperty(propertyUri, null)
	}
	
	def call(String propertyUri) {
		return getRdfProperty(propertyUri, null)
	}
	
	def call(String propertyUri, String language) {
		getRdfProperty(propertyUri, language)
	}
	
	private getRdfProperty(String propertyUri, String language) {
		
		def property = ResourceFactory.createProperty(propertyUri)
		def statements = jenaResource.listProperties(property).toList()
		
		def set = getPropertiesSet(statements, language)
		if (set.size() > 1 ) {
			return set
		} else {
			if (set.isEmpty()) return null
			return set.asList().first()
		}
	}
	
	private getPropertiesSet(List statements, String language) {
		def set = [] as Set
		statements.each { statement ->
			def value = getLiteralValueOrResource(statement, language)
			if (value) set += value 
		}
		return set 
	}
	
	private getLiteralValueOrResource(Statement statement, String language) {
		def node = statement.getObject()
		if (node.isLiteral()) {
			if (!language || node.asLiteral().getLanguage() == language) {				
				return node.asLiteral().getValue()
			}
		} else {
			return new JenaRdfResource(node.asResource())
		}
	}
	
}
