package de.datenwissen.util.groovyrdf.jena;


import de.datenwissen.util.groovyrdf.core.RdfData
import de.datenwissen.util.groovyrdf.core.RdfDataFormat
import org.junit.Before
import org.junit.Test

import static de.datenwissen.util.groovyrdf.test.Assert.*
import static org.junit.Assert.assertNotNull
import de.datenwissen.util.groovyrdf.core.InvalidWebIdException

import static org.junit.Assert.fail

/**
 * This tests covers the building of rdf data with a {@link JenaRdfBuilder}.
 * The data to be built is hardcoded, for building rdf from dynamic data structures see {@link JenaRdfBuilderDynamicTest},
 * for building rdf from classes see {@link JenaRdfBuilderClassesTest}
 */
class JenaRdfBuilderTest extends ExpectedDataBuilder {

    def rdfBuilder

    @Before
    public void setUp () throws Exception {
        rdfBuilder = new JenaRdfBuilder ()
    }

    @Test
    public void testEmptyData () {
        RdfData rdfData = rdfBuilder ()
        assertIsomorphic (emptyData (), rdfData)
    }

    @Test
    public void testCreateResource () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                "http://example.com/vocab/name" "Alice"
            }
        }
        assertIsomorphic (createResource (), rdfData)
    }

    @Test
    public void testCreateResourceWithManyProperties () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                "http://example.com/vocab/givenName" "Alice"
                "http://example.com/vocab/familyName" "Smith"
                "http://example.com/vocab/location" "Berlin"
            }
        }

        assertIsomorphic (createResourceWithManyProperties (), rdfData)

    }

    @Test
    public void testCreateResourceWithType () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                a "http://example.com/vocab/Person"
                "http://example.com/vocab/name" "Alice"
            }
        }

        assertIsomorphic (createResourceWithType (), rdfData)
    }

    @Test
    public void testLinkToOtherResource () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                a "http://example.com/vocab/Person"
                "http://example.com/vocab/name" "Alice"
                "http://example.com/vocab/knows" {
                    "http://example.com/resource/bob" {}
                }
            }
        }

        assertIsomorphic (linkToOtherResource (), rdfData)
    }

    @Test
    public void testLinkToManyOtherResource () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                a "http://example.com/vocab/Person"
                "http://example.com/vocab/name" "Alice"
                "http://example.com/vocab/knows" {
                    "http://example.com/resource/bob" {}
                    "http://example.com/resource/trudy" {}
                    "http://example.com/resource/carl" {}
                }
            }
        }

        assertIsomorphic (linkToManyOtherResource (), rdfData)
    }

    @Test
    public void testLinkToSubResource () {
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

        assertIsomorphic (linkToSubResource (), rdfData)
    }

    @Test
    public void testLinkToManySubResources () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                a "http://example.com/vocab/Person"
                "http://example.com/vocab/name" "Alice"
                "http://example.com/vocab/knows" {
                    "http://example.com/resource/bob" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Bob"
                    }
                    "http://example.com/resource/trudy" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Trudy"
                    }
                    "http://example.com/resource/carl" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Carl"
                    }
                }
            }
        }

        assertIsomorphic (linkToManySubResources (), rdfData)
    }

    @Test
    public void testManyNestedResources () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                a "http://example.com/vocab/Person"
                "http://example.com/vocab/name" "Alice"
                "http://example.com/vocab/knows" {
                    "http://example.com/resource/bob" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Bob"
                        "http://example.com/vocab/knows" {
                            "http://example.com/resource/trudy" {}
                        }
                    }
                    "http://example.com/resource/trudy" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Trudy"
                    }
                    "http://example.com/resource/carl" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Carl"
                    }
                }
            }
        }

        assertIsomorphic (manyNestedResources (), rdfData)
    }

    @Test
    public void testHighlyNestedResources () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                a "http://example.com/vocab/Person"
                "http://example.com/vocab/name" "Alice"
                "http://example.com/vocab/knows" {
                    "http://example.com/resource/bob" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Bob"
                        "http://example.com/vocab/knows" {
                            "http://example.com/resource/trudy" {
                                "http://example.com/vocab/knows" {
                                    "http://example.com/resource/tim" {
                                        a "http://example.com/vocab/Person"
                                        "http://example.com/vocab/name" "Tim"
                                    }
                                }
                            }
                        }
                    }
                    "http://example.com/resource/trudy" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Trudy"
                    }
                    "http://example.com/resource/carl" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Carl"
                        "http://example.com/vocab/knows" {
                            "http://example.com/resource/theodor" {
                                a "http://example.com/vocab/Person"
                                "http://example.com/vocab/name" "Theodor"
                                "http://example.com/vocab/knows" {
                                    "http://example.com/resource/elisabeth" {
                                        a "http://example.com/vocab/Person"
                                        "http://example.com/vocab/name" "Elisabeth"
                                    }
                                    "http://example.com/resource/anne" {
                                        a "http://example.com/vocab/Person"
                                        "http://example.com/vocab/name" "Anne"
                                        "http://example.com/vocab/location" "Hamburg"
                                    }
                                }
                                "http://example.com/vocab/friends" {
                                    "http://example.com/resource/elisabeth" {
                                    }
                                    "http://example.com/resource/marc" {
                                        a "http://example.com/vocab/Person"
                                        "http://example.com/vocab/name" "Marc"
                                        "http://example.com/vocab/location" "Braunschweig"
                                    }
                                }
                                "http://example.com/vocab/location" "Hannover"
                            }
                        }
                    }
                }
                "http://example.com/vocab/location" "Berlin"
            }
        }

        assertIsomorphic (highlyNestedResources (), rdfData)
    }

    @Test
    public void testCreateResourceWithManyPropertyValues () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                "http://example.com/vocab/givenName" (["Alice", "Claudia"])
                "http://example.com/vocab/familyName" (["Smith", "Miller"])
                "http://example.com/vocab/location" (["Berlin", "Hamburg"])
            }
        }

        assertIsomorphic (createResourceWithManyPropertyValues (), rdfData)
    }

    @Test
    public void testWebID () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                publicKey (
                        '#rsaPublicKey',
                        label: 'Public Key Label',
                        modulus: 'cbf8fff963dea33ee7d4f007ae',
                        exponent: 65537
                )
            }
        }
        assertIsomorphic (createResourceWithWebId (), rdfData)
    }

    @Test
    public void testWebIDWithoutLabel () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                publicKey (
                        '#rsaPublicKey',
                        modulus: 'cbf8fff963dea33ee7d4f007ae',
                        exponent: 65537
                )
            }
        }
        assertIsomorphic (createResourceWithWebIdWithoutLabel (), rdfData)
    }

    @Test
    public void testWebIDForNestedResources () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                "http://example.com/vocab/knows" {
                    "http://example.com/resource/bob" {
                        publicKey (
                                '#rsaPublicKey',
                                label: 'Public Key Label',
                                modulus: 'cbf8fff963dea33ee7d4f007ae',
                                exponent: 65537
                        )
                    }
                }
            }
        }
        assertIsomorphic (createNestedResourceWithWebId (), rdfData)
        rdfData.write (System.out, RdfDataFormat.RDF_XML_ABBREV)
    }

    @Test
    public void testWebIDForMultipleResources () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                publicKey (
                        '#rsaPublicKeyAlice',
                        label: 'Public Key of Alice',
                        modulus: '1cbf8fff963dea33ee7d4f007ae',
                        exponent: 65537
                )
                "http://example.com/vocab/knows" {
                    "http://example.com/resource/bob" {
                        publicKey (
                                '#rsaPublicKeyBob',
                                label: 'Public Key of Bob',
                                modulus: '2cbf8fff963dea33ee7d4f007ae',
                                exponent: 65538
                        )
                    }
                }
            }
        }
        assertIsomorphic (createMultipleResourceWithWebId (), rdfData)
    }

    @Test(expected=InvalidWebIdException)
    public void testWebIDWithoutExponent () {
        rdfBuilder {
            "http://example.com/resource/alice" {
                publicKey (
                        '#rsaPublicKey',
                        label: 'Public Key Label',
                        modulus: '2cbf8fff963dea33ee7d4f007ae'
                )
            }
        }
    }

    @Test(expected=InvalidWebIdException)
    public void testWebIDWithInvalidExponent () {
        rdfBuilder {
            "http://example.com/resource/alice" {
                publicKey (
                        '#rsaPublicKey',
                        label: 'Public Key Label',
                        modulus: '2cbf8fff963dea33ee7d4f007ae',
                        exponent: '1337'
                )
            }
        }
    }

    @Test(expected=InvalidWebIdException)
    public void testWebIDWithoutModulus () {
        rdfBuilder {
            "http://example.com/resource/alice" {
                publicKey (
                        '#rsaPublicKey',
                        label: 'Public Key Label',
                        exponent: 65537
                )
            }
        }
    }

    @Test
    public void testOutput () {
        RdfData rdfData = rdfBuilder {
            "http://example.com/resource/alice" {
                a "http://example.com/vocab/Person"
                "http://example.com/vocab/name" "Alice"
                publicKey (
                        '#rsaPublicKeyAlice',
                        label: 'Public Key of Alice',
                        modulus: '1cbf8fff963dea33ee7d4f007ae',
                        exponent: 65537
                )
                publicKey (
                        '#rsaPublicKeyAlice2',
                        label: 'Second Public Key of Alice',
                        modulus: '2cbf8fff963dea33ee7d4f007ae',
                        exponent: 65537
                )
                "http://example.com/vocab/knows" {
                    "http://example.com/resource/bob" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Bob"
                        "http://example.com/vocab/knows" {
                            "http://example.com/resource/trudy" {
                                "http://example.com/vocab/knows" {
                                    "http://example.com/resource/tim" {
                                        a "http://example.com/vocab/Person"
                                        "http://example.com/vocab/name" "Tim"
                                    }
                                }
                            }
                        }
                    }
                    "http://example.com/resource/trudy" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Trudy"
                    }
                    "http://example.com/resource/carl" {
                        a "http://example.com/vocab/Person"
                        "http://example.com/vocab/name" "Carl"
                        "http://example.com/vocab/knows" {
                            "http://example.com/resource/theodor" {
                                a "http://example.com/vocab/Person"
                                "http://example.com/vocab/name" "Theodor"
                                "http://example.com/vocab/knows" {
                                    "http://example.com/resource/elisabeth" {
                                        a "http://example.com/vocab/Person"
                                        "http://example.com/vocab/name" "Elisabeth"
                                    }
                                    "http://example.com/resource/anne" {
                                        a "http://example.com/vocab/Person"
                                        "http://example.com/vocab/name" "Anne"
                                        "http://example.com/vocab/location" "Hamburg"
                                    }
                                }
                                "http://example.com/vocab/friends" {
                                    "http://example.com/resource/elisabeth" {
                                    }
                                    "http://example.com/resource/marc" {
                                        a "http://example.com/vocab/Person"
                                        "http://example.com/vocab/name" "Marc"
                                        "http://example.com/vocab/location" "Braunschweig", [lang: "de"]
                                    }
                                }
                                "http://example.com/vocab/location" "Hannover"
                            }
                        }
                    }
                }
                "http://example.com/vocab/location" "Berlin"
            }
        }
        assertNotNull (rdfData)
        rdfData.write (System.out, RdfDataFormat.TURTLE)
    }


}
