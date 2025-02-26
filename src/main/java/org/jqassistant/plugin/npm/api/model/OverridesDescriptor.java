package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents a change to a dependency.
 */
@Label("Overrides")
public interface OverridesDescriptor extends NPMDescriptor, NamedDescriptor {

    String getVersion();

    void setVersion(String version);

}
