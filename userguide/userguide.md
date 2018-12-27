# User guide - groovyrdf

Versions 0.2.x

groovyrdf is a groovy library that helps you building and consuming RDF data in a "groovy" way.  For building RDF see section "Building RDF". If you want to process RDF data or even load linked data resoures see section "Loading & consuming RDF".

## Building RDF

### General syntax

    rdfBuilderInstance {
      resourceUri {
        a typeUri
        predicateUri value
        predicateUri list
        predicateUri value, [lang: language]
        predicateUri {
          resourceUri {
            // more statements if needed
          }
        }
      }
    }

    /*
    rdfBuilderInstance: Instance of RdfBuilder
    resourceUri: String that contains a URI of a resource
    a: Keyword for a type property
    typeUri: String that contains a URI of a type
    predicateUri: String that contains a URI of a predicate
    value: a String or a primitive
    list: a list of values 
    lang: Keyword for setting a language tag
    language: String containing a language tag
    */


### Code Examples

#### Instantiate RdfBuilder

Currently a RdfBuilder based on the Jena-Framework is supported:

    def rdfBuilder = new JenaRdfBuilder()

You may use a BuilderFactory to instantiate a RdfBuilder:

    def rdfBuilder = new JenaRdfBuilderFactory().newInstance()

RdfBuilder is not thread-safe and cannot be reused for multiple builds.

#### Build & write RdfData

    def rdfBuilder = new JenaRdfBuilder()

    RdfData rdfData = rdfBuilder {
      // statements
    }

You can export it to TURTLE like this:

    rdfData.write(System.out, RdfDataFormat.TURTLE)
    
#### Simple statement

    RdfData rdfData = rdfBuilder {
      "http://example.com/resource/alice" {
        "http://example.com/vocab/name" "Alice"
      }
    }

is equivalent to the following RDF in TURTLE syntax:

    <http://example.com/resource/alice>
      <http://example.com/vocab/name> "Alice".

#### RDF types

Use the "a" keyword as a shortcut for the rdf:type predicate:

    RdfData rdfData = rdfBuilder {
      "http://example.com/resource/alice" {
         a "http://example.com/vocab/Person"
        "http://example.com/vocab/name" "Alice"
      }
    }

is equivalent to the following RDF in TURTLE syntax:

    <http://example.com/resource/alice>
      a <http://example.com/vocab/Person>;
      <http://example.com/vocab/name> "Alice".

#### Multiple literals

Just pass a groovy list to assign multiple values:

    def names = ["Alice", "Julia"]

    RdfData rdfData = rdfBuilder {
      "http://example.com/resource/alice" {
         a "http://example.com/vocab/Person"
        "http://example.com/vocab/name" names
      }
    }

is equivalent to the following RDF in TURTLE syntax:

    <http://example.com/resource/alice>
      a <http://example.com/vocab/Person>;
      <http://example.com/vocab/name> "Alice", "Julia".


#### Language tags

You can easily assign a language to a literal like this:

    RdfData rdfData = rdfBuilder {
      "http://example.com/resource/alice" {
         a "http://example.com/vocab/Person"
        "http://example.com/vocab/name" "Alice", [lang: "en"]
      }
    }

is equivalent to the following RDF in TURTLE syntax:

    <http://example.com/resource/alice>
      a <http://example.com/vocab/Person>;
      <http://example.com/vocab/name> "Alice"@en.


#### Nested resources

Of course you can nest resources:

    RdfData rdfData = rdfBuilder {
      "http://example.com/resource/alice" {
         a "http://example.com/vocab/Person"
        "http://example.com/vocab/name" "Alice"
        "http://example.com/vocab/knows" {
          "http://example.com/resource/bob" {
             a "http://example.com/vocab/Person"
            "http://example.com/vocab/name" "Bob"
          }
        }
      }
    }

is equivalent to the following RDF in TURTLE syntax:

    <http://example.com/resource/alice>
      a <http://example.com/vocab/Person>;
      <http://example.com/vocab/name> "Alice";
      <http://example.com/vocab/knows> <http://example.com/resource/bob>.
      
    <http://example.com/resource/bob>
      a <http://example.com/vocab/Person>;
      <http://example.com/vocab/name> "Bob".

#### Adding WebIDs

You can easily add a WebID to a resource:

    RdfData rdfData = rdfBuilder {
      "http://example.com/resource/alice" {
        publicKey (
          '#alicePublicKey',
          label: 'Public Key of Alice',
          modulus: '2cbf8fff963dea33ee7d4f007ae',
          exponent: 65537
        )
      }
    }

is equivalent to the following RDF in TURTLE syntax:

    <http://example.com/resource/alice>
      <http://www.w3.org/ns/auth/cert#key>
        <#alicePublicKey>.

    <#alicePublicKey>
      a <http://www.w3.org/ns/auth/cert#RSAPublicKey>;
      <http://www.w3.org/2000/01/rdf-schema#label> "Public Key of Alice";
      <http://www.w3.org/ns/auth/cert#exponent> 65537;
      <http://www.w3.org/ns/auth/cert#modulus>
        "2cbf8fff963dea33ee7d4f007ae"^^<http://www.w3.org/2001/XMLSchema#hexBinary> .

#### Using namespaces

The code will be much clearer if you use namespaces:

    def vocab = new RdfNamespace("http://example.com/vocab/")
    
Any call to a property of vocab will result in a String of the form "http://example.com/vocab/property"

    vocab.anything == "http://example.com/vocab/anything"
    
The "Nested resources" example with the help of this namespace:

    RdfData rdfData = rdfBuilder {
      "http://example.com/resource/alice" {
         a vocab.Person
        "$vocab.name" "Alice"
        "$vocab.knows" {
          "http://example.com/resource/bob" {
             a vocab.Person
            "$vocab.name" "Bob"
          }
        }
      }
    }

##### Why this strange "$vocab.name"-Syntax?

Because RdfBuilder actually has to call a method named "http://example.com/vocab/name" to build the statement.
If we just wrote vocab.name Groovy would try to call a method _on_ the resulting string instead of calling a method
that is named like the value _in_ the string. You will get used to it ;-)
      
#### Be dynamic

Of course when using a scripting language, you want to build your RDF dynamically based on some data structures.
Here is an example with a Person class:

    class Person {
      String uri
      String name
      List<Person> friends
    }      
      
This will generate RDF data with the name of a person and all their friends:

    Person person = ... // whatever you program does to set up a person
    
    def vocab = new RdfNamespace("http://example.com/vocab/")

    RdfData rdfData = rdfBuilder {
      "$person.uri" {
         a vocab.Person
        "$vocab.name" person.name
        "$vocab.knows" {
          person.friends.each { friend ->
            "$friend.uri" {
               a vocab.Person
              "$vocab.name" friend.name
            }
          }
        }
      }
    }
    
#### Take a look at the unit tests

For more examples a look at the groovyrdf unit tests might be helpful:

https://github.com/angelo-v/groovyrdf/tree/master/src/test/groovy/de/datenwissen/util/groovyrdf

## Loading & consuming RDF

### General syntax

    RdfResource resoure = rdfLoaderInstance.loadResource(resourceUri)
    resource.uri // get the resources URI
    resource.type // get the RDF type URI of the resource
    resource(predicateUri) // get the value/object of a predicate
    resource."$predicateUri" // another way to get the value/object of a predicate
    resource(predicateUri, language) // get the value in a specific language

    /*
    rdfLoaderInstance: Instance of RdfLoader
    resourceUri: String that contains a URI of a resource
    predicateUri: String that contains a URI of a predicate
    language: String containing a language tag
    */

### Code Examples

Given the following data is provided under http://example.com/resource/alice

    <http://example.com/resource/alice>
      a <http://example.com/vocab/Person> ;
      <http://example.com/vocab/name> 'Alice' ;
      <http://example.com/vocab/knows> <http://example.com/resource/bob>.

    <http://example.com/resource/alice.rdf>
      <http://example.com/vocab/dateCreated> '2012-12-12' .

#### Instantiate RdfLoader

Currently a RdfLoader based on the Jena-Framework is supported:

    def rdfLoader = new JenaRdfLoader()

A loader may be reused for multiple requests.

#### Load RDF document

The RDF data can be retrieved like this:

      RdfData rdfData = rdfLoaderInstance.load('http://example.com/resource/alice')

The loader uses content-negotiation, so that you do not have to care about the RDF data format used by the server you are querying.

#### Accessing RDF resources

RDF resources can be accessed by there uri in two different ways:

    RdfData rdfData = rdfLoaderInstance.load('http://example.com/resource/alice')
    RdfResource alice = rdfData.'http://example.com/resource/alice' // first way
    RdfResource aliceDoc = rdfData('http://example.com/resource/alice.rdf') // second way

#### Load RDF resource directly

If you are interested in only one specific resource, you may retrieve it directly via loadResource:

    RdfResource alice = rdfLoaderInstance.loadResource('http://example.com/resource/alice')

#### Listing subjects

You can get a list of all subject resources using the listSubject()-Method:

    RdfData rdfData = rdfLoaderInstance.load('http://example.com/resource/alice')
    List<RdfResource> resources = rdfData.listSubjects()
    ['http://example.com/resource/alice', 'http://example.com/resource/alice.rdf'] == resources*.uri

You may only list subjects of a specific RDF type:

    List<RdfResource> resources = rdfData.listSubjects('http://example.com/vocab/Person')
    ['http://example.com/resource/alice'] == resources*.uri

#### Accessing properties

Accessing properties works the same ways as accessing the resources themselves:

    RdfResource alice = rdfData.'http://example.com/resource/alice'
    RdfResource aliceDoc = rdfData('http://example.com/resource/alice.rdf')
    alice.'http://example.com/vocab/name' == 'Alice'
    aliceDoc('http://example.com/vocab/dateCreated') == '2012-12-12'

You can add a preferred language to get the property in that language, if available:

    alice ('http://example.com/vocab/name', 'en') == 'Alice'

#### Listing properties

You can list the available properties of a resource:

    RdfResource alice = rdfData.'http://example.com/resource/alice'
    Set<String> properties = alice.listProperties ()
    ['http://example.com/vocab/name', 'http://example.com/vocab/knows'] as Set == properties

Be aware that the RDF type is excluded from that list, although it may be present!

#### Single value vs. list of values

For any RDF predicate may exist multiple values. groovyrdf returns a single value, if there is only one, and a Set of vales, if there are multiple values for the given predicate.

Given RDF:

    <http://example.com/resource/alice>
      <http://example.com/vocab/name> "Alice" ;
      <http://example.com/vocab/nick> "Ally", "Lissy";

Accessing the vocab:name predicate will return only one value, while accessing vocab:nick returns a Set:

    RdfResource alice = rdfData.'http://example.com/resource/alice'
    alice.'http://example.com/vocab/name' == 'Alice'
    alice.'http://example.com/vocab/nick' == ['Ally', 'Lissy'] as Set

#### Accessing linked resources

If the value of a property is a resoure, you will get a RdfResource instance:

    RdfResource bob = alice.'http://example.com/vocab/knows'

You will get a Set of RdfResource instances, if there are multiple values!

Note that you cannot access properties of the linked resource, that are not provided in the current RDF data:

    bob.'http://example.com/vocab/name' == null

But you may explicitly load the linked resource and process the loaded data afterwards:

    bob = rdfLoader.loadResource (bob.uri)
    bob.'http://example.com/vocab/name' == 'Bob'

## Final words
      
And now, be creative using groovyrdf to build and process RDF data "the groovy way".
Questions, ideas and any feedback may be sent to angelo.veltens@online.de

To contribute to groovyrdf just fork and sent a pull request at github:
https://github.com/angelo-v/groovyrdf