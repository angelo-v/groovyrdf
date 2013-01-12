package de.datenwissen.util.groovyrdf.jena

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Resource
import de.datenwissen.util.groovyrdf.core.InvalidNestingException
import de.datenwissen.util.groovyrdf.core.RdfBuilder

/**
 * Superclass for the classes participating in the build process of a {@link RdfBuilder}
 *
 */
protected abstract class JenaAbstractResourceBuilder {
	protected abstract void setParent(Object parent)
	
	protected Object createNode(Model model, Object name) {
		Resource resource = model.createResource(name)
		return new JenaResourceBuilder(resource: resource)
	}

    protected Object addPublicKey (Model model, String keyUri, String label, String modulus, int exponent) {
        throw new InvalidNestingException(this, keyUri)
    }
	
}
