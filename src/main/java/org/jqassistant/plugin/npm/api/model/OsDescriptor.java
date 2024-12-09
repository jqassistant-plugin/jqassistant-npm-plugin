package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents an operating system and its type (supported / blocked).
 */
@Label("Os")
public interface OsDescriptor extends NPMDescriptor, NamedDescriptor {

    String getType();

    void setType(String type);
}
