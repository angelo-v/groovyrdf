package de.datenwissen.util.groovyrdf.jena

import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.RDFS
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Used to build a RDF resource. The resource might be nested in a property of another resource, like
 *
 * <pre>{@code<resource> vocab:Friend <other>}</pre>
 */
class JenaResourceBuilder extends JenaAbstractResourceBuilder {

    Logger log = LoggerFactory.getLogger (JenaResourceBuilder.class);

    private Resource resource

    protected void setParent (Object parent) {
        JenaNestedPropertyBuilder nested = (JenaNestedPropertyBuilder) parent
        Property currentProperty = nested.property
        Resource currentResource = nested.currentResource
        log.debug "adding resource $resource to property $currentProperty of resource $currentResource"
        currentResource.addProperty (currentProperty, resource)
    }

    @Override
    protected Object createNode (Model model, Object name) {
        return new JenaNestedPropertyBuilder (property: ResourceFactory.createProperty (name))
    }

    protected Object addPublicKey (Model model, String keyUri, String label, String modulus, int exponent) {
        def key = model.createResource (keyUri)
        key.addProperty (RDF.type, ResourceFactory.createResource ('http://www.w3.org/ns/auth/cert#RSAPublicKey'))
        if (label) {
            key.addProperty (RDFS.label, label)
        }
        key.addProperty (ResourceFactory.createProperty ("http://www.w3.org/ns/auth/cert#modulus"), modulus, XSDDatatype.XSDhexBinary)
        key.addProperty (ResourceFactory.createProperty ("http://www.w3.org/ns/auth/cert#exponent"), exponent.toString (), XSDDatatype.XSDinteger)
        resource.addProperty (ResourceFactory.createProperty ("http://www.w3.org/ns/auth/cert#key"), key)
    }

    @Override
    public String toString () {
        return "$resource"
    }

}
