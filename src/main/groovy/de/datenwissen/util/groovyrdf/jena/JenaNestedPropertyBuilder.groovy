package de.datenwissen.util.groovyrdf.jena

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.Resource


/**
 * Used to remember the current resource while building a nested resource
 * that should be assigned as a property to the current one
 *
 */
protected class JenaNestedPropertyBuilder extends JenaPropertyBuilder {
	
	Logger log = LoggerFactory.getLogger(JenaNestedPropertyBuilder.class);
	
	Property property
	Resource currentResource
	
	void addProperty(Resource resource) {
		log.debug "Starting nested property for: $resource"
		currentResource = resource
	}
	
	@Override
	public String toString() {
		return "${currentResource?:""} $property"
	}
}
