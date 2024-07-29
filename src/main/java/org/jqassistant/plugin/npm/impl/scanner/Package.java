package org.jqassistant.plugin.npm.impl.scanner;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Used for unmarshalling package.json files using Jackson.
 */
@Getter
@Setter
@ToString
public class Package {

    private String name;
    private String version;

    private String description;

    private String[] keywords;

    private String homepage;

    private String license;

    @JsonDeserialize(using = AuthorDeserializer.class)
    private Map<String, Object> author; //Object -> String oder Map<String, Object>
    @JsonDeserialize(using = ContributorsDeserializer.class)
    private List<Map<String,Object>> contributors;

    private String[] files;

    private String main;

    private Map<String, String> scripts;

    private Map<String, String> dependencies;

    private Map<String, String> devDependencies;

    private Map<String, String> engines;



}
