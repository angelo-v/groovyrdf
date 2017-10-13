package de.datenwissen.util.groovyrdf.jena

import de.datenwissen.util.groovyrdf.core.RdfBuilder
import de.datenwissen.util.groovyrdf.core.RdfBuilderFactory

/**
 * A {@link RdfBuilderFactory} returning a {@link JenaRdfBuilder}
 *
 */
class JenaRdfBuilderFactory implements RdfBuilderFactory {

	@Override
	public RdfBuilder newInstance() {
		return new JenaRdfBuilder();
	}

}
