package org.jqassistant.plugin.npm.impl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Repository {

    private String type;

    private String url;

    private String directory;

}
