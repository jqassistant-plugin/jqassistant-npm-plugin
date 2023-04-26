package org.jqassistant.plugin.npm.impl.scanner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin.Requires;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import com.buschmais.jqassistant.plugin.json.api.model.JSONFileDescriptor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jqassistant.plugin.npm.api.model.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Scanner plugin for package.json files.
 */
@Requires(JSONFileDescriptor.class)
public class PackageJsonScannerPlugin extends AbstractScannerPlugin<FileResource, PackageDescriptor> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Override
    public boolean accepts(FileResource fileResource, String path, Scope scope) {
        return path.endsWith("/package.json");
    }

    @Override
    public PackageDescriptor scan(FileResource fileResource, String path, Scope scope, Scanner scanner) throws IOException {
        JSONFileDescriptor jsonFileDescriptor = scanner.getContext()
            .peek(JSONFileDescriptor.class);
        Store store = scanner.getContext()
            .getStore();
        PackageDescriptor packageDescriptor = store.addDescriptorType(jsonFileDescriptor, PackageDescriptor.class);
        Package value = OBJECT_MAPPER.readValue(fileResource.createStream(), Package.class);
        packageDescriptor.setName(value.getName());
        packageDescriptor.setVersion(value.getVersion());
        packageDescriptor.setDescription(value.getDescription());
        packageDescriptor.setKeywords(value.getKeywords());
        packageDescriptor.setHomepage(value.getHomepage());
        packageDescriptor.setLicense(value.getLicense());
        Package.Person author = value.getAuthor();
        if (author != null) {
            packageDescriptor.setAuthor(getPersonDescriptor(author, store));
        }
        List<Package.Person> contributors = value.getContributors();
        if (contributors != null) {
            for (Package.Person contributor : contributors) {
                packageDescriptor.getContributors()
                    .add(getPersonDescriptor(contributor, store));
            }
        }
        packageDescriptor.setFiles(value.getFiles());
        packageDescriptor.setMain(value.getMain());
        packageDescriptor.getScripts()
            .addAll(map(value.getScripts(), ScriptDescriptor.class, ScriptDescriptor::setScript, store));
        packageDescriptor.getDependencies()
            .addAll(map(value.getDependencies(), DependencyDescriptor.class, DependencyDescriptor::setDependency, store));
        packageDescriptor.getDevDependencies()
            .addAll(map(value.getDevDependencies(), DependencyDescriptor.class, DependencyDescriptor::setDependency, store));
        packageDescriptor.getEngines()
            .addAll(map(value.getEngines(), EngineDescriptor.class, EngineDescriptor::setEngine, store));
        return packageDescriptor;
    }

    private PersonDescriptor getPersonDescriptor(Package.Person author, Store store) {
        PersonDescriptor authorDescriptor = store.create(PersonDescriptor.class);
        authorDescriptor.setName(author.getName());
        authorDescriptor.setEmail(author.getEmail());
        authorDescriptor.setUrl(author.getUrl());
        return authorDescriptor;
    }

    private <T extends NamedDescriptor> List<T> map(Map<String, String> map, Class<T> descriptorType, BiConsumer<T, String> valueConsumer, Store store) {
        if (map != null) {
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
}
