package de.datenwissen.util.groovyrdf.jena

import com.hp.hpl.jena.n3.turtle.TurtleParseException
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.shared.JenaException
import com.hp.hpl.jena.shared.SyntaxError
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import de.datenwissen.util.groovyrdf.core.*

/**
 * Implementation of RdfLoader, that parses the response with Jena and creates JenaRdfData in result
 */
class JenaRdfLoader implements RdfLoader {


    private static final String ACCEPT_HEADER = 'application/rdf+xml;q=0.4, text/turtle;q=0.3, text/n3;q=0.2, text/plain;q=0.1'

    @Override
    RdfData load (String requestUri) throws RdfParsingException, RdfLoadingException {
        if  (!requestUri) throw new RdfLoadingException('The request URI must not be null')
        def http = new HTTPBuilder (requestUri)
        def result = null
        http.request (Method.GET, ContentType.TEXT) { req ->
            headers.Accept = ACCEPT_HEADER
            uri.fragment = null
            response.success = { def response, def reader ->
                def model = ModelFactory.createDefaultModel ()
                RdfDataFormat rdfDataFormat = determineRdfFormat (response)
                tryToReadModel (model, reader, rdfDataFormat)
                result = new JenaRdfData (model)
            }
            response.'404' = {
                throw new RdfLoadingException("The request to $requestUri returned 404 (Not Found)")
            }
            response.failure = {resp ->
                throw new RdfLoadingException("The request to $requestUri failed unexpectedly: $resp.statusLine")
            }
        }
        return result
    }

    private RdfDataFormat determineRdfFormat (response) {
        def rdfDataFormat = RdfDataFormat.findByMimeType (response.contentType)
        if (!rdfDataFormat) {
            throw new RdfLoadingException (
                    "The server did not respond with RDF. Content-Type $response.contentType could not be processed."
            )
        }
        return rdfDataFormat
    }

    private void tryToReadModel (Model model, Reader reader, RdfDataFormat rdfDataFormat) {
        try {
            model.read (reader, '', rdfDataFormat.jenaFormat)
        } catch (TurtleParseException ex) {
            handleParseError (rdfDataFormat, ex)
        } catch (SyntaxError err) {
            handleParseError (rdfDataFormat, err)
        } catch (JenaException ex) {
            handleParseError (rdfDataFormat, ex)
        }
    }

    private void handleParseError (RdfDataFormat rdfDataFormat, Throwable cause) {
        throw new RdfParsingException ("The response did not contain valid RDF data of format $rdfDataFormat.jenaFormat ($rdfDataFormat.mimeType)", cause)
    }

    @SuppressWarnings ("GroovyAssignabilityCheck")
    @Override
    RdfResource loadResource (String uri) throws RdfParsingException, RdfLoadingException {
        RdfData rdfData = load (uri)
        return rdfData (uri)
    }
}
