package org.jqassistant.plugin.npm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
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
        assertThat(packageJson.getBugTracker()).isNotNull();
        assertThat(packageJson.getBugTracker().getUrl()).isEqualTo("https://bug.tracker.example.com");
        assertThat(packageJson.getBugTracker().getEmail()).isEqualTo("bugs@example.com");
        assertThat(packageJson.getLicense()).isEqualTo("GPLv3");
        assertThat(packageJson.getPrivate()).isEqualTo("true");

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

        Map<String, ExportDescriptor> exportsByName = packageJson.getExports()
            .stream()
            .collect(toMap(NamedDescriptor::getName, Function.identity()));
        assertThat(((FileDescriptor) exportsByName.get("jqa-npm-test")).getFileName()).isEqualTo("./lib/index.js");
        assertThat(((FileDescriptor) exportsByName.get("jqa-npm-test/bib")).getFileName()).isEqualTo("./bib/index.js");
        assertThat(((FileDescriptor) exportsByName.get("jqa-npm-test/lib/*")).getFileName()).isEqualTo("./lib/*.js");
        assertThat(((FileDescriptor) exportsByName.get("jqa-npm-test/bib/*.js")).getFileName()).isEqualTo("./bib/*.js");

        assertThat(packageJson.getMain()).isEqualTo("test.js");
        assertThat(packageJson.getBrowser()).isEqualTo("test2.js");

        assertThat(packageJson.getBinaries()).hasSize(2);
        Map<String, String> binByName = packageJson.getBinaries()
            .stream()
            .collect(toMap(BinaryDescriptor::getName, BinaryDescriptor::getPath));
        assertThat(binByName)
            .containsEntry("bin1", "script1.js")
            .containsEntry("bin2", "script2.js");

        List<ManDescriptor> mans = packageJson.getMans();
        assertThat(mans.size()).isEqualTo(2);
        List<String> values = new ArrayList<>();
        mans.forEach(man -> values.add(((FileDescriptor) man).getFileName()));
        assertThat(values).contains("./man/foo.1");
        assertThat(values).contains("./man/bar.1");

        RepositoryDescriptor repo = packageJson.getRepository();
        assertThat(repo.getType()).isEqualTo("git");
        assertThat(repo.getUrl()).isEqualTo("git+https://github.com/npm/cli.git");
        assertThat(repo.getDirectory().getFileName()).isEqualTo("workspaces/libnpmpublish");

        Map<String, String> scriptsByName = packageJson.getScripts()
            .stream()
            .collect(toMap(NamedDescriptor::getName, ScriptDescriptor::getScript));
        assertThat(scriptsByName).containsEntry("start", "react-scripts start")
            .containsEntry("build", "react-scripts build");

        Map<String, String> configByName = packageJson.getConfig()
            .stream()
            .collect(toMap(NamedDescriptor::getName, ConfigDescriptor::getValue));
        assertThat(configByName).containsEntry("port", "8080");

        Map<String, DependencyDescriptor> dependenciesByName = packageJson.getDependencies()
            .stream()
            .collect(toMap(NamedDescriptor::getName, Function.identity()));
        assertThat(dependenciesByName.get("react")).isNotNull();
        assertThat(dependenciesByName.get("react").getVersionRange()).endsWith("^17.0.2");
        assertThat(dependenciesByName.get("react-dom")).isNotNull();
        assertThat(dependenciesByName.get("react-dom").getVersionRange()).endsWith("^17.0.2");
        assertThat(dependenciesByName.get("foo")).isNotNull();
        assertThat(dependenciesByName.get("foo").getVersionRange()).endsWith("3.0.0 - 2.9999.9999");
        assertThat(dependenciesByName.get("foo").getOptional()).isTrue();
        assertThat(dependenciesByName.get("colors")).isNotNull();
        assertThat(dependenciesByName.get("colors").getVersionRange()).endsWith("^1.4.0");
        assertThat(dependenciesByName.get("colors").getOptional()).isTrue();


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

        Map<String, OsDescriptor> osByName = packageJson.getOs()
            .stream()
            .collect(toMap(NamedDescriptor::getName, Function.identity()));
        assertThat(osByName.get("darwin")).isNotNull();
        assertThat(osByName.get("darwin").getType()).isEqualTo("supported");
        assertThat(osByName.get("linux")).isNotNull();
        assertThat(osByName.get("linux").getType()).isEqualTo("blocked");
        assertThat(osByName.get("win32")).isNotNull();
        assertThat(osByName.get("win32").getType()).isEqualTo("supported");

        Map<String, CpuDescriptor> cpuByName = packageJson.getCpu()
            .stream()
            .collect(toMap(NamedDescriptor::getName, Function.identity()));
        assertThat(cpuByName.get("x64")).isNotNull();
        assertThat(cpuByName.get("x64").getType()).isEqualTo("supported");
        assertThat(cpuByName.get("arm")).isNotNull();
        assertThat(cpuByName.get("arm").getType()).isEqualTo("blocked");

        Map<String, String> overridesByName = packageJson.getOverrides()
            .stream()
            .collect(toMap(NamedDescriptor::getName, OverridesDescriptor::getVersion));
        assertThat(overridesByName).hasSize(8).containsEntry("moo", "1.0.0").containsEntry("boo", "2.0.0")
            .containsEntry("boo/bar", "4.0.0").containsEntry("dar/doo", "6.0.0").containsEntry("baz/boz/biz", "7.0.0")
            .containsEntry("lar@2.0.0/loo", "8.0.0").containsEntry("soo", "3.0.0 - 2.9999.9999").containsEntry("joo", "3.0.0 - 2.9999.9999");

        Map<String, String> enginesByName = packageJson.getEngines()
            .stream()
            .collect(toMap(NamedDescriptor::getName, EngineDescriptor::getVersionRange));
        assertThat(enginesByName).containsEntry("node", ">=14")
            .containsEntry("npm", ">=6");

        Map<String, String> publishConfigByName = packageJson.getPublishConfig()
            .stream()
            .collect(toMap(NamedDescriptor::getName, PublishConfigDescriptor::getValue));
        assertThat(publishConfigByName).containsEntry("registry", "https://registry.npmjs.org/")
            .containsEntry("access", "public").containsEntry("tag", "latest");

        List<WorkspaceDescriptor> workspaces = packageJson.getWorkspaces();
        assertThat(workspaces.size()).isEqualTo(3);
        List<String> values1 = new ArrayList<>();
        workspaces.forEach(workspace  -> values1.add(((FileDescriptor) workspace ).getFileName()));
        assertThat(values1).contains(".packages/");
        assertThat(values1).contains(".apps/");
        assertThat(values1).contains(".playground/");


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

    @Test
    void exportsAsString() {
        File file = new File(getClassesDirectory(PackageJsonScannerPluginIT.class), "exports-string/package.json");

        PackageDescriptor packageJson = getScanner().scan(file, "/exports-string/package.json", DefaultScope.NONE);

        store.beginTransaction();
        assertThat(packageJson).isNotNull();

        assertThat(packageJson.getExports()).hasSize(1);
        ExportDescriptor export = packageJson.getExports().get(0);
        assertThat(export.getName()).isEqualTo("jqa-npm-test");
        assertThat(((FileDescriptor) export).getFileName()).isEqualTo("./index.js");

        store.commitTransaction();
    }

    @Test
    void repositoryAsString() {
        File file = new File(getClassesDirectory(PackageJsonScannerPluginIT.class), "repository-string/package.json");

        PackageDescriptor packageJson = getScanner().scan(file, "/repository-string/package.json", DefaultScope.NONE);

        store.beginTransaction();
        assertThat(packageJson).isNotNull();

        assertThat(packageJson.getRepository()).isNotNull();
        RepositoryDescriptor repo = packageJson.getRepository();
        assertThat(repo.getUrl()).isEqualTo("bitbucket:user/repo");

        store.commitTransaction();
    }

    @Test
    void manAsString() {
        File file = new File(getClassesDirectory(PackageJsonScannerPluginIT.class), "man-string/package.json");

        PackageDescriptor packageJson = getScanner().scan(file, "/man-string/package.json", DefaultScope.NONE);

        store.beginTransaction();
        assertThat(packageJson).isNotNull();

        assertThat(packageJson.getMans()).isNotNull();
        List<ManDescriptor> man = packageJson.getMans();
        assertThat(man.size()).isEqualTo(1);
        assertThat(((FileDescriptor) man.get(0)).getFileName()).isEqualTo("./man/doc.1");

        store.commitTransaction();
    }

    private static void verifyDevEngine (DevEngineDescriptor devEngineDescriptor, String expectedType, String expectedVersion, String expectedOnFail) {
        assertThat(devEngineDescriptor).isNotNull();
        assertThat(devEngineDescriptor.getType()).isEqualTo(expectedType);
        assertThat(devEngineDescriptor.getVersion()).isEqualTo(expectedVersion);
        assertThat(devEngineDescriptor.getOnFail()).isEqualTo(expectedOnFail);
    }

    @Test
    void devEngines() {
        File file = new File(getClassesDirectory(PackageJsonScannerPluginIT.class), "dev-engines/package.json");

        PackageDescriptor packageJson = getScanner().scan(file, "/dev-engines/package.json", DefaultScope.NONE);

        store.beginTransaction();
        assertThat(packageJson).isNotNull();

        List<DevEngineDescriptor> devEngines = packageJson.getDevEngines();
        assertThat(devEngines).isNotEmpty();

        Map<String, DevEngineDescriptor> devEnginesByName = devEngines.stream()
            .collect(toMap(NamedDescriptor::getName, devEngine -> devEngine));
        verifyDevEngine(devEnginesByName.get("x64"),  "cpu", null, "error");
        verifyDevEngine(devEnginesByName.get("linux"), "os", null, "error");
        verifyDevEngine(devEnginesByName.get("win32"), "os", null, "ignore");

        verifyDevEngine(devEnginesByName.get("glibc"), "libc", ">=2.28", "error");
        verifyDevEngine(devEnginesByName.get("musl"), "libc", null, "warn");

        verifyDevEngine(devEnginesByName.get("node"), "runtime", ">=18.0.0", "error");
        verifyDevEngine(devEnginesByName.get("npm"), "packageManager", ">=8.0.0", "warn");
        store.commitTransaction();
    }
}
