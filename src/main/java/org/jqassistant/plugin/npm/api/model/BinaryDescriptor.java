package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents information about executable files of the package that should be installed in PATH.
 */
@Label("Binary")
public interface BinaryDescriptor extends NPMDescriptor, NamedDescriptor {

    String getPath();

    void setPath(String path);
}
