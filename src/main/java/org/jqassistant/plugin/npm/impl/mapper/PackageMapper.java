package org.jqassistant.plugin.npm.impl.mapper;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONFileDescriptor;
import org.jqassistant.plugin.npm.api.model.DependencyDescriptor;
import org.jqassistant.plugin.npm.api.model.EngineDescriptor;
import org.jqassistant.plugin.npm.api.model.PackageDescriptor;
import org.jqassistant.plugin.npm.api.model.ScriptDescriptor;
import org.jqassistant.plugin.npm.impl.model.Package;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(uses = {PersonMapper.class})
public interface PackageMapper extends DescriptorMapper<Package, PackageDescriptor> {

    PackageMapper INSTANCE = getMapper(PackageMapper.class);

    @Override
    @Mapping(source = "scripts", target = "scripts", qualifiedByName = "scriptsMapping")
    @Mapping(source = "dependencies", target = "dependencies", qualifiedByName = "dependencyMapping")
    @Mapping(source = "devDependencies", target = "devDependencies", qualifiedByName = "dependencyMapping")
    @Mapping(source = "engines", target = "engines", qualifiedByName = "engineMapping")
    PackageDescriptor toDescriptor(Package value, @Context Scanner scanner);

    @Named("scriptsMapping")
    default List<ScriptDescriptor> scriptsMapping(Map<String, String> sourceField, @Context Scanner scanner) {
        return mapMapProperty(sourceField, ScriptDescriptor.class, ScriptDescriptor::setScript, scanner);
    }

    @Named("dependencyMapping")
    default List<DependencyDescriptor> dependencyMapping(Map<String, String> sourceField, @Context Scanner scanner) {
        return mapMapProperty(sourceField, DependencyDescriptor.class, DependencyDescriptor::setVersionRange, scanner);
    }

    @Named("engineMapping")
    default List<EngineDescriptor> engineMapping(Map<String, String> sourceField, @Context Scanner scanner) {
        return mapMapProperty(sourceField, EngineDescriptor.class, EngineDescriptor::setVersionRange, scanner);
    }

    static <T extends NamedDescriptor> List<T> mapMapProperty(Map<String, String> map, Class<T> descriptorType, BiConsumer<T, String> valueConsumer, Scanner scanner) {
        if (map != null) {
            Store store = scanner.getContext().getStore();
            return map.entrySet()
                .stream()
                .map(entry -> {
                    T descriptor = store.create(descriptorType);
                    descriptor.setName(entry.getKey());
                    valueConsumer.accept(descriptor, entry.getValue());
                    return descriptor;
                })
                .collect(toList());
        }
        return emptyList();
    }

    @Override
    @ObjectFactory
    default PackageDescriptor resolve(Package value, @TargetType Class<PackageDescriptor> descriptorType, @Context Scanner scanner) {
        JSONFileDescriptor jsonFileDescriptor = scanner.getContext()
            .peek(JSONFileDescriptor.class);
        Store store = scanner.getContext()
            .getStore();
        return store.addDescriptorType(jsonFileDescriptor, PackageDescriptor.class);
    }
}
