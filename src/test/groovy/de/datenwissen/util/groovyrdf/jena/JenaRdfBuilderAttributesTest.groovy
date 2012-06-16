package de.datenwissen.util.groovyrdf.jena;


import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.ResourceFactory
import com.hp.hpl.jena.shared.InvalidPropertyURIException
import de.datenwissen.util.groovyrdf.core.RdfData
import de.datenwissen.util.groovyrdf.core.RdfNamespace
import org.junit.Before
import org.junit.Test

import static de.datenwissen.util.groovyrdf.test.Assert.*

class JenaRdfBuilderAttributesTest {

	def rdfBuilder
	
	@Before
	public void setUp() throws Exception {
		rdfBuilder = new JenaRdfBuilder()
	}
	
	@Test
	public void testLanguageAttribute() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/book" {
				"http://example.com/vocab/title" "Buch", [lang: "de"]
				"http://example.com/vocab/title" "Book", [lang: "en"]
			}
		}
		
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource resource = expectedModel.createResource("http://example.com/resource/book")
		def property = ResourceFactory.createProperty("http://example.com/vocab/title")
		resource.addProperty(property, "Buch", "de")
		resource.addProperty(property, "Book", "en")
		
		assertIsomorphic(new JenaRdfData(expectedModel), rdfData)
		
	}
	
	@Test
	public void testInvalidAttributes() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/book" {
				"http://example.com/vocab/title" "Buch", [invalid: "de"]
				"http://example.com/vocab/title" "Book", [:]
			}
		}
		
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource resource = expectedModel.createResource("http://example.com/resource/book")
		def property = ResourceFactory.createProperty("http://example.com/vocab/title")
		resource.addProperty(property, "Buch")
		resource.addProperty(property, "Book")
		
		assertIsomorphic(new JenaRdfData(expectedModel), rdfData)
		
	}
	
	@Test
	public void testLanguageAndInvalidAttributes() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/book" {
				"http://example.com/vocab/title" "Buch", [lang: "de", invalid: "foo"]
				"http://example.com/vocab/title" "Book", [lang: "en", invalid: "bar"]
			}
		}
		
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource resource = expectedModel.createResource("http://example.com/resource/book")
		def property = ResourceFactory.createProperty("http://example.com/vocab/title")
		resource.addProperty(property, "Buch", "de")
		resource.addProperty(property, "Book", "en")
		
		assertIsomorphic(new JenaRdfData(expectedModel), rdfData)
		
	}
	
	@Test
	public void testDynamicLanguageAttribute() {
		
		def vocab = new RdfNamespace("http://example.com/vocab/")
		
		def book = [
			uri: "http://example.com/resource/book",
			titles: [
				[text: "Buch", lang: "de"],
				[text: "Book", lang: "en"]
			]
		]
		
		RdfData rdfData = rdfBuilder {
			"$book.uri" {
				for (def title : book.titles) {
					"$vocab.title" title.text, [lang: title.lang]
				}
			}
		}
		
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource resource = expectedModel.createResource("http://example.com/resource/book")
		def property = ResourceFactory.createProperty("http://example.com/vocab/title")
		resource.addProperty(property, "Buch", "de")
		resource.addProperty(property, "Book", "en")
		
		assertIsomorphic(new JenaRdfData(expectedModel), rdfData)
		
	}
	
	@Test(expected=InvalidPropertyURIException)
	public void testTypeWithAttributes() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/book" {
				a "http://example.com/vocab/Book", [lang: "de"]
			}
		}
	}

}
