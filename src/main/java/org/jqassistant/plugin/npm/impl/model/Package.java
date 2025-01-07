package org.jqassistant.plugin.npm.impl.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jqassistant.plugin.npm.impl.PackageJsonDeserializer;

import java.util.List;
import java.util.Map;

/**
 * Used for unmarshalling package.json files using Jackson.
 */
@Getter
@Setter
@ToString
@JsonDeserialize(using = PackageJsonDeserializer.class)
public class Package {

    private String name;

    private String version;

    private String description;

    private String[] keywords;

    private String homepage;

    private Bugs bugs;

    private String license;

    private Person author;

    private List<Person> contributors;

    private List<Funding> funding;

    private String[] files;

    private Map<String, String> exports;

    private String main;

    private String browser;

    private List<Binary> bin;

    private String[] man;

    private Repository repository;

    private Map<String, String> scripts;

    private Map<String, String> config;

    private Map<String, String> dependencies;

    private Map<String, String> devDependencies;

    private Map<String, String> peerDependencies;

    private Map<String, Boolean> peerDependenciesMeta;

    private BundleDependencies bundleDependencies;

    private Map<String, String> optionalDependencies;

    private Map<String, String> overrides;

    private Map<String, String> engines;

    private String[] os;

    private String[] cpu;

    private List<DevEngine> devEngines;

    private String privat;

    private Map<String, String> publishConfig;

    private String[] workspaces;

}
