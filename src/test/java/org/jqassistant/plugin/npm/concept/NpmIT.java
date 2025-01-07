package org.jqassistant.plugin.npm.concept;

import java.io.File;
import java.util.Arrays;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.common.api.model.DirectoryDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.FileResolver;

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
        store.beginTransaction();
        System.out.println(packageJson.getWorkspaces());
        FileResolver fileResolver = getScanner().getContext()
            .peek(FileResolver.class);
        FileDescriptor fileDescriptor = fileResolver.require("package.json", FileDescriptor.class, scanner.getContext());
        System.out.println(packageJson.getWorkspaces());
        for (WorkspaceDescriptor workspace : packageJson.getWorkspaces()) {
            if (((FileDescriptor) workspace).getFileName()
                .equals(".apps/")) {
                fileDescriptor.getParents()
                    .add((FileDescriptor) workspace);
            }
        }

        Result<Concept> result = applyConcept("jqassistant-plugin-npm:ResolveWorkspaces");

        assertThat(packageJson).isNotNull();
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
        assertThat(result.getRows()
            .size()).isEqualTo(1);
        Row row = result.getRows().get(0);
        Object noWorkspaceDir = row.getColumns().get("NoWorkspaceDirectories").getValue();
        assertThat(noWorkspaceDir).isNotInstanceOf(WorkspaceDescriptor.class);
        assertThat(((DirectoryDescriptor) noWorkspaceDir).getFileName().equals(".packages/")).isTrue();

        store.commitTransaction();
    }
}
