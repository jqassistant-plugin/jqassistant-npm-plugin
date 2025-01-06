package org.jqassistant.plugin.npm.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents a file for the man program to find.
 */
@Label("Man")
public interface ManDescriptor extends NPMDescriptor {

    String getFile();

    void setFile(String file);

}
