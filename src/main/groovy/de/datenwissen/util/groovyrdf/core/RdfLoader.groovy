package de.datenwissen.util.groovyrdf.core

/**
 * Loads {@link RdfData} over HTTP using content negotiation
 *
 */
public interface RdfLoader {

    /**
     *
     * @param uri The URI to load the data from
     * @return The retrieved data as {@link RdfData}
     * * @throws RdfParsingException If the response could not be parsed
     */
    RdfData load (String uri)  throws RdfParsingException

    /**
     * Load RDF data from the given URI but returns only the resource identified by that URI.
     * @param uri uri The URI to load the data from
     * @return The RdfResource represented by the given URI
     * @throws RdfParsingException If the response could not be parsed
     */
    RdfResource loadResource (String uri)  throws RdfParsingException

}