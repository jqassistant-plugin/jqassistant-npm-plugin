package org.jqassistant.plugin.npm.impl.mapper;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import org.jqassistant.plugin.npm.api.model.RepositoryDescriptor;
import org.jqassistant.plugin.npm.impl.model.Repository;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RepositoryMapper extends DescriptorMapper<Repository, RepositoryDescriptor> {

    @Mapping(target = "directory", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = "directory")
    @Override
    RepositoryDescriptor toDescriptor(Repository value, @Context Scanner scanner);
}
