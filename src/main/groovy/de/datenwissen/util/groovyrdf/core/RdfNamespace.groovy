package de.datenwissen.util.groovyrdf.core

/**
 * Use this class to easily set up namespaces.
 * 
 * Any call to a property of {@link RdfNamespace} will return a String of the form <code>"$baseUri$propertyName"<code>.
 * 
 * <br/><br/>
 * 
 * Examples:
 * 
 * <pre>
 * {@code
 * def vocab = new RdfNamespace("http://example.com/vocab/")
 * assert vocab.name == "http://example.com/vocab/name"
 * assert vocab.location == "http://example.com/vocab/location"
 * assert vocab.anything == "http://example.com/vocab/anything"
 * }
 * </pre>
 *
 */
class RdfNamespace {
	
	String baseUri
	
	/**
	 * @param baseUri The base URI of this namespace
	 */
	RdfNamespace(String baseUri) {
		this.baseUri = baseUri
	}
	
	/**
	 * Do not call this method directly. Just call <code>rdfNamespaceInstance.anythingYouLike</code>
	 */
	def propertyMissing(String property){
		return "${baseUri}${property}".toString()
	}

	@Override
	public String toString() {
		return baseUri
	}

}
