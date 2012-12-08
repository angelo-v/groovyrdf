package de.datenwissen.util.groovyrdf.core

/**
 * Thrown when something goes wrong while loading RDF with an RdfLoader instance
 */
class RdfLoadingException extends Exception {
    RdfLoadingException (String message) {
        super(message)
    }
}


