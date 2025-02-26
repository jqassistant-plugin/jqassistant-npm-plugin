package org.jqassistant.plugin.npm.impl.mapper;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import org.jqassistant.plugin.npm.api.model.RepositoryDescriptor;
import org.jqassistant.plugin.npm.impl.model.Repository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface RepositoryMapper extends DescriptorMapper<Repository, RepositoryDescriptor> {

    @Override
    RepositoryDescriptor toDescriptor(Repository value, @Context Scanner scanner);
}
