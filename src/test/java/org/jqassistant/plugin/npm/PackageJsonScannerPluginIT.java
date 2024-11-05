package org.jqassistant.plugin.npm;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import org.jqassistant.plugin.npm.api.model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
        assertThat(packageJson.getBugTracker()).isNotNull();
        assertThat(packageJson.getBugTracker().getUrl()).isEqualTo("https://bug.tracker.example.com");
        assertThat(packageJson.getBugTracker().getEmail()).isEqualTo("bugs@example.com");
        assertThat(packageJson.getLicense()).isEqualTo("GPLv3");

        verifyPerson(packageJson.getAuthor(), "Test User 1", "test1@example.com", "https://example.com/users/test1");

        List<PersonDescriptor> contributors = packageJson.getContributors();
        assertThat(contributors).isNotEmpty();
        Map<String, PersonDescriptor> contributorsByName = contributors.stream()
            .collect(toMap(NamedDescriptor::getName, contributor -> contributor));
        verifyPerson(contributorsByName.get("Test User 2"), "Test User 2", "test2@example.com", "https://example.com/users/test2");
        verifyPerson(contributorsByName.get("Test User 3"), "Test User 3", "test3@example.com", "https://example.com/users/test3");

        List<FundingDescriptor> funding = packageJson.getFunding();
        assertThat(funding).hasSize(2);
        Map<String, FundingDescriptor> fundingByType = funding.stream()
            .collect(toMap(FundingDescriptor::getType, fundingDescriptor -> fundingDescriptor));
        assertThat(fundingByType.get("fundingX")).isNotNull();
        assertThat(fundingByType.get("fundingX").getUrl()).isEqualTo("funding-x.com");
        assertThat(fundingByType.get("url")).isNotNull();
        assertThat(fundingByType.get("url").getUrl()).isEqualTo("funding-y.com");

        assertThat(packageJson.getFiles()).isEqualTo(new String[] { "dist/" });
        assertThat(packageJson.getMain()).isEqualTo("test.js");
        assertThat(packageJson.getBrowser()).isEqualTo("test2.js");

        assertThat(packageJson.getBinaries()).hasSize(2);
        Map<String, String> binByName = packageJson.getBinaries()
            .stream()
            .collect(toMap(BinaryDescriptor::getName, BinaryDescriptor::getPath));
        assertThat(binByName)
            .containsEntry("bin1", "script1.js")
            .containsEntry("bin2", "script2.js");


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

        Map<String, DependencyDescriptor> peerDependenciesByName = packageJson.getPeerDependencies()
            .stream()
            .collect(toMap(NamedDescriptor::getName, Function.identity()));
        assertThat(peerDependenciesByName.get("tea")).isNotNull();
        assertThat(peerDependenciesByName.get("tea").getVersionRange()).isEqualTo("2.x");
        assertThat(peerDependenciesByName.get("soy-milk")).isNotNull();
        assertThat(peerDependenciesByName.get("soy-milk").getVersionRange()).isEqualTo("1.2");
        assertThat(peerDependenciesByName.get("soy-milk").getOptional()).isTrue();

        assertThat(packageJson.getBundledDependencies()).hasSize(1);
        assertThat(packageJson.getBundledDependencies().get(0).getName()).isEqualTo("react-dom");
        assertThat(packageJson.getBundledDependencies().get(0).getVersionRange()).isEqualTo("^17.0.2");

        Map<String, String> enginesByName = packageJson.getEngines()
            .stream()
            .collect(toMap(NamedDescriptor::getName, EngineDescriptor::getVersionRange));
        assertThat(enginesByName).containsEntry("node", ">=14")
            .containsEntry("npm", ">=6");

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

    private static void verifyPerson(PersonDescriptor personDescriptor, String expectedName, String expectedEmail, String expectedUrl) {
        assertThat(personDescriptor).isNotNull();
        assertThat(personDescriptor.getName()).isEqualTo(expectedName);
        assertThat(personDescriptor.getEmail()).isEqualTo(expectedEmail);
        assertThat(personDescriptor.getUrl()).isEqualTo(expectedUrl);
    }

    @Test
    void binAsString() {
        File file = new File(getClassesDirectory(PackageJsonScannerPluginIT.class), "bin-string/package.json");

        PackageDescriptor packageJson = getScanner().scan(file, "/bin-string/package.json", DefaultScope.NONE);

        store.beginTransaction();
        assertThat(packageJson).isNotNull();

        assertThat(packageJson.getBinaries()).hasSize(1);
        BinaryDescriptor bin = packageJson.getBinaries().get(0);
        assertThat(bin.getName()).isEqualTo("jqa-npm-test");
        assertThat(bin.getPath()).isEqualTo("bin/script.js");

        store.commitTransaction();
    }
}
