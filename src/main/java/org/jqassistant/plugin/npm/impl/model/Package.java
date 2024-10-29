package org.jqassistant.plugin.npm.impl.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jqassistant.plugin.npm.impl.PackageJsonDeserializer;

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

    private String license;

    private Person author;

    private List<Person> contributors;

    private String[] files;

    private String main;

    private Map<String, String> scripts;

    private Map<String, String> dependencies;

    private Map<String, String> devDependencies;

    private Map<String, String> engines;

}
