package de.datenwissen.util.groovyrdf.jena

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.ResourceFactory
import com.hp.hpl.jena.vocabulary.RDF

/**
 * Builds a rdf:type property like <pre>{@code<resource> a vocab:Thing}</pre>
 *
 */
protected class JenaTypePropertyBuilder extends JenaPropertyBuilder {
	
	Logger log = LoggerFactory.getLogger(JenaTypePropertyBuilder.class);
	
	String uri
	
	private Resource getType() {
		return ResourceFactory.createResource(uri)
	}
	
	protected void addProperty(Resource resource) {
		log.debug "adding type ${this.type} to resource $resource"
		resource.addProperty(RDF.type, this.type)
	}
	
	@Override
	public String toString() {
		return "a $uri"
	}
	
}
