package de.datenwissen.util.groovyrdf.core


/**
 * Builds {@link RdfData}. You may instantiate an {@link RdfBuilder} with a {@link RdfBuilderFactory}.
 * 
 * <br/><br/>
 * 
 * Usage:
 * <pre>
 * rdfBuilderInstance {
 *  "$subject" {
 *    predicate "literal" 
 *    predicate {
 *      "$other" {
 *        predicate "literal"
 *      }
 *    }
 *   }
 * }
 * </pre>
 * 
 * For examples take a look at the unit tests and the user guide.
 * 
 */
interface RdfBuilder {
}