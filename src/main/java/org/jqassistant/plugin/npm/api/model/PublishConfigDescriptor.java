package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("PublishConfig")
public interface PublishConfigDescriptor extends NPMDescriptor, NamedDescriptor {

    String getValue();

    void setValue(String value);
}
