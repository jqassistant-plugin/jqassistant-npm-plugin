package org.jqassistant.plugin.npm.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents the repository of the package.
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
