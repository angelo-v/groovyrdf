package de.datenwissen.util.groovyrdf.core

import org.junit.Test
import static org.junit.Assert.*

class RdfDataFormatTest {

    @Test
    void testMimeTypes() {
        assertEquals ('text/turtle', RdfDataFormat.TURTLE.mimeType)
        assertEquals ('application/rdf+xml', RdfDataFormat.RDF_XML.mimeType)
        assertEquals ('application/rdf+xml', RdfDataFormat.RDF_XML_ABBREV.mimeType)
        assertEquals ('text/n3', RdfDataFormat.N3.mimeType)
        assertEquals ('text/plain', RdfDataFormat.N_TRIPLE.mimeType)
    }

    @Test
    void testFindByMimeType () {
        assertEquals(RdfDataFormat.TURTLE, RdfDataFormat.findByMimeType ('text/turtle'))
        assertEquals(RdfDataFormat.RDF_XML, RdfDataFormat.findByMimeType ('application/rdf+xml'))
        assertEquals(RdfDataFormat.N3, RdfDataFormat.findByMimeType ('text/n3'))
        assertEquals(RdfDataFormat.N_TRIPLE, RdfDataFormat.findByMimeType ('text/plain'))
    }

    @Test
    void testJenaFormats() {
        assertEquals ('TURTLE', RdfDataFormat.TURTLE.jenaFormat)
        assertEquals ('RDF/XML', RdfDataFormat.RDF_XML.jenaFormat)
        assertEquals ('RDF/XML-ABBREV', RdfDataFormat.RDF_XML_ABBREV.jenaFormat)
        assertEquals ('N3', RdfDataFormat.N3.jenaFormat)
        assertEquals ('N-TRIPLE', RdfDataFormat.N_TRIPLE.jenaFormat)
    }
}
