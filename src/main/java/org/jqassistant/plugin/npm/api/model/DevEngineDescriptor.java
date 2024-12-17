package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents a developer engine.
 */
@Label("DevEngine")
public interface DevEngineDescriptor extends NPMDescriptor, NamedDescriptor {

    String getType();

    void setType(String type);

    String getVersion();

    void setVersion(String version);

    String getOnFail();

    void setOnFail(String onFail);

}
