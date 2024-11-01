package org.jqassistant.plugin.npm.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents information about where to fund a package.
 */
@Label("Funding")
public interface FundingDescriptor extends NPMDescriptor {

    String getType();

    void setType(String type);

    String getUrl();

    void setUrl(String url);
}
