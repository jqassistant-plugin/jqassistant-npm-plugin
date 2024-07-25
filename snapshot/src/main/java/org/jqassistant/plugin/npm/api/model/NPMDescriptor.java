package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Base label for NPM related nodes.
 */
@Label("NPM")
@Abstract
public interface NPMDescriptor extends Descriptor {
}
