package de.datenwissen.util.groovyrdf.core

/**
 * Thrown when a WebID / PublicKey is not specified correctly, e.g. modulus is missing
 */
class InvalidWebIdException extends Exception {
    InvalidWebIdException (String s) {
        super(s)
    }
}
