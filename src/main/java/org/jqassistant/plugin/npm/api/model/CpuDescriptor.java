package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents a cpu architecture and its type (supported / blocked).
 */
@Label("Cpu")
public interface CpuDescriptor extends NPMDescriptor, NamedDescriptor {

    String getType();

    void setType(String type);
}
