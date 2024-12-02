package org.jqassistant.plugin.npm.impl.mapper;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONFileDescriptor;
import org.jqassistant.plugin.npm.api.model.*;
import org.jqassistant.plugin.npm.impl.model.Package;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(uses = {PersonMapper.class, BugTrackerMapper.class, FundingMapper.class, BinaryMapper.class, OsMapper.class},
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PackageMapper extends DescriptorMapper<Package, PackageDescriptor> {

    PackageMapper INSTANCE = getMapper(PackageMapper.class);

    @Override
    @Mapping(source = "bugs", target = "bugTracker")
    @Mapping(source = "bin", target = "binaries")
    @Mapping(source = "scripts", target = "scripts", qualifiedByName = "scriptsMapping")
    @Mapping(source = "dependencies", target = "dependencies", qualifiedByName = "dependencyMapping")
    @Mapping(source = "devDependencies", target = "devDependencies", qualifiedByName = "dependencyMapping")
    @Mapping(source = "peerDependencies", target = "peerDependencies", qualifiedByName = "dependencyMapping")
    @Mapping(source = "overrides", target = "overrides", qualifiedByName = "overridesMapping")
    @Mapping(source = "os", target = "os", qualifiedByName = "osMapping")
    @Mapping(target = "bundledDependencies", ignore = true)
    @Mapping(source = "engines", target = "engines", qualifiedByName = "engineMapping")
    PackageDescriptor toDescriptor(Package value, @Context Scanner scanner);

    @AfterMapping
    default void after(Package source, @MappingTarget PackageDescriptor target, @Context Scanner scanner) {
        // resolve string binary name to package name
        if (target.getName() != null) {
            target.getBinaries().stream().filter(b -> b.getName() == null).forEach(b -> b.setName(target.getName()));
        }

        // resolve peerDependenciesMeta
        if (source.getPeerDependenciesMeta() != null) {
            source.getPeerDependenciesMeta()
                .forEach((depName, optional) -> target.getPeerDependencies()
                    .stream()
                    .filter(dep -> dep.getName().equals(depName))
                    .forEach(dep -> dep.setOptional(optional))
                );
        }

        // resolve bundledDependencies
        if(source.getBundleDependencies() != null) {
            if(Boolean.TRUE.equals(source.getBundleDependencies().getAllBundled())) {
                target.getBundledDependencies().addAll(target.getDependencies());
            } else {
                source.getBundleDependencies().getDependencies().forEach(depName ->
                    target.getDependencies()
                    .stream()
                    .filter(dep -> dep.getName().equals(depName))
                    .findFirst()
                    .ifPresent(target.getBundledDependencies()::add)
                );
            }
        }
        // resolve optionalDependencies - already defined dependencies that are also defined in optionalDependencies will be overridden
        if (source.getOptionalDependencies() != null) {
            source.getOptionalDependencies().forEach((key, value) ->
                target.getDependencies().stream()
                .filter(dep -> dep.getName().equals(key))
                .findFirst()
                .ifPresentOrElse(depToUpdate -> {
                    // mark existing dependency as optional and override version range
                    depToUpdate.setOptional(true);
                    depToUpdate.setVersionRange(value);
                }, () -> {
                    // add new optional dependency
                    Store store = scanner.getContext().getStore();
                    DependencyDescriptor descriptor = store.create(DependencyDescriptor.class);
                    descriptor.setName(key);
                    descriptor.setVersionRange(value);
                    descriptor.setOptional(true);
                    target.getDependencies().add(descriptor);
                })
            );
        }
        if (source.getOverrides() != null) {
            target.getOverrides().stream()
                .filter(descriptor -> descriptor.getVersion().startsWith("$"))
                .findFirst()
                .ifPresent(refOverride -> {
                    target.getDependencies().stream()
                        .filter(dep -> dep.getName().equals(refOverride.getVersion().substring(1)))
                        .findFirst()
                        .ifPresent(dep -> {
                            refOverride.setVersion(dep.getVersionRange());
                        });
                });
        }
    }

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

    @Named("overridesMapping")
    default List<OverridesDescriptor> overridesMapping(Map<String, String> sourceField, @Context Scanner scanner) {
        return mapMapProperty(sourceField, OverridesDescriptor.class, OverridesDescriptor::setVersion, scanner);
    }

    @Named("osMapping")
    default List<OsDescriptor> osMapping(String[] sourceField, @Context Scanner scanner) {
        if (sourceField != null) {
            return Arrays.stream(sourceField)
                .map(os -> {
                    OsDescriptor descriptor = scanner.getContext().getStore().create(OsDescriptor.class);
                    if (os.startsWith("!")) {
                        descriptor.setType("blocked");
                        descriptor.setName(os.substring(1));
                    } else {
                        descriptor.setType("supported");
                        descriptor.setName(os);
                    }
                    return descriptor;
                })
                .collect(toList());
        }
        return emptyList();
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
