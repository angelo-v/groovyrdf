package de.datenwissen.util.groovyrdf.core

/**
 * Formats that can be used to represent {@link RdfData}
 *
 */
enum RdfDataFormat {

    TURTLE('text/turtle', 'TURTLE'),
    RDF_XML('application/rdf+xml', 'RDF/XML'),
    RDF_XML_ABBREV('application/rdf+xml', 'RDF/XML-ABBREV'),
    N3('text/n3', 'N3'),
    N_TRIPLE('text/plain', 'N-TRIPLE')

    String mimeType // The official MIME type
    String jenaFormat // The name of the format used by Apache Jena framework

    /**
     * @param mimeType The MIME type of this RDF format
     */
    RdfDataFormat (String mimeType, String jenaFormat) {
        this.mimeType = mimeType
        this.jenaFormat = jenaFormat
    }

    /**
     * Finds an RdfDataFormat matching the given MIME type
     * @param mimeType A MIME type of a supported RDF format
     * @return The RdfDataFormat that the MIME type represents
     */
    static RdfDataFormat findByMimeType (String mimeType) {
        values().find {it.mimeType == mimeType}
    }

}


