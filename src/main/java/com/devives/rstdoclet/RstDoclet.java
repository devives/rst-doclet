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
 * Marklet entry point. This class declares
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
     * Start the generation of files. Call generate methods in the individual
     * writers, which will in turn genrate the documentation files. Call the
     * TreeWriter generation first to ensure the Class Hierarchy is built
     * first and then can be used in the later generation.
     * <p>
     * For new format.
     *
     * @see com.sun.javadoc.RootDoc
     */
    protected void generateOtherFiles(RootDoc root, ClassTree classtree)
            throws Exception {
//        super.generateOtherFiles(root, classtree);
//        if (configuration.linksource) {
//            SourceToHTMLConverter.convertRoot(configuration,
//                    root, DocPaths.SOURCE_OUTPUT);
//        }
//
//        if (configuration.topFile.isEmpty()) {
//            configuration.standardmessage.
//                    error("doclet.No_Non_Deprecated_Classes_To_Document");
//            return;
//        }
//        boolean nodeprecated = configuration.nodeprecated;
//        performCopy(configuration.helpfile);
//        performCopy(configuration.stylesheetfile);
//        // do early to reduce memory footprint
//        if (configuration.classuse) {
//            ClassUseWriter.generate(configuration, classtree);
//        }
//        IndexBuilder indexbuilder = new IndexBuilder(configuration, nodeprecated);
//
//        if (configuration.createtree) {
//            TreeWriter.generate(configuration, classtree);
//        }
//        if (configuration.createindex) {
//            if (configuration.splitindex) {
//                SplitIndexWriter.generate(configuration, indexbuilder);
//            } else {
//                SingleIndexWriter.generate(configuration, indexbuilder);
//            }
//        }
//
//        if (!(configuration.nodeprecatedlist || nodeprecated)) {
//            DeprecatedListWriter.generate(configuration);
//        }
//
//        AllClassesFrameWriter.generate(configuration,
//                new IndexBuilder(configuration, nodeprecated, true));
//
//        FrameOutputWriter.generate(configuration);
//
//        if (configuration.createoverview) {
//            PackageIndexWriter.generate(configuration);
//        }
//        if (configuration.helpfile.length() == 0 &&
//                !configuration.nohelp) {
//            HelpWriter.generate(configuration);
//        }
//        // If a stylesheet file is not specified, copy the default stylesheet
//        // and replace newline with platform-specific newline.
//        DocFile f;
//        if (configuration.stylesheetfile.length() == 0) {
//            f = DocFile.createFileForOutput(configuration, DocPaths.STYLESHEET);
//            f.copyResource(DocPaths.RESOURCES.resolve(DocPaths.STYLESHEET), false, true);
//        }
//        f = DocFile.createFileForOutput(configuration, DocPaths.JAVASCRIPT);
//        f.copyResource(DocPaths.RESOURCES.resolve(DocPaths.JAVASCRIPT), true, true);
    }

    /**
     * {@inheritDoc}
     */
    protected void generateClassFiles(ClassDoc[] arr, ClassTree classtree) {
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
//                if (curr.isAnnotationType()) {
//                    AbstractBuilder annotationTypeBuilder =
//                            configuration.getBuilderFactory()
//                                    .getAnnotationTypeBuilder((AnnotationTypeDoc) curr,
//                                            prev, next);
//                    annotationTypeBuilder.build();
//                } else {
//                    AbstractBuilder classBuilder =
//                            configuration.getBuilderFactory()
//                                    .getClassBuilder(curr, prev, next, classtree);
//                    classBuilder.build();
//                }
            } catch (IOException e) {
                throw new DocletAbortException(e);
            } catch (FatalError fe) {
                throw fe;
            } catch (DocletAbortException de) {
                throw de;
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
    protected void generatePackageFiles(ClassTree classtree) throws Exception {
        PackageDoc[] packages = configuration.packages;
        for (int i = 0; i < packages.length; i++) {
            // if -nodeprecated option is set and the package is marked as
            // deprecated, do not generate the package-summary.html, package-frame.html
            // and package-tree.html pages for that package.
            if (!(configuration.nodeprecated && Util.isDeprecated(packages[i]))) {
                generatePackage(packages[i]);
            }
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

    private void performCopy(String filename) {
        if (filename.isEmpty())
            return;

        try {
            DocFile fromfile = DocFile.createFileForInput(configuration, filename);
            DocPath path = DocPath.create(fromfile.getName());
            DocFile toFile = DocFile.createFileForOutput(configuration, path);
            if (toFile.isSameFile(fromfile))
                return;

            configuration.message.notice((SourcePosition) null,
                    "doclet.Copying_File_0_To_File_1",
                    fromfile.toString(), path.getPath());
            toFile.copyFile(fromfile);
        } catch (IOException exc) {
            configuration.message.error((SourcePosition) null,
                    "doclet.perform_copy_exception_encountered",
                    exc.toString());
            throw new DocletAbortException(exc);
        }
    }
}
