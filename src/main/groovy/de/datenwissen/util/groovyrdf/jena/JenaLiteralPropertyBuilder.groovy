package de.datenwissen.util.groovyrdf.jena

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.ResourceFactory

/**
 * Builds a literal property like
 * <pre>{@code<resource> vocab:name "Alice"}</pre>
 * or
 * <pre>{@code<resource> vocab:description "A description in englisch"@en}</pre>
 * or
 * <pre>{@code<resource> vocab:count "5"^^xsd:integer}</pre>
 *
 */
protected class JenaLiteralPropertyBuilder extends JenaPropertyBuilder {
	
	Logger log = LoggerFactory.getLogger(JenaLiteralPropertyBuilder.class);
	
	String uri
	Object value
	String language
	
	private Property getProperty() {
		return ResourceFactory.createProperty(uri)
	}
	
	protected void addProperty(Resource resource) {
		log.debug "adding property ${this.property} with value $value to resource $resource"
		add(resource, value)
	}
	
	private add(Resource resource, List values) {
		for (def value : values) {
			add(resource, value)
		}
	}
	
	private add(Resource resource, def value) {
		if (language) {
			resource.addProperty(this.property, value, language)
		} else {		
			resource.addProperty(this.property, value)
		}
	}
	
	@Override
	public String toString() {
		return "$uri $value"
	}
}