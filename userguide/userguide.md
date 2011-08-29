# User guide - groovyrdf

## General syntax

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
    
    rdfBuilderInstance: Instance of RdfBuilder
    resourceUri: String that contains a URI of a resource
    a: Keyword for a type property
    typeUri: String that contains a URI of a type
    predicateUri: String that contains a URI of a predicate
    value: a String or a primitive
    list: a list of values 
    lang: Keyword for setting a language tag
    language: String containing a language tag
    

## Code Examples

### Instantiate RdfBuilder

Currently a RdfBuilder based on the Jena-Framework is supported:

    def rdfBuilder = new JenaRdfBuilder()

You may use a BuilderFactory to instantiate a RdfBuilder:

    def rdfBuilder = new JenaRdfBuilderFactory().newInstance()

### Build & write RdfData

    def rdfBuilder = new JenaRdfBuilder()

    RdfData rdfData = rdfBuilder {
      // statements
    }

You can export it to TURTLE like this:

    rdfData.write(System.out, RdfDataFormat.TURTLE)
    
### Simple statement

    RdfData rdfData = rdfBuilder {
      "http://example.com/resource/alice" {
        "http://example.com/vocab/name" "Alice"
      }
    }

is equivalent to the following RDF in TURTLE syntax:

    <http://example.com/resource/alice>
      <http://example.com/vocab/name> "Alice".

### RDF types

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

### Multiple literals

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


### Language tags

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


### Nested resources

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
      
### Using namespaces

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

#### Why this strange "$vocab.name"-Syntax?

Because RdfBuilder actually has to call a method named "http://example.com/vocab/name" to build the statement.
If we just wrote vocab.name Groovy would try to call a method _on_ the resulting string instead of calling a method
that is named like the value _in_ the string. You will get used to it ;-)
      
### Be dynamic

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
    
### Take a look at the unit tests

For more examples a look at the groovyrdf unit tests might be helpful:

https://github.com/angelo-v/groovyrdf/tree/master/src/test/groovy/de/datenwissen/util/groovyrdf

## Final words
      
And now, be creative using groovyrdf to build your RDF data "the groovy way".
Questions, ideas and any feedback may be sent to angelo.veltens@online.de

To contribute to groovyrdf just fork and sent a pull request at github:
https://github.com/angelo-v/groovyrdf