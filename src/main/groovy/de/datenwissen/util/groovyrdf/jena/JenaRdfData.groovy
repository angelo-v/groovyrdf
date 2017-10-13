package de.datenwissen.util.groovyrdf.jena

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.Resource

import de.datenwissen.util.groovyrdf.core.RdfData;
import de.datenwissen.util.groovyrdf.core.RdfDataFormat
import org.apache.jena.vocabulary.RDF
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.rdf.model.ResIterator
import de.datenwissen.util.groovyrdf.core.RdfResource;

/**
 * This implementation of {@link RdfData} stores the data in ja Jena-Model
 * (@see <a href="http://openjena.org">Jena-Framework</a>).
 *
 */
class JenaRdfData implements RdfData {

    private Model jenaModel

    public JenaRdfData (Model jenaModel) {
        this.jenaModel = jenaModel
    }

    public Model getJenaModel () {
        return jenaModel
    }

    @SuppressWarnings ("GroovyAssignabilityCheck")
    protected Object createNode (Model model, Object name) {
        Resource resource = model.createResource (name)
        return new JenaResourceBuilder (resource: resource)
    }

    @Override
    public void write (Writer writer, RdfDataFormat format) {
        jenaModel.write (writer, format.jenaFormat)
    }

    @Override
    public void write (OutputStream outputStream, RdfDataFormat format) {
        jenaModel.write (outputStream, format.jenaFormat)
    }

    def propertyMissing (String resourceUri) {
        return getRdfResource (resourceUri)
    }

    def call (String resourceUri) {
        return getRdfResource (resourceUri)
    }

    private RdfResource getRdfResource (String resourceUri) {
        Resource resource = jenaModel.getResource (resourceUri)
        return new JenaRdfResource (resource)
    }

    @Override
    public boolean equals (Object obj) {
        if (this.is (obj))
            return true;
        if (obj == null)
            return false;
        if (getClass () != obj.getClass ())
            return false;
        JenaRdfData other = (JenaRdfData) obj;
        if (jenaModel == null) {
            if (other.jenaModel != null)
                return false;
        } else if (!other.jenaModel || !jenaModel.isIsomorphicWith (other.jenaModel))
            return false;
        return true;
    }

    @Override
    public String toString (RdfDataFormat format) {
        StringWriter stringWriter = new StringWriter ()
        this.write (stringWriter, format)
        return stringWriter.toString ()
    }

    @Override
    List<RdfResource> listSubjects () {
        Iterator<Resource> subjects = jenaModel.listSubjects ()
        return collectSubjects (subjects)
    }

    @Override
    List<RdfResource> listSubjects (String typeUri) {
        Iterator<Resource> subjects = jenaModel.listSubjectsWithProperty (RDF.type, ResourceFactory.createResource (typeUri))
        return collectSubjects (subjects)
    }

    private List<RdfResource> collectSubjects (ResIterator subjects) {
        List<RdfResource> subjectUris = []
        while (subjects.hasNext ()) {
            def uri = subjects.next ().getURI ()
            subjectUris.add (getRdfResource(uri))
        }
        return subjectUris
    }

    @Override
    public String toString () {
        return toString (RdfDataFormat.TURTLE)
    }


}
