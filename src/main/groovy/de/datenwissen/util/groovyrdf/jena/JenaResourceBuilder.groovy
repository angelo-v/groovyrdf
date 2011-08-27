package de.datenwissen.util.groovyrdf.jena

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.ResourceFactory


/**
 * Used to build a resource that is nested in a property of another resource, like
 * 
 * <pre>{@code<resource> vocab:Friend <other>}</pre>
 */
protected class JenaResourceBuilder extends JenaAbstractResourceBuilder {
	
	Logger log = LoggerFactory.getLogger(JenaResourceBuilder.class);
	
	private Resource resource
	
	protected void setParent(Object parent) {
		JenaNestedPropertyBuilder nested = (JenaNestedPropertyBuilder) parent
		Property currentProperty = nested.property
		Resource currentResource = nested.currentResource
		log.debug "adding resource $resource to property $currentProperty of resource $currentResource"
		currentResource.addProperty(currentProperty, resource)
	}
	
	@Override
	protected Object createNode(Model model, Object name) {
		return new JenaNestedPropertyBuilder(property: ResourceFactory.createProperty(name))
	}
	
	@Override
	public String toString() {
		return "$resource"
	}
	
}
