package de.datenwissen.util.groovyrdf.jena;

import java.io.StringWriter;

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
import de.datenwissen.util.groovyrdf.core.RdfDataFormat;
import de.datenwissen.util.groovyrdf.jena.JenaRdfBuilder;
import de.datenwissen.util.groovyrdf.jena.JenaRdfData;

/**
 * This tests covers the building of rdf data with a {@link JenaRdfBuilder}.
 * The data to be built is hardcoded, for building rdf from dynamic data structures see {@link JenaRdfBuilderDynamicTest},
 * for building rdf from classes see {@link JenaRdfBuilderClassesTest}
 */
class JenaRdfBuilderTest extends ExpectedDataBuilder {

	def rdfBuilder
	
	@Before
	public void setUp() throws Exception {
		rdfBuilder = new JenaRdfBuilder()
	}
	
	@Test
	public void testEmptyData() {
		RdfData rdfData = rdfBuilder()
		assertIsomorphic(emptyData(), rdfData)
	}
	
	@Test
	public void testCreateResource() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				"http://example.com/vocab/name" "Alice"
			}
		}
		assertIsomorphic(createResource(), rdfData)
	}
	
	@Test
	public void testCreateResourceWithManyProperties() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				"http://example.com/vocab/givenName" "Alice"
				"http://example.com/vocab/familyName" "Smith"
				"http://example.com/vocab/location" "Berlin"
			}
		}
		
		assertIsomorphic(createResourceWithManyProperties(), rdfData)
		
	}
	
	@Test
	public void testCreateResourceWithType() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				a "http://example.com/vocab/Person"
				"http://example.com/vocab/name" "Alice"
			}
		}
		
		assertIsomorphic(createResourceWithType(), rdfData)
	}
	
	@Test
	public void testLinkToOtherResource() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				a "http://example.com/vocab/Person"
				"http://example.com/vocab/name" "Alice"
				"http://example.com/vocab/knows" {
					"http://example.com/resource/bob" {}
				}
			}
		}

		assertIsomorphic(linkToOtherResource(), rdfData)
	}
		
	@Test
	public void testLinkToManyOtherResource() {
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

		assertIsomorphic(linkToManyOtherResource(), rdfData)
	}
	
	@Test
	public void testLinkToSubResource() {
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
		
		assertIsomorphic(linkToSubResource(), rdfData)
	}
	
	@Test
	public void testLinkToManySubResources() {
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
		
		assertIsomorphic(linkToManySubResources(), rdfData)
	}
	
	@Test
	public void testManyNestedResources() {
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
		
		assertIsomorphic(manyNestedResources(), rdfData)
	}
	
	@Test
	public void testHighlyNestedResources() {
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
		
		assertIsomorphic(highlyNestedResources(), rdfData)
	}
	
	@Test
	public void testCreateResourceWithManyPropertyValues() {
		RdfData rdfData = rdfBuilder {
			"http://example.com/resource/alice" {
				"http://example.com/vocab/givenName" (["Alice", "Claudia"])
				"http://example.com/vocab/familyName" (["Smith", "Miller"])
				"http://example.com/vocab/location" (["Berlin", "Hamburg"])
			}
		}
		
		assertIsomorphic(createResourceWithManyPropertyValues(), rdfData)
	}
	
	@Test
	public void testOutput() {
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
		assertNotNull(rdfData)
		rdfData.write(System.out, RdfDataFormat.TURTLE)
	}
	
	
}
