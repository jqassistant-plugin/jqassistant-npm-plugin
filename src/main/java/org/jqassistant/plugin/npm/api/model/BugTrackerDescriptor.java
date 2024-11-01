package org.jqassistant.plugin.npm.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Represents information about where to report bugs.
 */
@Label("BugTracker")
public interface BugTrackerDescriptor extends NPMDescriptor {

    String getEmail();

    void setEmail(String email);

    String getUrl();

    void setUrl(String url);
}
