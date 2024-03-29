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

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.builders.AbstractBuilder;
import com.sun.tools.doclets.internal.toolkit.builders.BuilderFactory;
import com.sun.tools.doclets.internal.toolkit.util.*;

/**
 * An abstract implementation of a Doclet.
 *
 * @author <a href="mailto:ivvlev@devives.com">Vladimir Ivanov</a>
 */
public abstract class AbstractDoclet {

    /**
     * The global configuration information for this run.
     */
    public Configuration configuration;

    /**
     * The only doclet that may use this toolkit is {@value}
     */
    private static final String TOOLKIT_DOCLET_NAME =
            RstDoclet.class.getName();

    /**
     * Verify that the only doclet that is using this toolkit is
     * {@link #TOOLKIT_DOCLET_NAME}.
     */
    private boolean isValidDoclet(AbstractDoclet doclet) {
        if (! doclet.getClass().getName().equals(TOOLKIT_DOCLET_NAME)) {
            configuration.message.error("doclet.Toolkit_Usage_Violation",
                TOOLKIT_DOCLET_NAME);
            return false;
        }
        return true;
    }

    /**
     * The method that starts the execution of the doclet.
     *
     * @param doclet the doclet to start the execution for.
     * @param root   the {@link RootDoc} that points to the source to document.
     * @return true if the doclet executed without error.  False otherwise.
     */
    public boolean start(AbstractDoclet doclet, RootDoc root) {
        configuration = configuration();
        configuration.root = root;
        if (! isValidDoclet(doclet)) {
            return false;
        }
        try {
            doclet.startGeneration(root);
        } catch (Configuration.Fault f) {
            root.printError(f.getMessage());
            return false;
        } catch (FatalError fe) {
            return false;
        } catch (DocletAbortException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause.getLocalizedMessage() != null) {
                    root.printError(cause.getLocalizedMessage());
                } else {
                    root.printError(cause.toString());
                }
            }
            return false;
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Indicate that this doclet supports the 1.5 language features.
     * @return JAVA_1_5, indicating that the new features are supported.
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }


    /**
     * Create the configuration instance and returns it.
     * @return the configuration of the doclet.
     */
    public abstract Configuration configuration();

    /**
     * Start the generation of files. Call generate methods in the individual
     * writers, which will in turn generate the documentation files. Call the
     * TreeWriter generation first to ensure the Class Hierarchy is built
     * first and then can be used in the later generation.
     *
     * @see RootDoc
     */
    private void startGeneration(RootDoc root) throws Configuration.Fault, Exception {
        if (root.classes().length == 0) {
            configuration.message.
                error("doclet.No_Public_Classes_To_Document");
            return;
        }
        configuration.setOptions();
        configuration.getDocletSpecificMsg().notice("doclet.build_version",
            configuration.getDocletSpecificBuildDate());
        ClassTree classtree = new ClassTree(configuration, configuration.nodeprecated);

        generateClassFiles(root, classtree);
        Util.copyDocFiles(configuration, DocPaths.DOC_FILES);

        //PackageListWriter.generate(configuration);
        generatePackageFiles(classtree);
        //generateProfileFiles();

        //generateOtherFiles(root, classtree);
        configuration.tagletManager.printReport();
    }

    /**
     * Generate additional documentation that is added to the API documentation.
     *
     * @param root      the RootDoc of source to document.
     * @param classtree the data structure representing the class tree.
     */
    protected void generateOtherFiles(RootDoc root, ClassTree classtree) throws Exception {
        BuilderFactory builderFactory = configuration.getBuilderFactory();
        AbstractBuilder constantsSummaryBuilder = builderFactory.getConstantsSummaryBuider();
        constantsSummaryBuilder.build();
        AbstractBuilder serializedFormBuilder = builderFactory.getSerializedFormBuilder();
        serializedFormBuilder.build();
    }

    /**
     * Generate the package documentation.
     *
     * @param classtree the data structure representing the class tree.
     */
    protected abstract void generatePackageFiles(ClassTree classtree) throws Exception;

    /**
     * Generate the class documentation.
     *
     * @param classtree the data structure representing the class tree.
     */
    protected abstract void generateClassFiles(ClassDoc[] arr, ClassTree classtree);

    /**
     * Iterate through all classes and construct documentation for them.
     *
     * @param root      the RootDoc of source to document.
     * @param classtree the data structure representing the class tree.
     */
    protected void generateClassFiles(RootDoc root, ClassTree classtree) {
        generateClassFiles(classtree);
        PackageDoc[] packages = root.specifiedPackages();
        for (int i = 0; i < packages.length; i++) {
            generateClassFiles(packages[i].allClasses(), classtree);
        }
    }

    /**
     * Generate the class files for single classes specified on the command line.
     *
     * @param classtree the data structure representing the class tree.
     */
    private void generateClassFiles(ClassTree classtree) {
        String[] packageNames = configuration.classDocCatalog.packageNames();
        for (int packageNameIndex = 0; packageNameIndex < packageNames.length;
                packageNameIndex++) {
            generateClassFiles(configuration.classDocCatalog.allClasses(
                packageNames[packageNameIndex]), classtree);
        }
    }
}
