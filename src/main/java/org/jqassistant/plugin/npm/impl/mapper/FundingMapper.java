package org.jqassistant.plugin.npm.impl.mapper;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import org.jqassistant.plugin.npm.api.model.FundingDescriptor;
import org.jqassistant.plugin.npm.impl.model.Funding;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface FundingMapper extends DescriptorMapper<Funding, FundingDescriptor> {

    @Override
    FundingDescriptor toDescriptor(Funding value, @Context Scanner scanner);


    List<FundingDescriptor> mapList(List<Funding> value, @Context Scanner scanner);
}
