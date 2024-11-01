package org.jqassistant.plugin.npm.impl;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin.Requires;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import com.buschmais.jqassistant.plugin.json.api.model.JSONFileDescriptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jqassistant.plugin.npm.api.model.PackageDescriptor;
import org.jqassistant.plugin.npm.impl.mapper.PackageMapper;
import org.jqassistant.plugin.npm.impl.model.Package;

import java.io.IOException;

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
        Package value = OBJECT_MAPPER.readValue(fileResource.createStream(), Package.class);
        return PackageMapper.INSTANCE.toDescriptor(value, scanner);
    }
}
