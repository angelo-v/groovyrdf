package de.datenwissen.util.groovyrdf.core

/**
 * Any RDF data without a specific representation
 *
 */
interface RdfData {

    /**
     * Writes the data to a {@link Writer} in a representation like RDF/XML or N3
     * @param writer The {@link Writer} to write to
     * @param format The format of the representation
     */
    void write (Writer writer, RdfDataFormat format)

    /**
     * Writes the data to an {@link OutputStream} in a representation like RDF/XML or N3
     * @param outputStream The {@link OutputStream} to write to
     * @param format The format of the representation
     */
    void write (OutputStream outputStream, RdfDataFormat format)

    /**
     * Lists the URIs of the subjects present in this RdfData
     * @return The subject URIs as List<String>
     */
    List<String> listSubjects ()

    /**
     * Lists the URIs of the subjects present in this RdfData, that have the given type.
     * @param typeUri The URI of the desired type
     * @return The subject URIs as List<String>
     */
    List<String> listSubjects (String typeUri)

    /**
     * Converts the data to a {@link String} in a representation like RDF/XML or N3
     * @param format The format of the representation
     * @return The data as {@link String} in the given format
     */
    String toString (RdfDataFormat format)

}
