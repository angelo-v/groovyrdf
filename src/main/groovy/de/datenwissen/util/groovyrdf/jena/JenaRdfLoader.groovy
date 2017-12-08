package de.datenwissen.util.groovyrdf.jena

import de.datenwissen.util.groovyrdf.core.*
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import org.apache.jena.n3.turtle.TurtleParseException
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.shared.JenaException
import org.apache.jena.shared.SyntaxError

import static groovyx.net.http.HttpBuilder.configure

/**
 * Implementation of RdfLoader, that parses the response with Jena and creates JenaRdfData in result
 */
class JenaRdfLoader implements RdfLoader {


    private static
    final String ACCEPT_HEADER = 'application/rdf+xml;q=0.4, text/turtle;q=0.3, text/n3;q=0.2, text/plain;q=0.1'

    @Override
    RdfData load(String requestUri) throws RdfParsingException, RdfLoadingException {
        if (!requestUri) throw new RdfLoadingException('The request URI must not be null')
        HttpBuilder http = configureHttp(requestUri)
        def result = null
        http.get {
            request.headers.Accept = ACCEPT_HEADER
            request.uri.fragment = null
            response.when(404) {
                throw new RdfLoadingException("The request to $requestUri returned 404 (Not Found)")
            }
            response.success { FromServer fs, byte[] content ->
                def model = ModelFactory.createDefaultModel()
                RdfDataFormat rdfDataFormat = determineRdfFormat(fs.contentType)
                tryToReadModel(model, new ByteArrayInputStream(content), rdfDataFormat)
                result = new JenaRdfData(model)
            }
            response.failure { FromServer fs ->
                throw new RdfLoadingException("The request to $requestUri failed unexpectedly: ${fs.message}")
            }
        }
        return result
    }

    static HttpBuilder configureHttp(String requestUri) {
        return configure {
            request.uri = requestUri
        }
    }

    private static RdfDataFormat determineRdfFormat(String contentType) {
        def rdfDataFormat = RdfDataFormat.findByMimeType(contentType)
        if (!rdfDataFormat) {
            throw new RdfLoadingException(
                    "The server did not respond with RDF. Content-Type $contentType could not be processed."
            )
        }
        return rdfDataFormat
    }

    private void tryToReadModel(Model model, InputStream stream, RdfDataFormat rdfDataFormat) {
        try {
            model.read(stream, '', rdfDataFormat.jenaFormat)
        } catch (TurtleParseException ex) {
            handleParseError(rdfDataFormat, ex)
        } catch (SyntaxError err) {
            handleParseError(rdfDataFormat, err)
        } catch (JenaException ex) {
            handleParseError(rdfDataFormat, ex)
        }
    }

    private void handleParseError(RdfDataFormat rdfDataFormat, Throwable cause) {
        throw new RdfParsingException("The response did not contain valid RDF data of format $rdfDataFormat.jenaFormat ($rdfDataFormat.mimeType)", cause)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @Override
    RdfResource loadResource(String uri) throws RdfParsingException, RdfLoadingException {
        RdfData rdfData = load(uri)
        return rdfData(uri)
    }
}
