package de.datenwissen.util.groovyrdf.core

/**
 * Loads {@link RdfData} over HTTP using content negotiation
 *
 */
public interface RdfLoader {

    /**
     * Load RDF data from the given URI
     * @param uri The URI to load the data from
     * @return The retrieved data as {@link RdfData}
     * @throws RdfParsingException If the response could not be parsed
     * @throws RdfLoadingException If no RDF data could not be loaded
     */
    RdfData load (String uri)  throws RdfParsingException, RdfLoadingException

    /**
     * Load RDF data from the given URI but returns only the resource identified by that URI.
     * @param uri uri The URI to load the data from
     * @return The RdfResource represented by the given URI
     * @throws RdfParsingException If the response could not be parsed
     * @throws RdfLoadingException If no RDF data could not be loaded
     */
    RdfResource loadResource (String uri)  throws RdfParsingException, RdfLoadingException

}