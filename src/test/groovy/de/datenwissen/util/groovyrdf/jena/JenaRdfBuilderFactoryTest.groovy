package de.datenwissen.util.groovyrdf.jena
import static org.junit.Assert.*

import org.junit.Test

import de.datenwissen.util.groovyrdf.core.RdfBuilder;
import de.datenwissen.util.groovyrdf.jena.JenaRdfBuilder
import de.datenwissen.util.groovyrdf.jena.JenaRdfBuilderFactory


class JenaRdfBuilderFactoryTest {
	
	@Test
	void testNewInstance() {
		def factory = new JenaRdfBuilderFactory()
		RdfBuilder actualBuilder = factory.newInstance()
		assertTrue(actualBuilder instanceof JenaRdfBuilder)
	}

}
