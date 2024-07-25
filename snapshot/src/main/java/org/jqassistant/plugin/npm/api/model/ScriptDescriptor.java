package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents a script.
 */
@Label("Script")
public interface ScriptDescriptor extends NPMDescriptor, NamedDescriptor {

    String getScript();

    void setScript(String script);

}
