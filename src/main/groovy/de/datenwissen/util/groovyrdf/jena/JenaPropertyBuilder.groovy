package de.datenwissen.util.groovyrdf.jena

import com.hp.hpl.jena.rdf.model.Resource

import de.datenwissen.util.groovyrdf.core.InvalidNestingException;

/**
 * Adds a property to a resource.
 */
protected abstract class JenaPropertyBuilder extends JenaAbstractResourceBuilder {
	
	protected abstract void addProperty(Resource resource)
	
	protected void setParent(Object parent) {
		if (parent instanceof JenaResourceBuilder) {
			Resource resource = ((JenaResourceBuilder) parent).resource
			this.addProperty(resource)
			return
		}
		throw new InvalidNestingException(parent, this)
	}
	
}