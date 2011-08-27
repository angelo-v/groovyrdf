package de.datenwissen.util.groovyrdf.jena;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static de.datenwissen.util.groovyrdf.test.Assert.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach;

import de.datenwissen.util.groovyrdf.core.RdfData;
import de.datenwissen.util.groovyrdf.core.RdfNamespace;
import de.datenwissen.util.groovyrdf.jena.JenaRdfBuilder;
import de.datenwissen.util.groovyrdf.jena.JenaRdfData;

/**
 * This tests covers the building of rdf data from classes with {@link JenaRdfBuilder}
 */
class JenaRdfBuilderClassesTest {
	
	def builder
	def birthDay
	
	@Before
	public void setUp() throws Exception {
		builder = new JenaRdfBuilder()
		birthDay = new Date()
	}

	@Test
	void testBuildPersonRdf() {
		
		final RdfNamespace FOAF = new RdfNamespace("http://example.org/vocab/foaf/")
		final RdfNamespace DC = new RdfNamespace("http://example.org/vocab/dc/")
		
		def alice = new Person(
			webId: "http://example.org/alice#me",
			documentUri: "http://example.org/alice",
			title: "Mrs.",
			givenName: "Alice",
			familyName: "Smith",
			nick: "alice",
			mboxSha1: '0815',
			homepage: "http://example.org/",
			birthDay: birthDay+2,
			age: 21,
			knows: [
			]
		)
		
		def bob = new Person(
			webId: "http://example.org/bob#me",
			documentUri: "http://example.org/bob",
			title: "Mr.",
			givenName: "Bob",
			familyName: "Miller",
			nick: "bo",
			mboxSha1: '4711',
			homepage: "http://example.com/",
			birthDay: birthDay+1,
			age: 22,
			knows: [
				alice	
			]
		)
		
		def trudy = new Person(
			webId: "http://example.org/trudy#me",
			documentUri: "http://example.org/trudy",
			title: "Mrs.",
			givenName: "Trudy",
			familyName: "Doe",
			nick: "trudy",
			mboxSha1: "1337",
			homepage: "http://example.net/",
			birthDay: birthDay,
			age: 23,
			knows: [
				alice, bob
			]
		)
		
		RdfData expectedRdfData = buildExpectedData()
		RdfData rdfData = builder.rdf(trudy.rdfTemplate)
		assertIsomorphic(expectedRdfData, rdfData)

	}
	
	private RdfData buildExpectedData() {
		Model expectedModel = ModelFactory.createDefaultModel()
		
		def aliceResource = expectedModel.createResource("http://example.org/alice#me", ResourceFactory.createResource("http://example.org/vocab/foaf/Person"))
		aliceResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/givenName"), "Alice")
		aliceResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/familyName"), "Smith")
		def bobResource = expectedModel.createResource("http://example.org/bob#me", ResourceFactory.createResource("http://example.org/vocab/foaf/Person"))
		bobResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/givenName"), "Bob")
		bobResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/familyName"), "Miller")
		
		def trudyResource = expectedModel.createResource("http://example.org/trudy#me", ResourceFactory.createResource("http://example.org/vocab/foaf/Person"))
		trudyResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/title"), "Mrs.")
		trudyResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/givenName"), "Trudy")
		trudyResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/familyName"), "Doe")
		trudyResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/mbox_sha1sum"), "1337")
		trudyResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/homepage"), ResourceFactory.createResource("http://example.net/"))
		trudyResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/birthday"), new SimpleDateFormat('MM-dd').format(birthDay))
		trudyResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/age"), 23)
		def knowsProperty = ResourceFactory.createProperty("http://example.org/vocab/foaf/knows")
		trudyResource.addProperty(knowsProperty, aliceResource)
		trudyResource.addProperty(knowsProperty, bobResource)
		
		def documentResource = expectedModel.createResource("http://example.org/trudy", ResourceFactory.createResource("http://example.org/vocab/foaf/PersonalProfileDocument"))
		documentResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/dc/title"), "FOAF document of Trudy")
		documentResource.addProperty(ResourceFactory.createProperty("http://example.org/vocab/foaf/primaryTopic"), trudyResource)
		
		return new JenaRdfData(expectedModel)
	}
}

class Person {
	
	static final RdfNamespace FOAF = new RdfNamespace("http://example.org/vocab/foaf/")
	static final RdfNamespace DC = new RdfNamespace("http://example.org/vocab/dc/")
	
	String webId
	String documentUri
	
	String	title
	String	givenName
	String	familyName
	
	String	nick
	
	String	mboxSha1
	String	homepage
	
	Date	birthDay
	int 	age
	
	List<Person> knows = []
	
	def rdfTemplate = {
		"$webId" {
			a FOAF.Person
			"$FOAF.title" title
			"$FOAF.givenName" givenName
			"$FOAF.familyName" familyName
			"$FOAF.mbox_sha1sum" mboxSha1
			"$FOAF.homepage" {
				"$homepage" {}
			}
			"$FOAF.birthday" new SimpleDateFormat('MM-dd').format(birthDay)
			"$FOAF.age" age
			"$FOAF.knows" {
				knows.each { friend ->
					"$friend.webId" {
						a FOAF.Person
						"$FOAF.givenName" friend.givenName
						"$FOAF.familyName" friend.familyName
					}
				}
			}
		}
		"$documentUri" {
			a FOAF.PersonalProfileDocument
			"$DC.title" "FOAF document of $givenName"
			"$FOAF.primaryTopic" {
				"$webId" {}
			}
		}
	}
	
}