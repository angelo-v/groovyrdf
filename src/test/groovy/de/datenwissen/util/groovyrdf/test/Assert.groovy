package de.datenwissen.util.groovyrdf.test

import org.junit.ComparisonFailure

import de.datenwissen.util.groovyrdf.core.RdfData;
import de.datenwissen.util.groovyrdf.core.RdfDataFormat;

/**
 * Helper class for assertions about {@link RdfData}
 */
class Assert {

	public static void assertIsomorphic(RdfData expected, RdfData actual) {
		boolean isomorphic = expected.equals(actual)
		if (!isomorphic) {
			throw new ComparisonFailure("Expected and actual RDF data are not structurally identical (isomorphic). Differences as TURTLE:\n", expected.toString(RdfDataFormat.TURTLE), actual.toString(RdfDataFormat.TURTLE));
		}
	}
	
}
