package org.jqassistant.plugin.npm.concept;

import java.io.File;
import java.util.List;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.FileResolver;
import com.buschmais.jqassistant.plugin.json.api.model.JSONDescriptor;

import org.jqassistant.plugin.npm.api.model.PackageDescriptor;
import org.jqassistant.plugin.npm.api.model.WorkspaceDescriptor;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

class NpmIT extends AbstractPluginIT {

    @Test
    void resolveWorkspaces() throws RuleException {
        File file = new File(getClassesDirectory(NpmIT.class), "full/package.json");
        Scanner scanner = getScanner();

        PackageDescriptor packageJson = scanner.scan(file, "/full/package.json", DefaultScope.NONE);

        FileResolver fileResolver = getScanner().getContext()
            .peek(FileResolver.class);
        store.beginTransaction();
        for (WorkspaceDescriptor workspace : packageJson.getWorkspaces()) {
            if (!((FileDescriptor) workspace).getFileName()
                .equals(".packages/")) {
                System.out.println(((FileDescriptor) workspace).getFileName());
                FileDescriptor fileDescriptor = fileResolver.require("package.json", FileDescriptor.class, scanner.getContext());
                fileDescriptor.getParents()
                    .add((FileDescriptor) workspace);
                scanner.getContext()
                    .getStore()
                    .addDescriptorType(fileDescriptor, JSONDescriptor.class);
            }
        }
        store.commitTransaction();
        Result<Concept> result = applyConcept("jqassistant-plugin-npm:ResolveWorkspaces");
        store.beginTransaction();
        assertThat(packageJson).isNotNull();
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
        List<WorkspaceDescriptor> leftWorkspaces = query(
            "MATCH (a:Package:NPM)-[:DECLARES_WORKSPACE]->(b) WHERE b.fileName = '.apps/' OR b.fileName = '.playground/' RETURN b AS Workspaces").getColumn(
            "Workspaces");
        assertThat(leftWorkspaces.size()).isEqualTo(2);
        store.commitTransaction();
    }
}
