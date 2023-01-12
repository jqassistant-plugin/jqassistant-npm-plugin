package org.jqassistant.contrib.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents a person including contact data.
 */
@Label("Person")
public interface PersonDescriptor extends NPMDescriptor, NamedDescriptor {

    String getEmail();

    void setEmail(String email);

    String getUrl();

    void setUrl(String url);
}
