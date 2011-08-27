package de.datenwissen.util.groovyrdf.core

/**
 * Thrown if {@link RdfBuilder} encountered an invalid nesting of closures.
 */
class InvalidNestingException extends RuntimeException {

	public InvalidNestingException(Object parent, Object child) {
		super("'$parent' is an invalid parent node for '$child'".toString())
	}
}
