package org.jqassistant.contrib.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents a dependency.
 */
@Label("Dependency")
public interface DependencyDescriptor extends NPMDescriptor, NamedDescriptor {

    String getDependency();

    void setDependency(String dependency);

}
