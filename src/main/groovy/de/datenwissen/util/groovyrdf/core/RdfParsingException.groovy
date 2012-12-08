package de.datenwissen.util.groovyrdf.core

/**
 * Thrown when something goes wrong while parsing RDF data from a String
 */
class RdfParsingException extends Exception {

    RdfParsingException (String message, Throwable cause) {
        super(message, cause)
    }

}
