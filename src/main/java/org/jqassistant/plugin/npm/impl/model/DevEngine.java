package org.jqassistant.plugin.npm.impl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DevEngine {

    private String name;

    private String type;

    private String version;

    private String onFail;

}
