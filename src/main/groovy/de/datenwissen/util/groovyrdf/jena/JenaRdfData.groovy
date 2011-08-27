package de.datenwissen.util.groovyrdf.jena

import java.io.OutputStream
import java.io.StringWriter
import java.io.Writer

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Resource

import de.datenwissen.util.groovyrdf.core.RdfData;
import de.datenwissen.util.groovyrdf.core.RdfDataFormat;


/**
 * This implementation of {@link RdfData} stores the data in ja Jena-Model
 * (@see <a href="http://openjena.org">Jena-Framework</a>).
 *
 */
class JenaRdfData implements RdfData {
	
	private Model jenaModel
	
	private Map<RdfDataFormat, String> formatMap =
		[
			(RdfDataFormat.TURTLE):"TURTLE",
			(RdfDataFormat.RDF_XML):"RDF/XML",
			(RdfDataFormat.RDF_XML_ABBREV):"RDF/XML-ABBREV",
			(RdfDataFormat.N3):"N3",
			(RdfDataFormat.N_TRIPLE):"N-TRIPLE"
		]
	
	public JenaRdfData(Model jenaModel) {
		this.jenaModel = jenaModel
	}
	
	public Model getJenaModel() {
		return jenaModel
	}
	
	protected Object createNode(Model model, Object name) {
		Resource resource = model.createResource(name)
		return new JenaResourceBuilder(resource: resource)
	}
	
	@Override
	public void write(Writer writer, RdfDataFormat format) {
		jenaModel.write(writer, formatMap[format])
	}
	
	@Override
	public void write(OutputStream outputStream, RdfDataFormat format) {
		jenaModel.write(outputStream, formatMap[format])		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.is(obj))
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JenaRdfData other = (JenaRdfData) obj;
		if (jenaModel == null) {
			if (other.jenaModel != null)
				return false;
		} else if (!jenaModel.isIsomorphicWith(other.jenaModel))
			return false;
		return true;
	}
	
	@Override
	public String toString(RdfDataFormat format) {
		StringWriter stringWriter = new StringWriter()
		this.write(stringWriter, format)
		return stringWriter.toString()
	}
	
	@Override
	public String toString() {
		return toString(RdfDataFormat.RDF_XML)
	}
	

}
