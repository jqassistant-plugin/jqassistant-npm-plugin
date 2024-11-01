package org.jqassistant.plugin.npm.impl.mapper;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import org.jqassistant.plugin.npm.api.model.BugTrackerDescriptor;
import org.jqassistant.plugin.npm.impl.model.Bugs;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface BugTrackerMapper extends DescriptorMapper<Bugs, BugTrackerDescriptor> {

    @Override
    BugTrackerDescriptor toDescriptor(Bugs value, @Context Scanner scanner);

}
