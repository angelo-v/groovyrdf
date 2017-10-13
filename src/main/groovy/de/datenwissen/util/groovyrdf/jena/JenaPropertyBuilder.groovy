package de.datenwissen.util.groovyrdf.jena

import de.datenwissen.util.groovyrdf.core.InvalidNestingException
import org.apache.jena.rdf.model.Resource;

/**
 * Adds a property to a resource.
 */
abstract class JenaPropertyBuilder extends JenaAbstractResourceBuilder {

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