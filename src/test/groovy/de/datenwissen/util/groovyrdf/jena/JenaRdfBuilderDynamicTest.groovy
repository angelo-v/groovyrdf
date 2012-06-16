package de.datenwissen.util.groovyrdf.jena;


import de.datenwissen.util.groovyrdf.core.RdfData
import de.datenwissen.util.groovyrdf.core.RdfNamespace
import org.junit.Before
import org.junit.Test

import static de.datenwissen.util.groovyrdf.test.Assert.*

/**
 * This tests covers the building of rdf data with {@link JenaRdfBuilder} in a dynamic way
 */
class JenaRdfBuilderDynamicTest extends ExpectedDataBuilder {
	
	def vocab
	def rdfBuilder
	
	@Before
	public void setUp() throws Exception {
		vocab = new RdfNamespace("http://example.com/vocab/")
		rdfBuilder = new JenaRdfBuilder()
	}
	
	@Test
	public void testDynamicCreateResource() {
		
		String uri = "http://example.com/resource/alice"
		String name = "Alice"
		
		RdfData rdfData = rdfBuilder {
			"$uri" {
				"$vocab.name" name
			}
		}
		
		assertIsomorphic(createResource(), rdfData)
	}
	
	@Test
	public void testDynamicCreateResourceWithManyProperties() {

		def person = [
				uri: "http://example.com/resource/alice",
				givenName: "Alice",
				familyName: "Smith",
				location: "Berlin"
			]
		
		RdfData rdfData = rdfBuilder {
			"$person.uri" {
				"$vocab.givenName" person.givenName
				"$vocab.familyName" person.familyName
				"$vocab.location" person.location
			}
		}
		
		assertIsomorphic(createResourceWithManyProperties(), rdfData)
	}
	
	@Test
	public void testDynamicCreateResourceWithType() {
		
		String uri = "http://example.com/resource/alice"
		String name = "Alice"
		
		RdfData rdfData = rdfBuilder {
			"$uri" {
				a vocab.Person
				"$vocab.name" name
			}
		}
		
		assertIsomorphic(createResourceWithType(), rdfData)
	}
	
	@Test
	public void testDynamicLinkToOtherResource() {
		
		String uriAlice = "http://example.com/resource/alice"
		String uriBob = "http://example.com/resource/bob"
		
		String name = "Alice"
		
		RdfData rdfData = rdfBuilder {
			"$uriAlice" {
				a vocab.Person
				"$vocab.name" name
				"$vocab.knows" {
					"$uriBob" {}
				}
			}
		}
		
		assertIsomorphic(linkToOtherResource(), rdfData)
	}
	
	@Test
	public void testDynamicLinkToManyOtherResource() {
		
		String uriAlice = "http://example.com/resource/alice"
		String name = "Alice"
		def knows = ["http://example.com/resource/bob", "http://example.com/resource/trudy", "http://example.com/resource/carl"]
		
		RdfData rdfData = rdfBuilder {
			"$uriAlice" {
				a vocab.Person
				"$vocab.name" name
				"$vocab.knows" {
					knows.each {
						"$it" {}
					}
				}
			}
		}
		
		assertIsomorphic(linkToManyOtherResource(), rdfData)
	}
	
	@Test
	public void testDynamicLinkToSubResource() {
		
		def alice = [
			uri: "http://example.com/resource/alice",
			name: "Alice"
		]
		
		def bob = [
			uri: "http://example.com/resource/bob",
			name: "Bob"
		]
		
		RdfData rdfData = rdfBuilder {
			"$alice.uri" {
				a vocab.Person
				"$vocab.name" alice.name
				"$vocab.knows" {
					"$bob.uri" {
						a vocab.Person
						"$vocab.name" bob.name
					}
				}
			}
		}
		
		assertIsomorphic(linkToSubResource(), rdfData)
	}
	
	@Test
	public void testDynamicLinkToManySubResource() {
		
		def bob = [
			uri: "http://example.com/resource/bob",
			name: "Bob"
		]
		
		def trudy = [
			uri: "http://example.com/resource/trudy",
			name: "Trudy"
		]
		
		def carl = [
			uri: "http://example.com/resource/carl",
			name: "Carl"
		]
		
		def alice = [
			uri: "http://example.com/resource/alice",
			name: "Alice",
			knows: [bob, trudy, carl]
		]
		
		RdfData rdfData = rdfBuilder {
			"$alice.uri" {
				a vocab.Person
				"$vocab.name" alice.name
				knowsClosure(delegate, alice.knows)
			}
		}
		
		assertIsomorphic(linkToManySubResources(), rdfData)
	}
	
	@Test
	public void testDynamicManyNestedResources() {


		def trudy = [
			uri: "http://example.com/resource/trudy",
			name: "Trudy"
		]

		def bob = [
			uri: "http://example.com/resource/bob",
			name: "Bob",
			knows: [trudy]
		]

		def carl = [
			uri: "http://example.com/resource/carl",
			name: "Carl"
		]

		def alice = [
			uri: "http://example.com/resource/alice",
			name: "Alice",
			knows: [
				bob,
				trudy,
				carl
			]
		]

		RdfData rdfData = rdfBuilder {
			"$alice.uri" {
				a vocab.Person
				"$vocab.name" alice.name
				knowsClosure(delegate, alice.knows)
			}
		}
		
		assertIsomorphic(manyNestedResources(), rdfData)
	}
		
	def knowsClosure = { Object delegate, List persons ->
		this.knowsClosure.delegate = delegate
		if (persons) {
			"$vocab.knows" {
				for (Map person : persons) {
					personClosure(delegate, person)
				}
			}
		}
	}
	
	def friendsClosure = { Object delegate, List persons ->
		this.friendsClosure.delegate = delegate
		if (persons) {
			"$vocab.friends" {
				for (Map person : persons) {
					personClosure(delegate, person)
				}
			}
		}
	}
	
	def personClosure = { Object delegate, def person ->
		this.personClosure.delegate = delegate
		if (person) { 
			"$person.uri" {
				a "$vocab.Person"
				"$vocab.name" person.name
				"$vocab.location" person.location
				knowsClosure(delegate, person.knows)
				friendsClosure(delegate, person.friends)
			}
		}
	}
	
	@Test
	public void testHighlyNestedResources() {
		
		def tim = [
			uri: "http://example.com/resource/tim",
			name: "Tim"
		]
		
		def elisabeth = [
			uri: "http://example.com/resource/elisabeth",
			name: "Elisabeth"
		]
		
		def anne = [
			uri: "http://example.com/resource/anne",
			name: "Anne",
			location: "Hamburg"
		]
		
		def marc = [
			uri: "http://example.com/resource/marc",
			location: "Braunschweig",
			name: "Marc"
		]
		
		def theodor = [
			uri: "http://example.com/resource/theodor",
			name: "Theodor",
			location: "Hannover",
			knows: [elisabeth, anne],
			friends: [elisabeth, marc]
		]
		
		def carl = [
			uri: "http://example.com/resource/carl",
			name: "Carl",
			knows: [theodor]
		]
		
		def trudy = [
			uri: "http://example.com/resource/trudy",
			name: "Trudy",
			knows: [tim]
		]

		def bob = [
			uri: "http://example.com/resource/bob",
			name: "Bob",
			knows: [trudy]
		]

		def alice = [
			uri: "http://example.com/resource/alice",
			name: "Alice",
			location: "Berlin",
			knows: [
				bob,
				trudy,
				carl
			]
		]

		RdfData rdfData = rdfBuilder {
			personClosure(delegate, alice)
		}

		assertIsomorphic(highlyNestedResources(), rdfData)
	}
	
	@Test
	public void testDynamicCreateResourceWithManyPropertyValues() {
		
		String uri = "http://example.com/resource/alice"
		def givenNames = ["Alice", "Claudia"]
		def familyNames = ["Smith", "Miller"]
		def locations = ["Berlin", "Hamburg"]
		
		RdfData rdfData = rdfBuilder {
			"$uri" {
				"$vocab.givenName" givenNames
				"$vocab.familyName" familyNames
				"$vocab.location" locations
			}
		}
		
		assertIsomorphic(createResourceWithManyPropertyValues(), rdfData)
	} 
	
}
