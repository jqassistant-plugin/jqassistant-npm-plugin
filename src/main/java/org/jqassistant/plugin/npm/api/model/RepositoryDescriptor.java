package org.jqassistant.plugin.npm.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents an entry point (alternative to main).
 */
@Label("Repository")
public interface RepositoryDescriptor extends NPMDescriptor {

    String getType();

    void setType(String type);

    String getUrl();

    void setUrl(String url);

    String getDirectory();

    void setDirectory(String directory);
}
