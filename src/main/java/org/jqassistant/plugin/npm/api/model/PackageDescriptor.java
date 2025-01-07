package org.jqassistant.plugin.npm.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

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

    @Relation("HAS_EXPORT")
    List<ExportDescriptor> getExports();

    String getMain();

    void setMain(String main);

    String getBrowser();

    void setBrowser(String browser);

    @Relation("HAS_BINARY")
    List<BinaryDescriptor> getBinaries();

    @Relation("HAS_MAN")
    List<ManDescriptor> getMans();

    @Relation("IN_REPOSITORY")
    RepositoryDescriptor getRepository();

    void setRepository(RepositoryDescriptor repository);

    @Relation("HAS_BUG_TRACKER")
    BugTrackerDescriptor getBugTracker();

    void setBugTracker(BugTrackerDescriptor bugs);

    @Relation("HAS_AUTHOR")
    PersonDescriptor getAuthor();

    void setAuthor(PersonDescriptor author);

    @Relation("HAS_CONTRIBUTOR")
    List<PersonDescriptor> getContributors();

    @Relation("HAS_FUNDING")
    List<FundingDescriptor> getFunding();

    @Relation("DECLARES_SCRIPT")
    List<ScriptDescriptor> getScripts();

    @Relation("DECLARES_CONFIG")
    List<ConfigDescriptor> getConfig();

    @Relation("DECLARES_DEPENDENCY")
    List<DependencyDescriptor> getDependencies();

    @Relation("DECLARES_DEV_DEPENDENCY")
    List<DependencyDescriptor> getDevDependencies();

    @Relation("DECLARES_PEER_DEPENDENCY")
    List<DependencyDescriptor> getPeerDependencies();

    @Relation("HAS_BUNDLED_DEPENDENCY")
    List<DependencyDescriptor> getBundledDependencies();

    @Relation("HAS_OVERRIDES")
    List<OverridesDescriptor> getOverrides();

    @Relation("DECLARES_ENGINE")
    List<EngineDescriptor> getEngines();

    @Relation("DECLARES_OS")
    List<OsDescriptor> getOs();

    @Relation("DECLARES_CPU")
    List<CpuDescriptor> getCpu();

    @Relation("DECLARES_DEV_ENGINE")
    List<DevEngineDescriptor> getDevEngines();

    @Relation("DECLARES_PUBLISH_CONFIG")
    List<PublishConfigDescriptor> getPublishConfig();

    @Relation("DECLARES_WORKSPACE")
    List<WorkspaceDescriptor> getWorkspaces();

    String getPrivate();

    void setPrivate(String privat);
}
