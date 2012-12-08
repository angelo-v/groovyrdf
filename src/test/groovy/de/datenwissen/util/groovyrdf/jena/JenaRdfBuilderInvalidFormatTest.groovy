package de.datenwissen.util.groovyrdf.jena;


import de.datenwissen.util.groovyrdf.core.InvalidNestingException
import de.datenwissen.util.groovyrdf.core.RdfData
import org.junit.Before
import org.junit.Test

import static de.datenwissen.util.groovyrdf.test.Assert.*

/**
 * This test covers invalid formatting / nesting of {@link JenaRdfBuilder} closures
 */
class JenaRdfBuilderInvalidFormatTest extends ExpectedDataBuilder {
	
	def rdfBuilder

	@Before
	public void setUp() throws Exception {
		rdfBuilder = new JenaRdfBuilder()
	}
	
	@Test
	void testStatmentWithoutResource() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/vocab/name" "Alice"
		}
		assertIsomorphic(emptyData(), rdfData)
	}
	
	@Test
	void testTypeWithoutResource() {
		RdfData rdfData = rdfBuilder {
			a "http://example.com/vocab/Person"
		}
		assertIsomorphic(emptyData(), rdfData)
	}
	
	@Test
	void testEmptyResource() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
			}
		}
		assertIsomorphic(emptyData(), rdfData)
	}
	
	@Test
	void testEmptyResourceWithEmptyProperty() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				"http://example.com/resource/vocab/name"
			}
		}
		assertIsomorphic(emptyData(), rdfData)
	}
	
	@Test
	void testEmptyResourceWithEmptyPropertiesAndSubResources() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				"http://example.com/resource/vocab/name"
				"http://example.com/resource/vocab/location" ([])
				"http://example.com/resource/vocab/knows" {}
			}
		}
		assertIsomorphic(emptyData(), rdfData)
	}
	
	@Test
	void testEmptyResourceWithExplicitNullValues() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				"http://example.com/resource/vocab/name" null
				"http://example.com/resource/vocab/location" ([null, null])
				"http://example.com/resource/vocab/knows" {null}
			}
		}
		assertIsomorphic(emptyData(), rdfData)
	}
	
	@Test
	void testNonsense() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" "Alice " {
					"Bob"
				}
		}
		assertIsomorphic(emptyData(), rdfData)
	}
	
	@Test(expected=MissingMethodException)
	void testInvalidMethodCall() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" "Alice", "Miller"
		}
	}
	
	@Test(expected=UnsupportedOperationException)
	void testCreateNodeWithAttributesUnsupported() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" [:]
		}
	}
	
	@Test(expected=InvalidNestingException)
	void testInvalidNesting() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				"http://example.com/vocab/knows" {
					"http://example.com/vocab/name" "Anne"
				}
			}
		}
	}
	
	@Test(expected=InvalidNestingException)
	void testInvalidNestingType() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				"http://example.com/vocab/knows" {
					a "http://example.com/vocab/Person"
				}
			}
		}
	}
}
