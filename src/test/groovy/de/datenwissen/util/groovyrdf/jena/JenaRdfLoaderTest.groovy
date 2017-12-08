package de.datenwissen.util.groovyrdf.jena

import de.datenwissen.util.groovyrdf.core.RdfData
import de.datenwissen.util.groovyrdf.core.RdfDataFormat
import de.datenwissen.util.groovyrdf.core.RdfLoadingException
import de.datenwissen.util.groovyrdf.core.RdfParsingException
import de.datenwissen.util.groovyrdf.core.RdfResource
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static groovyx.net.http.HttpBuilder.configure as configure
import static org.junit.Assert.*

public class JenaRdfLoaderTest {

    @After
    void tearDown() {
        HttpBuilder.metaClass = null
        JenaRdfLoader.metaClass = null
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none()

    @Test
    void testLoadTurtleSuccessfully() {
        testLoadFormatSuccessfully('text/turtle', '''
            @prefix ex: <http://example.com/ontology#> .
            <http://example.com/resource> ex:ample "value".
        ''')
    }

    @Test
    void testLoadRdfXmlSuccessfully() {
        testLoadFormatSuccessfully('application/rdf+xml', '''
            <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:ex="http://example.com/ontology#">
              <rdf:Description rdf:about="http://example.com/resource">
                <ex:ample>value</ex:ample>
              </rdf:Description>
            </rdf:RDF>
        ''')
    }

    @Test
    void testRealWorldRdf() {
        def rdfData = new JenaRdfLoader().load('http://dbpedia.org/resource/Berlin')
        assert rdfData('http://dbpedia.org/resource/Berlin') != null
    }

    @Test
    void testLoadN3Successfully() {
        testLoadFormatSuccessfully('text/n3', '''
            @prefix ex: <http://example.com/ontology#> .
            <http://example.com/resource> ex:ample "value".
        ''')
    }

    @Test
    void testLoadNTriplesSuccessfully() {
        testLoadFormatSuccessfully('text/plain', '''
            <http://example.com/resource> <http://example.com/ontology#ample> "value".
        ''')
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    private void testLoadFormatSuccessfully(String responseContentType, String responseContent) {
        mockRequest(responseContentType, responseContent)
            RdfData rdfData = new JenaRdfLoader().load('http://example.com/resource')
            assertNotNull('Data should be loaded', rdfData)
            assertEquals(rdfData('http://example.com/resource').'http://example.com/ontology#ample', 'value')
        }

    @Test
    void testLoadInvalidTurtleFormat() {
        expectedException.expect(RdfParsingException)
        expectedException.expectMessage('The response did not contain valid RDF data of format TURTLE (text/turtle)')
        mockRequest('text/turtle', 'This is not TURTLE.')
            new JenaRdfLoader().load('http://example.com/resource')
        }

    @Test
    void testLoadInvalidRdfXmlFormat() {
        expectedException.expect(RdfParsingException)
        expectedException.expectMessage('The response did not contain valid RDF data of format RDF/XML (application/rdf+xml)')
        mockRequest('application/rdf+xml', 'This is not RDF/XML.')
            new JenaRdfLoader().load('http://example.com/resource')
        }

    @Test
    void testLoadInvalidN3Format() {
        expectedException.expect(RdfParsingException)
        expectedException.expectMessage('The response did not contain valid RDF data of format N3 (text/n3)')
        mockRequest('text/n3', 'This is not N3.')
            new JenaRdfLoader().load('http://example.com/resource')
        }

    @Test
    void testLoadInvalidNTripleFormat() {
        expectedException.expect(RdfParsingException)
        expectedException.expectMessage('The response did not contain valid RDF data of format N-TRIPLE (text/plain)')
        mockRequest('text/plain', 'This is not RDF.')
            new JenaRdfLoader().load('http://example.com/resource')
        }

    @Test
    void testLoadNonRdfFormatFormat() {
        expectedException.expect(RdfLoadingException)
        expectedException.expectMessage('The server did not respond with RDF. Content-Type text/html could not be processed.')
        mockRequest('text/html', 'This is not RDF.')
            new JenaRdfLoader().load('http://example.com/resource')
        }

    @Test
    void test404Response() {
        expectedException.expect(RdfLoadingException)
        expectedException.expectMessage('The request to http://example.com/resource returned 404 (Not Found)')
        mock404Request()
        new JenaRdfLoader().load('http://example.com/resource')
    }

    @Test
    void testUnexpectedHttpFailure() {
        expectedException.expect(RdfLoadingException)
        expectedException.expectMessage('The request to http://example.com/resource failed unexpectedly: Mocked HTTP Error')
        mockRequestWithFailure()
        new JenaRdfLoader().load('http://example.com/resource')
    }

    private void mockRequest(String responseContentType, String responseContent) {
        def headers = [Accept: null]
        def uri = [fragment: '#it']
        Closure successClosure = null
        def responseCallback = [
                success: { Closure onSuccess -> successClosure = onSuccess},
                failure: { Closure onFailure -> },
                when: { Integer code, Closure onStatus -> }
        ]
        JenaRdfLoader.metaClass.getRequest = { -> [
                headers: headers,
                uri: uri
        ] }
        JenaRdfLoader.metaClass.getResponse = { -> responseCallback }
        def mockHttp = configure {
            request.uri = 'http://mock.example'
        }
        JenaRdfLoader.metaClass.static.configureHttp = { String requestUri -> return mockHttp }
        mockHttp.metaClass.get = { Closure closure ->
            closure()
            assertEquals('application/rdf+xml;q=0.4, text/turtle;q=0.3, text/n3;q=0.2, text/plain;q=0.1', headers.Accept)
            assertNull('Fragment should be stripped off', uri.fragment)
            successClosure(new MockFromServer(contentType: responseContentType), responseContent.bytes)
        }
    }

    private void mock404Request() {
        def headers = [Accept: null]
        def uri = [fragment: '#it']

        Closure on404Closure = null
        def responseCallback = [
                success: { Closure onSuccess -> },
                failure: { Closure onFailure -> },
                when: { Integer code, Closure onStatus -> on404Closure = (code == 404 ? onStatus : null)}
        ]
        JenaRdfLoader.metaClass.getRequest = { -> [
                headers: headers,
                uri: uri
        ] }
        JenaRdfLoader.metaClass.getResponse = { -> responseCallback }
        def mockHttp = configure {
            request.uri = 'http://mock.example'
        }
        JenaRdfLoader.metaClass.static.configureHttp = { String requestUri -> return mockHttp }
        mockHttp.metaClass.get = { Closure closure ->
            closure()
            on404Closure()
        }
    }

    private void mockRequestWithFailure() {
        def headers = [Accept: null]
        def uri = [fragment: '#it']
        Closure failureClosure = null
        def responseCallback = [
                success: { Closure onSuccess -> },
                failure: { Closure onFailure -> failureClosure = onFailure },
                when: { Integer code, Closure onStatus -> }
        ]
        JenaRdfLoader.metaClass.getRequest = { -> [
                headers: headers,
                uri: uri
        ] }
        JenaRdfLoader.metaClass.getResponse = { -> responseCallback }
        def mockHttp = configure {
            request.uri = 'http://mock.example'
        }
        JenaRdfLoader.metaClass.static.configureHttp = { String requestUri -> return mockHttp }
        mockHttp.metaClass.get = { Closure closure ->
            closure()
            failureClosure(new MockFromServer(message: "Mocked HTTP Error"))
        }
    }

    @Test
    void testLoadNull() {
        expectedException.expect(RdfLoadingException)
        expectedException.expectMessage('The request URI must not be null')
        new JenaRdfLoader().load(null)
    }

    @Test
    void testLoadResource() {
        mockRequest('text/turtle', '''
            @prefix ex: <http://example.com/ontology#> .
            <http://example.com/resource> ex:ample "value".
        ''')
        RdfResource resource = new JenaRdfLoader().loadResource('http://example.com/resource')
        assertNotNull('Resource should be loaded', resource)
        assertEquals(resource.'http://example.com/ontology#ample', 'value')
    }

}

class MockFromServer implements FromServer {

    private String contentType
    private String message

    @Override
    InputStream getInputStream() {
        return null
    }

    @Override
    int getStatusCode() {
        return 0
    }

    @Override
    String getMessage() {
        return message
    }

    @Override
    List<FromServer.Header<?>> getHeaders() {
        return null
    }

    @Override
    boolean getHasBody() {
        return false
    }

    @Override
    URI getUri() {
        return null
    }

    @Override
    void finish() {

    }

    Reader getReader() {
        return null
    }

    String getContentType() {
        return this.contentType
    }
}