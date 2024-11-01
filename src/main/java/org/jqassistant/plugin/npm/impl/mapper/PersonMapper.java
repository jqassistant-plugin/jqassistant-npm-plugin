package org.jqassistant.plugin.npm.impl.mapper;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import org.jqassistant.plugin.npm.api.model.PersonDescriptor;
import org.jqassistant.plugin.npm.impl.model.Person;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PersonMapper extends DescriptorMapper<Person, PersonDescriptor> {

    @Override
    PersonDescriptor toDescriptor(Person value, @Context Scanner scanner);


    List<PersonDescriptor> mapList(List<Person> value, @Context Scanner scanner);
}
