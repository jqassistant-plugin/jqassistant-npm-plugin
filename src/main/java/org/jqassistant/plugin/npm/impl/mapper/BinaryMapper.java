package org.jqassistant.plugin.npm.impl.mapper;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import org.jqassistant.plugin.npm.api.model.BinaryDescriptor;
import org.jqassistant.plugin.npm.impl.model.Binary;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BinaryMapper extends DescriptorMapper<Binary, BinaryDescriptor> {

    @Override
    BinaryDescriptor toDescriptor(Binary value, @Context Scanner scanner);


    List<BinaryDescriptor> mapList(List<Binary> value, @Context Scanner scanner);
}
