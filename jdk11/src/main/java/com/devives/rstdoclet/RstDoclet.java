/**
 * RstDoclet for JavaDoc Tool, generating reStructuredText for Sphinx.
 * Copyright (C) 2023-2024 Vladimir Ivanov <ivvlev@devives.com>.
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation..
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.devives.rstdoclet;


import com.devives.rst.util.TextFileWriter;
import com.devives.rstdoclet.rst.ClassRstGenerator;
import com.devives.rstdoclet.rst.PackageSummaryRstGenerator;
import com.devives.sphinx.java.doc.PackagesIndexRstGenerator;
import com.sun.tools.javac.util.FatalError;
import jdk.javadoc.internal.doclets.toolkit.DocletException;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;
import jdk.javadoc.internal.doclets.toolkit.util.DocFile;
import jdk.javadoc.internal.doclets.toolkit.util.SimpleDocletException;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.SortedSet;


public class RstDoclet extends AbstractRstDoclet {

    /**
     * {@inheritDoc}
     */
    @Override // defined by AbstractDoclet
    protected void generatePackageFiles(ClassTree classtree) throws DocletException {
        // if -nodeprecated option is set and the package is marked as
        // deprecated, do not generate the package-summary.html, package-frame.html
        // and package-tree.html pages for that package.
        final PackageElement[] packages = configuration.packages.stream()
                .filter(pkg -> (!(configuration.nodeprecated && utils.isDeprecated(pkg)) && configuration.createtree))
                .toArray(PackageElement[]::new);
        try {
            generatePackagesIndex(packages);
            for (PackageElement pkg : packages) {
                generatePackage(pkg);
            }
        } catch (IOException e) {
            throw new SimpleDocletException(e.getMessage(), e);
        }
    }

    /**
     * Generates package index for the given packages.
     *
     * @param packages Packages to generate index for.
     * @throws IOException If any error occurs while creating file or directories.
     */
    private void generatePackagesIndex(final PackageElement[] packages) throws IOException {
        File file = Paths.get(configuration.destDirName).resolve("packages.rst").toFile();
        String[] packageNames = Arrays.stream(packages).map(p -> p.getQualifiedName().toString()).toArray(String[]::new);
        new TextFileWriter(file,
                new PackagesIndexRstGenerator(packageNames)
                        .setTitle(configuration.doctitle)
                        .setPackageIndexFileName(rstConfiguration.getPackageIndexFileName())
        ).write();
    }

    /**
     * Builds and retrieves the path for the
     * directory associated to the package
     * with the given <tt>name</tt>.
     *
     * @param packageName Name of the package to get directory for.
     * @return Built path.
     */
    private Path getPackageDirectory(final String packageName) {
        final String directory = packageName.replace('.', '/');
        DocFile.createFileForDirectory(configuration, directory);
        return Paths.get(configuration.destDirName)
                .resolve(directory)
                .toAbsolutePath();
    }

    /**
     * Generates package documentation for the given
     * ``packageDoc``.
     *
     * @param packageDoc Package to generate documentation for.
     * @throws IOException If any error occurs while creating file or directories.
     */
    private Path generatePackage(final PackageElement packageDoc) {
        try {
            final String name = packageDoc.getQualifiedName().toString();
            if (!name.isEmpty()) {
                final Path directoryPath = getPackageDirectory(name);
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }
                File file = directoryPath.resolve(rstConfiguration.getPackageIndexFileName() + ".rst").toFile();
                new TextFileWriter(file, new PackageSummaryRstGenerator(packageDoc, rstConfiguration)).write();
                return directoryPath;
            }
            return Paths.get(".");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PackageElement getPackageOfType(TypeElement typeElement) {
        Element parentElement = typeElement.getEnclosingElement();
        while (parentElement != null && !(parentElement instanceof PackageElement)) {
            parentElement = parentElement.getEnclosingElement();
        }
        return (PackageElement) parentElement;
    }

    @Override
    protected void generateClassFiles(SortedSet<TypeElement> typeElems, ClassTree classTree) throws DocletException {
        for (TypeElement typeElement : typeElems) {
            if (utils.hasHiddenTag(typeElement) ||
                    !(configuration.isGeneratedDoc(typeElement) && utils.isIncluded(typeElement))) {
                continue;
            }
            try {
                final PackageElement packageDoc = getPackageOfType(typeElement);
                final String packageName = packageDoc.getQualifiedName().toString();
                final Path packageDirectory = getPackageDirectory(packageName);
                String fileName = utils.getSimpleName(typeElement).replace(".", "-");
                File file = packageDirectory.resolve(fileName + ".rst").toFile();
                new TextFileWriter(file, new ClassRstGenerator(rstConfiguration, typeElement, classTree)).write();
            } catch (IOException e) {
                throw new SimpleDocletException(e.getMessage(), e);
            } catch (FatalError fe) {
                throw fe;
            } catch (Exception e) {
                e.printStackTrace();
                throw new SimpleDocletException(e.getMessage(), e);
            }
        }
    }
}
