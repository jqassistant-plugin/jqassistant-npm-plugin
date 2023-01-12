package org.jqassistant.contrib.plugin.npm.api.model;

import java.util.List;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Represents a package.json file.
 */
@Label("Package")
public interface PackageDescriptor extends NPMDescriptor, NamedDescriptor {

    String getVersion();

    void setVersion(String version);

    String getDescription();

    void setDescription(String description);

    String[] getKeywords();

    void setKeywords(String[] keywords);

    String getHomepage();

    void setHomepage(String homepage);

    String getLicense();

    void setLicense(String license);

    String[] getFiles();

    void setFiles(String[] files);

    String getMain();

    void setMain(String main);

    @Relation("HAS_AUTHOR")
    PersonDescriptor getAuthor();

    void setAuthor(PersonDescriptor author);

    @Relation("HAS_CONTRIBUTOR")
    List<PersonDescriptor> getContributors();

    @Relation("DECLARES_SCRIPT")
    List<ScriptDescriptor> getScripts();

    @Relation("DECLARES_DEPENDENCY")
    List<DependencyDescriptor> getDependencies();

    @Relation("DECLARES_DEV_DEPENDENCY")
    List<DependencyDescriptor> getDevDependencies();

    @Relation("DECLARES_ENGINE")
    List<EngineDescriptor> getEngines();
}
