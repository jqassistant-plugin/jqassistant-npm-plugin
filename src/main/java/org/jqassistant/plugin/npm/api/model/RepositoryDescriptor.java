package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.DirectoryDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Represents the repository of the package.
 */
@Label("Repository")
public interface RepositoryDescriptor extends NPMDescriptor {

    String getType();

    void setType(String type);

    String getUrl();

    void setUrl(String url);

    @Relation("IN_REPOSITORY")
    DirectoryDescriptor getDirectory();

    void setDirectory(DirectoryDescriptor directory);
}
