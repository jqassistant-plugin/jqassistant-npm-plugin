package org.jqassistant.plugin.npm.impl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class BundleDependencies {

    List<String> dependencies = new ArrayList<>();

    Boolean allBundled = false;

}
