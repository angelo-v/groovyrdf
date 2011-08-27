package de.datenwissen.util.groovyrdf.jena
import java.util.Map

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory

import de.datenwissen.util.groovyrdf.core.RdfBuilder;
import de.datenwissen.util.groovyrdf.core.RdfData;



/**
 * A {@link RdfBuilder} that uses the <a href="http://openjena.org">Jena-Framework</a> to build {@link RdfData}
 *
 */
class JenaRdfBuilder extends BuilderSupport implements RdfBuilder {
	
	Logger log = LoggerFactory.getLogger(JenaRdfBuilder.class);
	
	private static final String TYPE_ATTRIBUTE = "a"
	
	private Model model
	
	@Override
	protected void setParent(Object parent, Object child) {
		if (parent instanceof RdfData) return
		if (child instanceof JenaAbstractResourceBuilder) {
			child.setParent(parent)
		}
	}
	
	@Override
	protected Object createNode(Object name) {
		def currentNode = getCurrent()
		if (!currentNode) {
			log.debug "creating rdf model"
			model = ModelFactory.createDefaultModel()
			return new JenaRdfData(model)
		}
		return currentNode.createNode(model, name)
	}
	
	@Override
	protected Object createNode(Object name, Object value) {
		if (isTypeAttribute(name)) {
			return new JenaTypePropertyBuilder(uri: value)
		}
		return new JenaLiteralPropertyBuilder(uri: name, value: value)
	}
	
	private boolean isTypeAttribute(Object name) {
		return name == TYPE_ATTRIBUTE
	}
	
	@Override
	protected Object createNode(Object name, Map attributes) {
		throw new UnsupportedOperationException()
	}
	
	@Override
	protected Object createNode(Object name, Map attributes, Object value) {
		return new JenaLiteralPropertyBuilder(uri: name, value: value, language: attributes.lang)
	}

}








