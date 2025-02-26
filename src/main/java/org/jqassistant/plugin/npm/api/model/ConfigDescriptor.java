package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents a configuration parameter.
 */
@Label("Config")
public interface ConfigDescriptor extends NPMDescriptor, NamedDescriptor {

    String getValue();

    void setValue(String value);

}
