package org.jqassistant.plugin.npm;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;

import org.jqassistant.plugin.npm.api.model.*;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

class PackageJsonScannerPluginIT extends AbstractPluginIT {

    @Test
    void minimal() {
        File file = new File(getClassesDirectory(PackageJsonScannerPluginIT.class), "minimal/package.json");

        PackageDescriptor packageJson = getScanner().scan(file, "/minimal/package.json", DefaultScope.NONE);

        store.beginTransaction();
        assertThat(packageJson).isNotNull();
        assertThat(packageJson.getName()).isEqualTo("jqa-npm-test");
        assertThat(packageJson.getVersion()).isEqualTo("1.0.0");
        store.commitTransaction();
    }

    @Test
    void full() {
        File file = new File(getClassesDirectory(PackageJsonScannerPluginIT.class), "full/package.json");

        PackageDescriptor packageJson = getScanner().scan(file, "/full/package.json", DefaultScope.NONE);

        store.beginTransaction();
        assertThat(packageJson).isNotNull();
        assertThat(packageJson.getName()).isEqualTo("jqa-npm-test");
        assertThat(packageJson.getVersion()).isEqualTo("1.0.0");
        assertThat(packageJson.getDescription()).isEqualTo("Test Package Descriptor");
        assertThat(packageJson.getKeywords()).isEqualTo(new String[] { "jQAssistant", "Test" });
        assertThat(packageJson.getHomepage()).isEqualTo("https://jqassistant.org");
        assertThat(packageJson.getLicense()).isEqualTo("GPLv3");

        verifyPerson(packageJson.getAuthor(), "Test User 1", "test1@example.com", "https://example.com/users/test1");

        List<PersonDescriptor> contributors = packageJson.getContributors();
        assertThat(contributors).isNotEmpty();
        Map<String, PersonDescriptor> contributorsByName = contributors.stream()
            .collect(toMap(NamedDescriptor::getName, contributor -> contributor));
        verifyPerson(contributorsByName.get("Test User 2"), "Test User 2", "test2@example.com", "https://example.com/users/test2");
        verifyPerson(contributorsByName.get("Test User 3"), "Test User 3", "test3@example.com", "https://example.com/users/test3");

        assertThat(packageJson.getFiles()).isEqualTo(new String[] { "dist/" });
        assertThat(packageJson.getMain()).isEqualTo("test.js");

        Map<String, String> scriptsByName = packageJson.getScripts()
            .stream()
            .collect(toMap(NamedDescriptor::getName, ScriptDescriptor::getScript));
        assertThat(scriptsByName).containsEntry("start", "react-scripts start")
            .containsEntry("build", "react-scripts build");

        Map<String, String> dependenciesByName = packageJson.getDependencies()
            .stream()
            .collect(toMap(NamedDescriptor::getName, DependencyDescriptor::getVersionRange));
        assertThat(dependenciesByName).containsEntry("react", "^17.0.2")
            .containsEntry("react-dom", "^17.0.2");

        Map<String, String> devDependenciesByName = packageJson.getDevDependencies()
            .stream()
            .collect(toMap(NamedDescriptor::getName, DependencyDescriptor::getVersionRange));
        assertThat(devDependenciesByName).containsEntry("@lingui/cli", "^3.4.0")
            .containsEntry("@lingui/macro", "^3.4.0");

        Map<String, String> enginesByName = packageJson.getEngines()
            .stream()
            .collect(toMap(NamedDescriptor::getName, EngineDescriptor::getVersionRange));
        assertThat(enginesByName).containsEntry("node", ">=14")
            .containsEntry("npm", ">=6");

        store.commitTransaction();
    }

    @Test
    void authorAsString() {
        File file = new File(getClassesDirectory(PackageJsonScannerPluginIT.class), "person-strings/package.json");

        PackageDescriptor packageJson = getScanner().scan(file, "/person-strings/package.json", DefaultScope.NONE);

        store.beginTransaction();
        assertThat(packageJson).isNotNull();

        verifyPerson(packageJson.getAuthor(), "Test User 1", "test1@example.com", "https://example.com/users/test1");

        List<PersonDescriptor> contributors = packageJson.getContributors();
        assertThat(contributors).isNotEmpty();
        Map<String, PersonDescriptor> contributorsByName = contributors.stream()
            .collect(toMap(NamedDescriptor::getName, contributor -> contributor));
        verifyPerson(contributorsByName.get("Test User 2"), "Test User 2", "test2@example.com", "https://example.com/users/test2");
        verifyPerson(contributorsByName.get("Test User 3"), "Test User 3", null, "https://example.com/users/test3");
        verifyPerson(contributorsByName.get("Test User 4"), "Test User 4", "test4@example.com", null);
        verifyPerson(contributorsByName.get("Test User 5"), "Test User 5", null, null);

        store.commitTransaction();
    }

    @Test
    void invalidProperties() {
        File file = new File(getClassesDirectory(PackageJsonScannerPluginIT.class), "invalid-properties/package.json");

        PackageDescriptor packageJson = getScanner().scan(file, "/invalid-properties/package.json", DefaultScope.NONE);

        store.beginTransaction();

        assertThat(packageJson).isNotNull();
        assertThat(packageJson.getName()).isEqualTo("invalid-properties");
        assertThat(packageJson.getVersion()).isNull();

        store.commitTransaction();
    }

    private static void verifyPerson(PersonDescriptor personDescriptor, String expectedName, String expectedEmail, String expectedUrl) {
        assertThat(personDescriptor).isNotNull();
        assertThat(personDescriptor.getName()).isEqualTo(expectedName);
        assertThat(personDescriptor.getEmail()).isEqualTo(expectedEmail);
        assertThat(personDescriptor.getUrl()).isEqualTo(expectedUrl);
    }
}
