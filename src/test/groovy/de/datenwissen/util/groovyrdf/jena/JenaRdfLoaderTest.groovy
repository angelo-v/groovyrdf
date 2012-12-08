package de.datenwissen.util.groovyrdf.jena;


import org.junit.Test

import static org.junit.Assert.*
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method

import static org.junit.Assert.assertEquals
import de.datenwissen.util.groovyrdf.core.RdfData
import de.datenwissen.util.groovyrdf.core.RdfParsingException
import org.junit.rules.ExpectedException
import org.junit.Rule
import org.junit.After
import de.datenwissen.util.groovyrdf.core.RdfLoadingException
import de.datenwissen.util.groovyrdf.core.RdfResource

public class JenaRdfLoaderTest {

    @After
    void tearDown () {
        HTTPBuilder.metaClass = null
        JenaRdfLoader.metaClass = null
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none()

    @Test
    void testLoadTurtleSuccessfully () {
        testLoadFormatSuccessfully ('text/turtle', '''
            @prefix ex: <http://example.com/ontology#> .
            <http://example.com/resource> ex:ample "value".
        ''')
    }

    @Test
    void testLoadRdfXmlSuccessfully (){
        testLoadFormatSuccessfully ('application/rdf+xml', '''
            <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:ex="http://example.com/ontology#">
              <rdf:Description rdf:about="http://example.com/resource">
                <ex:ample>value</ex:ample>
              </rdf:Description>
            </rdf:RDF>
        ''')
    }

    @Test
    void testLoadN3Successfully () {
        testLoadFormatSuccessfully ('text/n3', '''
            @prefix ex: <http://example.com/ontology#> .
            <http://example.com/resource> ex:ample "value".
        ''')
    }

    @Test
    void testLoadNTriplesSuccessfully () {
        testLoadFormatSuccessfully ('text/plain', '''
            <http://example.com/resource> <http://example.com/ontology#ample> "value".
        ''')
    }

    @SuppressWarnings ("GroovyAssignabilityCheck")
    private void testLoadFormatSuccessfully (String responseContentType, String responseContent) {
        mockRequest (responseContentType, responseContent)
        RdfData rdfData = new JenaRdfLoader ().load ('http://example.com/resource')
        assertNotNull ('Data should be loaded', rdfData)
        assertEquals (rdfData ('http://example.com/resource').'http://example.com/ontology#ample', 'value')
    }

    @Test
    void testLoadInvalidTurtleFormat () {
        expectedException.expect(RdfParsingException)
        expectedException.expectMessage('The response did not contain valid RDF data of format TURTLE (text/turtle)')
        mockRequest ('text/turtle', 'This is not TURTLE.')
        new JenaRdfLoader ().load ('http://example.com/resource')
    }

    @Test
    void testLoadInvalidRdfXmlFormat () {
        expectedException.expect(RdfParsingException)
        expectedException.expectMessage('The response did not contain valid RDF data of format RDF/XML (application/rdf+xml)')
        mockRequest ('application/rdf+xml', 'This is not RDF/XML.')
        new JenaRdfLoader ().load ('http://example.com/resource')
    }

    @Test
    void testLoadInvalidN3Format () {
        expectedException.expect(RdfParsingException)
        expectedException.expectMessage('The response did not contain valid RDF data of format N3 (text/n3)')
        mockRequest ('text/n3', 'This is not N3.')
        new JenaRdfLoader ().load ('http://example.com/resource')
    }

    @Test
    void testLoadInvalidNTripleFormat () {
        expectedException.expect(RdfParsingException)
        expectedException.expectMessage('The response did not contain valid RDF data of format N-TRIPLE (text/plain)')
        mockRequest ('text/plain', 'This is not RDF.')
        new JenaRdfLoader ().load ('http://example.com/resource')
    }

    @Test
    void testLoadNonRdfFormatFormat () {
        expectedException.expect(RdfLoadingException)
        expectedException.expectMessage('The server did not respond with RDF. Content-Type text/html could not be processed.')
        mockRequest ('text/html', 'This is not RDF.')
        new JenaRdfLoader ().load ('http://example.com/resource')
    }

    @Test
    void test404Response () {
        expectedException.expect(RdfLoadingException)
        expectedException.expectMessage('The request to http://example.com/resource returned 404 (Not Found)')
        mock404Request ()
        new JenaRdfLoader ().load ('http://example.com/resource')
    }

    @Test
    void testUnexpectedHttpFailure () {
        expectedException.expect(RdfLoadingException)
        expectedException.expectMessage('The request to http://example.com/resource failed unexpectedly: Mocked HTTP Error')
        mockRequestWithFailure ()
        new JenaRdfLoader ().load ('http://example.com/resource')
    }

    private void mockRequest (String responseContentType, String responseContent) {
        def response = [contentType: responseContentType]
        def reader = new StringReader (responseContent)
        def headers = [Accept: null]
        def uri = [fragment: '#it']
        def responseCallback = [success: null, '404': null]
        JenaRdfLoader.metaClass.getHeaders = {-> headers}
        JenaRdfLoader.metaClass.getUri = {-> uri}
        JenaRdfLoader.metaClass.getResponse = {-> responseCallback}

        HTTPBuilder.metaClass.request = {Method method, ContentType contentType, Closure closure ->
            assertEquals (Method.GET, method)
            assertEquals (ContentType.TEXT, contentType)
            closure ()
            assertEquals ('application/rdf+xml;q=0.4, text/turtle;q=0.3, text/n3;q=0.2, text/plain;q=0.1', headers.Accept)
            assertNull ('Fragment should be stripped off', uri.fragment)
            responseCallback.success (response, reader)
        }
    }

    private void mock404Request () {
        def headers = [Accept: null]
        def uri = [fragment: '#it']
        def responseCallback = [success: null, '404': null]
        JenaRdfLoader.metaClass.getHeaders = {-> headers}
        JenaRdfLoader.metaClass.getUri = {-> uri}
        JenaRdfLoader.metaClass.getResponse = {-> responseCallback}
        HTTPBuilder.metaClass.request = {Method method, ContentType contentType, Closure closure ->
            closure ()
            responseCallback.'404' ()
        }
    }

    private void mockRequestWithFailure () {
        def headers = [Accept: null]
        def uri = [fragment: '#it']
        def responseCallback = [success: null, '404': null, failure: null]
        JenaRdfLoader.metaClass.getHeaders = {-> headers}
        JenaRdfLoader.metaClass.getUri = {-> uri}
        JenaRdfLoader.metaClass.getResponse = {-> responseCallback}
        HTTPBuilder.metaClass.request = {Method method, ContentType contentType, Closure closure ->
            closure ()
            responseCallback.failure ([statusLine: 'Mocked HTTP Error'])
        }
    }

    @Test
    void testLoadNull () {
        expectedException.expect(RdfLoadingException)
        expectedException.expectMessage('The request URI must not be null')
        new JenaRdfLoader ().load (null)
    }

    @Test
    void testLoadResource () {
        mockRequest ('text/turtle', '''
            @prefix ex: <http://example.com/ontology#> .
            <http://example.com/resource> ex:ample "value".
        ''')
        RdfResource resource = new JenaRdfLoader ().loadResource ('http://example.com/resource')
        assertNotNull ('Resource should be loaded', resource)
        assertEquals (resource.'http://example.com/ontology#ample', 'value')
    }

}
