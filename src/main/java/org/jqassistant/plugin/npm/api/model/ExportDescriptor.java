package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents an entry point (alternative to main).
 */
@Label("Export")
@Abstract
public interface ExportDescriptor extends NPMDescriptor, NamedDescriptor {
}
