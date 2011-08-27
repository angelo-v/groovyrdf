package de.datenwissen.util.groovyrdf.core;

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import de.datenwissen.util.groovyrdf.core.RdfNamespace;


/**
 * This test covers the creation and usage of a {@link RdfNamespace}
 */
class RdfNamespaceTest {

	@Test
	void testRdfNamespace() {
		RdfNamespace vocab = new RdfNamespace("http://example.com/vocab/")
		String actualBaseUri = vocab
		assertEquals("http://example.com/vocab/", actualBaseUri)
		assertEquals("http://example.com/vocab/Person", vocab.Person)
		assertEquals("http://example.com/vocab/givenName", vocab.givenName)
		assertEquals("http://example.com/vocab/anything", vocab.anything)
	}
}
