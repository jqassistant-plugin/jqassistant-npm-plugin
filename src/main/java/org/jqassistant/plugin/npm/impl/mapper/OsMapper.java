package org.jqassistant.plugin.npm.impl.mapper;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import org.jqassistant.plugin.npm.api.model.OsDescriptor;
import org.jqassistant.plugin.npm.impl.model.Os;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface OsMapper extends DescriptorMapper<Os, OsDescriptor> {

    @Override
    OsDescriptor toDescriptor(Os value, @Context Scanner scanner);

}
