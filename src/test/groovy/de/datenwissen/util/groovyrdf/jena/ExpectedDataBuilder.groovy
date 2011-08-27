package de.datenwissen.util.groovyrdf.jena

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.ResourceFactory
import com.hp.hpl.jena.vocabulary.RDF

import de.datenwissen.util.groovyrdf.core.RdfData;
import de.datenwissen.util.groovyrdf.jena.JenaRdfData


/**
 * Superclass for unit tests, that helps to build the expected data with the jena framework
 */
class ExpectedDataBuilder {
	
	private static final knowsProperty = ResourceFactory.createProperty("http://example.com/vocab/knows")
	private static final friendsProperty = ResourceFactory.createProperty("http://example.com/vocab/friends")
	private static final locationProperty = ResourceFactory.createProperty("http://example.com/vocab/location")
	
	protected RdfData emptyData() {
		return new JenaRdfData(ModelFactory.createDefaultModel())
	}
	
	protected RdfData createResource() {
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource alice = expectedModel.createResource("http://example.com/resource/alice")
		alice.addProperty(ResourceFactory.createProperty("http://example.com/vocab/name"), "Alice")
		
		return new JenaRdfData(expectedModel)
	}
	
	protected RdfData createResourceWithManyProperties() {
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource resource = expectedModel.createResource("http://example.com/resource/alice")
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/givenName"), "Alice")
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/familyName"), "Smith")
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/location"), "Berlin")
		
		return new JenaRdfData(expectedModel)
	}
	
	protected RdfData createResourceWithType() {
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource resource = createPerson(expectedModel, "Alice")
		return new JenaRdfData(expectedModel)
	}
	
	protected RdfData linkToOtherResource() {
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource alice = createPerson(expectedModel, "Alice")
		knows(alice, ResourceFactory.createResource("http://example.com/resource/bob"))
		return new JenaRdfData(expectedModel)
	}
	
	protected RdfData linkToManyOtherResource() {
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource alice = createPerson(expectedModel, "Alice")
		knows (alice, ResourceFactory.createResource("http://example.com/resource/bob"))
		knows (alice, ResourceFactory.createResource("http://example.com/resource/trudy"))
		knows (alice, ResourceFactory.createResource("http://example.com/resource/carl"))
		return new JenaRdfData(expectedModel)
	}
	
	protected RdfData linkToSubResource() {
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource alice = createPerson(expectedModel, "Alice")
		Resource bob = createPerson(expectedModel, "Bob")
		knows(alice, bob)
		return new JenaRdfData(expectedModel)
	}
	
	protected RdfData linkToManySubResources() {
		Model expectedModel = ModelFactory.createDefaultModel()
		
		Resource alice = createPerson(expectedModel, "Alice")
		Resource bob = createPerson(expectedModel, "Bob")
		Resource trudy = createPerson(expectedModel, "Trudy")
		Resource carl = createPerson(expectedModel, "Carl")
		
		knows(alice, bob)
		knows(alice, trudy)
		knows(alice, carl)
		
		return new JenaRdfData(expectedModel)
	}
	
	protected RdfData manyNestedResources() {
		Model expectedModel = ModelFactory.createDefaultModel()
		
		Resource alice = createPerson(expectedModel, "Alice")
		Resource trudy = createPerson(expectedModel, "Trudy")
		Resource bob = createPerson(expectedModel, "Bob")
		Resource carl = createPerson(expectedModel, "Carl")
		
		knows(bob, trudy)
		knows(alice, bob)
		knows(alice, trudy)
		knows(alice, carl)
		
		return new JenaRdfData(expectedModel)
	}
	
	protected RdfData highlyNestedResources() {
		Model expectedModel = ModelFactory.createDefaultModel()
		
		Resource alice = createPerson(expectedModel, "Alice")
		Resource tim = createPerson(expectedModel, "Tim")
		Resource trudy = createPerson(expectedModel, "Trudy")
		Resource bob = createPerson(expectedModel, "Bob")
		Resource carl = createPerson(expectedModel, "Carl")
		Resource theodor = createPerson(expectedModel, "Theodor")
		Resource elisabeth = createPerson(expectedModel, "Elisabeth")
		Resource anne = createPerson(expectedModel, "Anne")
		Resource marc = createPerson(expectedModel, "Marc")
		
		knows(trudy, tim)
		knows(bob, trudy)
		knows(alice, bob)
		knows(alice, trudy)
		knows(alice, carl)
		knows(carl, theodor)
		knows(theodor, elisabeth)
		knows(theodor, anne)
		
		friends(theodor, elisabeth)
		friends(theodor, marc)
		
		location(alice, "Berlin")
		location(theodor, "Hannover")
		location(anne, "Hamburg")
		location(marc, "Braunschweig")
		
		RdfData expectedRdfData = new JenaRdfData(expectedModel)
	}
	
	private Resource createPerson(Model model, String name) {
		Resource resource = model.createResource("http://example.com/resource/${name.toLowerCase()}", ResourceFactory.createResource("http://example.com/vocab/Person"))
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/name"), name)
		return resource
	}
	
	private void knows(Resource a, Resource b) {
		a.addProperty(knowsProperty, b)
	}
	
	private void friends(Resource a, Resource b) {
		a.addProperty(friendsProperty, b)
	}
	
	private void location(Resource person, String location) {
		person.addProperty(locationProperty, location)
	}
	
	protected RdfData createResourceWithManyPropertyValues() {
		Model expectedModel = ModelFactory.createDefaultModel()
		Resource resource = expectedModel.createResource("http://example.com/resource/alice")
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/givenName"), "Alice")
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/givenName"), "Claudia")
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/familyName"), "Smith")
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/familyName"), "Miller")
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/location"), "Berlin")
		resource.addProperty(ResourceFactory.createProperty("http://example.com/vocab/location"), "Hamburg")
		
		return new JenaRdfData(expectedModel)
	}

}
