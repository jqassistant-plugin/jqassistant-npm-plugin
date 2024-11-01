package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
/**
 * Represents an engine.
 */
@Label("Engine")
public interface EngineDescriptor extends NPMDescriptor, NamedDescriptor {

    String getVersionRange();

    void setVersionRange(String versionRange);

}
