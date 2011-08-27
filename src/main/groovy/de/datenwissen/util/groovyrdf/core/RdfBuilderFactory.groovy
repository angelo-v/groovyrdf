package de.datenwissen.util.groovyrdf.core


/**
 * Factory for instantiating {@link RdfBuilder}s.
 *
 */
interface RdfBuilderFactory {
	
	/**
	 * @return A new instance of a {@link RdfBuilder}
	 */
	RdfBuilder newInstance()

}
