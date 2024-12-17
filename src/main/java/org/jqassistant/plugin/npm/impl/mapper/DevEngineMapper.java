package org.jqassistant.plugin.npm.impl.mapper;

import java.util.List;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;

import org.jqassistant.plugin.npm.api.model.DevEngineDescriptor;
import org.jqassistant.plugin.npm.impl.model.DevEngine;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface DevEngineMapper extends DescriptorMapper<DevEngine, DevEngineDescriptor> {

    @Override
    DevEngineDescriptor toDescriptor(DevEngine value, @Context Scanner scanner);


    List<DevEngineDescriptor> mapList(List<DevEngine> value, @Context Scanner scanner);
}
