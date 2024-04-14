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
import com.devives.rstdoclet.rst.PackageSummaryRstGenerator;
import com.devives.sphinx.java.doc.PackagesIndexRstGenerator;
import com.devives.sphinx.rst.document.JavaDocRstElementFactoryImpl;
import com.sun.tools.javac.util.FatalError;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.internal.doclets.toolkit.DocletException;
import jdk.javadoc.internal.doclets.toolkit.Messages;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;
import jdk.javadoc.internal.doclets.toolkit.util.DocFile;
import jdk.javadoc.internal.doclets.toolkit.util.DocPath;
import jdk.javadoc.internal.doclets.toolkit.util.SimpleDocletException;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

/**
 * RstDoclet entry point. This class declares
 * the {@link #run(jdk.javadoc.doclet.DocletEnvironment)} method required
 * by the doclet API in order to be called by the
 * javadoc tool.
 *
 * @author ivvlev
 */
public final class RstDoclet extends AbstractDoclet {

    /**
     * The initiating doclet, to be specified when creating
     * the configuration.
     */
    private final Doclet initiatingDoclet;
    /**
     * The global configuration information for this run.
     * Initialized in {@link #init(Locale, Reporter)}.
     */
    private RstConfiguration configuration;

    /**
     * Object for generating messages and diagnostics.
     */
    private Messages messages;

    /**
     * Base path for resources for this doclet.
     */
    private static final DocPath DOCLET_RESOURCES = DocPath
            .create("/jdk/javadoc/internal/doclets/formats/html/resources");

    /**
     * Creates an instance of the standard doclet, used to generate HTML-formatted
     * documentation.
     */
    public RstDoclet() {
        this.initiatingDoclet = this;
        Rst.setElementFactory(new JavaDocRstElementFactoryImpl());
    }

    /**
     * Creates a doclet to generate HTML documentation,
     * specifying the "initiating doclet" to be used when
     * initializing any taglets for this doclet.
     * An initiating doclet is one that delegates to
     * this doclet.
     *
     * @param initiatingDoclet the initiating doclet
     */
    public RstDoclet(Doclet initiatingDoclet) {
        this.initiatingDoclet = initiatingDoclet;
        Rst.setElementFactory(new JavaDocRstElementFactoryImpl());
    }

    @Override
    public BaseConfiguration getConfiguration() {
        return configuration;
    }


    @Override
    protected Function<String, String> getResourceKeyMapper(DocletEnvironment docEnv) {
        SourceVersion sv = docEnv.getSourceVersion();
        Map<String, String> map = new HashMap<>();
        String[][] pairs = {
                // in standard.properties
                {"doclet.Enum_Hierarchy", "doclet.Enum_Class_Hierarchy"},
                {"doclet.Annotation_Type_Hierarchy", "doclet.Annotation_Interface_Hierarchy"},
                {"doclet.Href_Annotation_Title", "doclet.Href_Annotation_Interface_Title"},
                {"doclet.Href_Enum_Title", "doclet.Href_Enum_Class_Title"},
                {"doclet.Annotation_Types", "doclet.Annotation_Interfaces"},
                {"doclet.Annotation_Type_Members", "doclet.Annotation_Interface_Members"},
                {"doclet.annotation_types", "doclet.annotation_interfaces"},
                {"doclet.annotation_type_members", "doclet.annotation_interface_members"},
                {"doclet.help.enum.intro", "doclet.help.enum.class.intro"},
                {"doclet.help.annotation_type.intro", "doclet.help.annotation_interface.intro"},
                {"doclet.help.annotation_type.declaration", "doclet.help.annotation_interface.declaration"},
                {"doclet.help.annotation_type.description", "doclet.help.annotation_interface.description"},

                // in doclets.properties
                {"doclet.Enums", "doclet.EnumClasses"},
                {"doclet.AnnotationType", "doclet.AnnotationInterface"},
                {"doclet.AnnotationTypes", "doclet.AnnotationInterfaces"},
                {"doclet.annotationtype", "doclet.annotationinterface"},
                {"doclet.annotationtypes", "doclet.annotationinterfaces"},
                {"doclet.Enum", "doclet.EnumClass"},
                {"doclet.enum", "doclet.enumclass"},
                {"doclet.enums", "doclet.enumclasses"},
                {"doclet.Annotation_Type_Member", "doclet.Annotation_Interface_Member"},
                {"doclet.enum_values_doc.fullbody", "doclet.enum_class_values_doc.fullbody"},
                {"doclet.enum_values_doc.return", "doclet.enum_class_values_doc.return"},
                {"doclet.enum_valueof_doc.fullbody", "doclet.enum_class_valueof_doc.fullbody"},
                {"doclet.enum_valueof_doc.throws_ila", "doclet.enum_class_valueof_doc.throws_ila"},
                {"doclet.search.types", "doclet.search.classes_and_interfaces"}
        };
        for (String[] pair : pairs) {
            if (sv.compareTo(SourceVersion.RELEASE_16) >= 0) {
                map.put(pair[0], pair[1]);
            } else {
                map.put(pair[1], pair[0]);
            }
        }
        return (k) -> map.getOrDefault(k, k);
    }

    @Override
    protected void generateModuleFiles() throws DocletException {

    }

    @Override
    protected void generatePackageFiles(ClassTree classtree) throws DocletException {
        RstOptions options = configuration.getOptions();
        // if -nodeprecated option is set and the package is marked as
        // deprecated, do not generate the package-summary.html, package-frame.html
        // and package-tree.html pages for that package.
        final PackageElement[] packages = configuration.packages.stream()
                .filter(pkg -> (!(options.noDeprecated() && utils.isDeprecated(pkg)) && options.createTree()))
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
        configuration.reporter.print(Diagnostic.Kind.NOTE, "Generates packages index.");
        File file = Paths.get(configuration.getOptions().destDirName()).resolve("packages.rst").toFile();
        String[] packageNames = Arrays.stream(packages).map(p -> p.getQualifiedName().toString()).toArray(String[]::new);
        new TextFileWriter(file,
                new PackagesIndexRstGenerator(packageNames)
                        .setTitle(configuration.getOptions().docTitle())
                        .setPackageIndexFileName(configuration.getOptions().getPackageIndexFileName())
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
        return Paths.get(configuration.getOptions().destDirName())
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
            configuration.reporter.print(Diagnostic.Kind.NOTE, "Generates package documentation for " + name);
            if (!name.isEmpty()) {
                final Path directoryPath = getPackageDirectory(name);
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }
                File file = directoryPath.resolve(configuration.getOptions().getPackageIndexFileName() + ".rst").toFile();
                new TextFileWriter(file, new PackageSummaryRstGenerator(packageDoc, configuration)).write();
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
//        BuilderFactory f = configuration.getBuilderFactory();
//        for (TypeElement te : typeElems) {
//            if (utils.hasHiddenTag(te) ||
//                    !(configuration.isGeneratedDoc(te) && utils.isIncluded(te))) {
//                continue;
//            }
//            f.getClassBuilder(te, classTree).build();
//        }
////

        for (TypeElement te : typeElems) {
            if (utils.hasHiddenTag(te) ||
                    !(configuration.isGeneratedDoc(te) && utils.isIncluded(te))) {
                continue;
            }
            try {
                final PackageElement packageDoc = getPackageOfType(te);
                final String packageName = packageDoc.getQualifiedName().toString();
                final Path packageDirectory = getPackageDirectory(packageName);
                configuration.reporter.print(Diagnostic.Kind.NOTE, "Generates documentation for " + te.getQualifiedName().toString());
                String fileName = utils.getSimpleName(te).replace(".", "-");
                File file = packageDirectory.resolve(fileName + ".rst").toFile();
                new TextFileWriter(file, new ClassRstGenerator(te, configuration)).write();
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
                throw new SimpleDocletException(e.getMessage(), e);
            } catch (FatalError fe) {
                throw fe;
            } catch (Exception e) {
                e.printStackTrace();
                throw new SimpleDocletException(e.getMessage(), e);
            }
            //f.getClassBuilder(te, classTree).build();
        }

//        for (int i = 0; i < typeElems.size(); i++) {
//            if (!(configuration.isGeneratedDoc(arr[i]) && arr[i].isIncluded())) {
//                continue;
//            }
//            ClassDoc curr = arr[i];
//            try {
//                final PackageDoc packageDoc = curr.containingPackage();
//                final String packageName = packageDoc.name();
//                final Path packageDirectory = getPackageDirectory(packageName);
//                configuration.root.printNotice("Generates documentation for " + curr.name());
//                String fileName = curr.name().replace(".", "-");
//                File file = packageDirectory.resolve(fileName + ".rst").toFile();
//                new TextFileWriter(file, new ClassRstGenerator(curr, configuration)).write();
////                if (curr.isAnnotationType()) {
////                    AbstractBuilder annotationTypeBuilder =
////                            configuration.getBuilderFactory()
////                                    .getAnnotationTypeBuilder((AnnotationTypeDoc) curr,
////                                            prev, next);
////                    annotationTypeBuilder.build();
////                } else {
////                    AbstractBuilder classBuilder =
////                            configuration.getBuilderFactory()
////                                    .getClassBuilder(curr, prev, next, classtree);
////                    classBuilder.build();
////                }
//            } catch (IOException e) {
//                throw new SimpleDocletException(e.getMessage(), e);
//            } catch (FatalError fe) {
//                throw fe;
//            } catch (DocletException de) {
//                throw de;
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new SimpleDocletException(e.getMessage(), e);
//            }
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        configuration = new RstConfiguration(initiatingDoclet, locale, reporter);
        messages = configuration.getMessages();
    }

    @Override
    public String getName() {
        return "Rst";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return configuration.getOptions().getSupportedOptions();
    }
}
