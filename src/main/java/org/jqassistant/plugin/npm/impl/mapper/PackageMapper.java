package org.jqassistant.plugin.npm.impl.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.mapper.DescriptorMapper;
import com.buschmais.jqassistant.plugin.common.api.model.DirectoryDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.FileResolver;
import com.buschmais.jqassistant.plugin.json.api.model.JSONFileDescriptor;

import org.jqassistant.plugin.npm.api.model.*;
import org.jqassistant.plugin.npm.impl.model.Package;
import org.mapstruct.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(uses = {PersonMapper.class, BugTrackerMapper.class, FundingMapper.class, BinaryMapper.class, RepositoryMapper.class, DevEngineMapper.class},
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PackageMapper extends DescriptorMapper<Package, PackageDescriptor> {

    PackageMapper INSTANCE = getMapper(PackageMapper.class);

    @Override
    @Mapping(source = "bugs", target = "bugTracker")
    @Mapping(source = "bin", target = "binaries")
    @Mapping(source = "man", target = "mans", qualifiedByName = "manMapping")
    @Mapping(source = "repository", target = "repository")
    @Mapping(source = "scripts", target = "scripts", qualifiedByName = "scriptsMapping")
    @Mapping(source = "config", target = "config", qualifiedByName = "configMapping")
    @Mapping(source = "dependencies", target = "dependencies", qualifiedByName = "dependencyMapping")
    @Mapping(source = "devDependencies", target = "devDependencies", qualifiedByName = "dependencyMapping")
    @Mapping(source = "peerDependencies", target = "peerDependencies", qualifiedByName = "dependencyMapping")
    @Mapping(source = "overrides", target = "overrides", qualifiedByName = "overridesMapping")
    @Mapping(source = "os", target = "os", qualifiedByName = "osMapping")
    @Mapping(source = "cpu", target = "cpu", qualifiedByName = "cpuMapping")
    @Mapping(source = "exports", target = "exports", qualifiedByName = "exportMapping")
    @Mapping(target = "bundledDependencies", ignore = true)
    @Mapping(source = "engines", target = "engines", qualifiedByName = "engineMapping")
    @Mapping(source = "devEngines", target = "devEngines")
    @Mapping(source = "privat", target = "private")
    @Mapping(source = "publishConfig", target = "publishConfig", qualifiedByName = "publishConfigMapping")
    @Mapping(source = "workspaces", target = "workspaces", qualifiedByName = "workspacesMapping")
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
        if (source.getBundleDependencies() != null) {
            if (Boolean.TRUE.equals(source.getBundleDependencies().getAllBundled())) {
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
                .filter(descriptor -> descriptor.getVersion().startsWith("$")).forEach(refOverride -> target.getDependencies().stream()
                    .filter(dep -> dep.getName().equals(refOverride.getVersion().substring(1)))
                    .findFirst()
                    .ifPresent(dep -> refOverride.setVersion(dep.getVersionRange())));
        }

        if (source.getExports() != null && target.getName() != null) {
            target.getExports().stream()
                .filter(descriptor -> descriptor.getName().startsWith(".")).forEach(descriptor1 -> {
                    String newName = target.getName() + descriptor1.getName().substring(1);
                    descriptor1.setName(newName);
                    }
                );
        }

        if (source.getRepository() != null) {
            if (source.getRepository()
                .getDirectory() != null) {
                String directoryName = source.getRepository()
                    .getDirectory();
                FileResolver fileResolver = scanner.getContext()
                    .peek(FileResolver.class);
                DirectoryDescriptor directoryDescriptor = fileResolver.require(directoryName, DirectoryDescriptor.class, scanner.getContext());
                target.getRepository()
                    .setDirectory(directoryDescriptor);
            }
        }
    }

    @Named("scriptsMapping")
    default List<ScriptDescriptor> scriptsMapping(Map<String, String> sourceField, @Context Scanner scanner) {
        return mapMapProperty(sourceField, ScriptDescriptor.class, ScriptDescriptor::setScript, scanner);
    }

    @Named("configMapping")
    default List<ConfigDescriptor> configMapping(Map<String, String> sourceField, @Context Scanner scanner) {
        return mapMapProperty(sourceField, ConfigDescriptor.class, ConfigDescriptor::setValue, scanner);
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

    @Named("publishConfigMapping")
    default List<PublishConfigDescriptor> publishConfigMapping(Map<String, String> sourceField, @Context Scanner scanner) {
        return mapMapProperty(sourceField, PublishConfigDescriptor.class, PublishConfigDescriptor::setValue, scanner);
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

    @Named("exportMapping")
    default List<ExportDescriptor> exportMapping(Map<String, String> map, @Context Scanner scanner) {
        FileResolver fileResolver = scanner.getContext()
            .peek(FileResolver.class);
        if (map != null) {
            return map.entrySet()
                .stream()
                .map(entry -> {
                    FileDescriptor fileDescriptor = fileResolver.require(entry.getValue(), FileDescriptor.class, scanner.getContext());
                    ExportDescriptor exportDescriptor = scanner.getContext()
                        .getStore()
                        .addDescriptorType(fileDescriptor, ExportDescriptor.class);
                    exportDescriptor.setName(entry.getKey());
                    return exportDescriptor;
                })
                .collect(toList());
        }
        return emptyList();
    }

    @Named("manMapping")
    default List<ManDescriptor> manMapping(String[] sourceField, @Context Scanner scanner) {
        if (sourceField != null) {
            FileResolver fileResolver = scanner.getContext()
                .peek(FileResolver.class);
            return Arrays.stream(sourceField)
                .map(man -> {
                    FileDescriptor fileDescriptor = fileResolver.require(man, FileDescriptor.class, scanner.getContext());
                    fileDescriptor.setFileName(man);
                    return scanner.getContext()
                        .getStore()
                        .addDescriptorType(fileDescriptor, ManDescriptor.class);
                })
                .collect(toList());
        }
        return emptyList();
    }

    @Named("cpuMapping")
    default List<CpuDescriptor> cpuMapping(String[] sourceField, @Context Scanner scanner) {
        if (sourceField != null) {
            return Arrays.stream(sourceField)
                .map(cpu -> {
                    CpuDescriptor descriptor = scanner.getContext().getStore().create(CpuDescriptor.class);
                    if (cpu.startsWith("!")) {
                        descriptor.setType("blocked");
                        descriptor.setName(cpu.substring(1));
                    } else {
                        descriptor.setType("supported");
                        descriptor.setName(cpu);
                    }
                    return descriptor;
                })
                .collect(toList());
        }
        return emptyList();
    }

    @Named("workspacesMapping")
    default List<WorkspaceDescriptor> workspacesMapping(String[] sourceField, @Context Scanner scanner) {
        if (sourceField != null) {
            FileResolver fileResolver = scanner.getContext()
                .peek(FileResolver.class);
            return Arrays.stream(sourceField)
                .map(workspace -> {
                    DirectoryDescriptor directoryDescriptor = fileResolver.require(workspace, DirectoryDescriptor.class, scanner.getContext());
                        return scanner.getContext()
                                .getStore()
                                .addDescriptorType(directoryDescriptor, WorkspaceDescriptor.class);
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
