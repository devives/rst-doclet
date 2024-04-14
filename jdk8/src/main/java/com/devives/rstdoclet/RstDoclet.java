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

import com.devives.rst.Rst;
import com.devives.rst.util.TextFileWriter;
import com.devives.rstdoclet.rst.ClassRstGenerator;
import com.devives.rstdoclet.rst.JavaDocRstElementFactoryImpl;
import com.devives.rstdoclet.rst.PackageSummaryRstGenerator;
import com.devives.sphinx.java.doc.PackagesIndexRstGenerator;
import com.sun.javadoc.*;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * RstDoclet entry point. This class declares
 * the {@link #start(RootDoc)} method required
 * by the doclet API in order to be called by the
 * javadoc tool.
 *
 * @author ivvlev
 */
public final class RstDoclet extends AbstractDoclet {
    // An instance will be created by validOptions, and used by start.
    private static RstDoclet docletToStart = null;

    public RstDoclet() {
        configuration = new ConfigurationImpl();
        Rst.setElementFactory(new JavaDocRstElementFactoryImpl());
    }

    /**
     * The global configuration information for this run.
     */
    public final ConfigurationImpl configuration;

    /**
     * The "start" method as required by Javadoc.
     *
     * @param root the root of the documentation tree.
     * @return true if the doclet ran without encountering any errors.
     * @see com.sun.javadoc.RootDoc
     */
    public static boolean start(RootDoc root) {
        // In typical use, options will have been set up by calling validOptions,
        // which will create an RstDoclet for use here.
        RstDoclet doclet;
        if (docletToStart != null) {
            doclet = docletToStart;
            docletToStart = null;
        } else {
            doclet = new RstDoclet();
        }
        return doclet.start(doclet, root);
    }

    /**
     * Create the configuration instance.
     * Override this method to use a different
     * configuration.
     */
    public Configuration configuration() {
        return configuration;
    }

    /**
     * {@inheritDoc}
     */
    protected void generateClassFiles(ClassDoc[] arr, ClassTree classTree) {
        Arrays.sort(arr);
        for (int i = 0; i < arr.length; i++) {
            if (!(configuration.isGeneratedDoc(arr[i]) && arr[i].isIncluded())) {
                continue;
            }
            ClassDoc curr = arr[i];
            try {
                final PackageDoc packageDoc = curr.containingPackage();
                final String packageName = packageDoc.name();
                final Path packageDirectory = getPackageDirectory(packageName);
                configuration.root.printNotice("Generates documentation for " + curr.name());
                String fileName = curr.name().replace(".", "-");
                File file = packageDirectory.resolve(fileName + ".rst").toFile();
                new TextFileWriter(file, new ClassRstGenerator(curr, configuration)).write();
            } catch (FatalError fe) {
                throw fe;
            } catch (DocletAbortException de) {
                throw de;
            } catch (IOException e) {
                throw new DocletAbortException(e);
            } catch (Exception e) {
                e.printStackTrace();
                throw new DocletAbortException(e);
            }
        }
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
        return Paths.get(configuration.destDirName)
                .resolve(directory)
                .toAbsolutePath();
    }

    /**
     * Generates package index for the given packages.
     *
     * @param packages Packages to generate index for.
     * @throws IOException If any error occurs while creating file or directories.
     */
    private void generatePackagesIndex(final PackageDoc[] packages) throws IOException {
        configuration.root.printNotice("Generates packages index.");
        File file = Paths.get(configuration.destDirName).resolve("packages.rst").toFile();
        String[] packageNames = Arrays.stream(packages).map(Doc::name).toArray(String[]::new);
        new TextFileWriter(file,
                new PackagesIndexRstGenerator(packageNames)
                        .setTitle(configuration.doctitle)
                        .setPackageIndexFileName(configuration.packageIndexFileName)
        ).write();
    }

    /**
     * Generates package documentation for the given
     * ``packageDoc``.
     *
     * @param packageDoc Package to generate documentation for.
     * @throws IOException If any error occurs while creating file or directories.
     */
    private Path generatePackage(final PackageDoc packageDoc) throws IOException {
        final String name = packageDoc.name();
        configuration.root.printNotice("Generates package documentation for " + name);
        if (!name.isEmpty()) {
            final Path directoryPath = getPackageDirectory(name);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            File file = directoryPath.resolve(configuration.packageIndexFileName + ".rst").toFile();
            new TextFileWriter(file, new PackageSummaryRstGenerator(packageDoc, configuration)).write();
            return directoryPath;
        }
        return Paths.get(".");
    }


    /**
     * {@inheritDoc}
     */
    protected void generatePackageFiles(ClassTree classTree) throws Exception {
        // if -nodeprecated option is set and the package is marked as
        // deprecated, do not generate the package-summary.html, package-frame.html
        // and package-tree.html pages for that package.
        final PackageDoc[] packages = Arrays.stream(configuration.packages)
                .filter(pkg -> !(configuration.nodeprecated && Util.isDeprecated(pkg)))
                .toArray(PackageDoc[]::new);
        generatePackagesIndex(packages);
        for (PackageDoc pkg : packages) {
            generatePackage(pkg);
        }
    }

    public static final ConfigurationImpl sharedInstanceForOptions =
            new ConfigurationImpl();

    /**
     * Check for doclet added options here.
     *
     * @return number of arguments to option. Zero return means
     * option not known.  Negative value means error occurred.
     */
    public static int optionLength(String option) {
        // Construct temporary configuration for check
        return sharedInstanceForOptions.optionLength(option);
    }

    /**
     * Check that options have the correct arguments here.
     * <p>
     * This method is not required and will default gracefully
     * (to true) if absent.
     * <p>
     * Printing option related error messages (using the provided
     * DocErrorReporter) is the responsibility of this method.
     *
     * @return true if the options are valid.
     */
    public static boolean validOptions(String[][] options,
                                       DocErrorReporter reporter) {
        docletToStart = new RstDoclet();
        return docletToStart.configuration.validOptions(options, reporter);
    }

}
