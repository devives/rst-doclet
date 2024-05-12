/**
 * RstDoclet for JavaDoc Tool, generating reStructuredText for Sphinx.
 * Copyright (C) 2023-2024 Vladimir Ivanov <ivvlev@devives.com>.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.devives.rstdoclet;

import com.devives.rst.Rst;
import com.devives.sphinx.rst.document.JavaDocRstElementFactoryImpl;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.*;

/**
 * An abstract implementation of a Doclet.
 *
 * @see com.sun.javadoc.Doclet
 */
public abstract class AbstractRstDoclet {

    /**
     * The global configuration information for this run.
     */
    private final HtmlDoclet htmlDoclet_;
    public final ConfigurationImpl configuration;
    public MessageRetriever messages;
    public RstConfigurationImpl rstConfiguration;

    public AbstractRstDoclet() {
        htmlDoclet_ = new LocalHtmlDoclet();
        configuration = (ConfigurationImpl) htmlDoclet_.configuration();
        messages = htmlDoclet_.configuration().message;
        rstConfiguration = new RstConfigurationImpl(configuration);
        Rst.setElementFactory(new JavaDocRstElementFactoryImpl());
    }

    /**
     * The method that starts the execution of the doclet.
     *
     * @param doclet the doclet to start the execution for.
     * @param root   the {@link com.sun.javadoc.RootDoc} that points to the source to document.
     * @return true if the doclet executed without error.  False otherwise.
     */
    public boolean start(AbstractRstDoclet doclet, RootDoc root) {
        htmlDoclet_.start(htmlDoclet_, root);
        configuration.root = root;
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
    private void startGeneration(RootDoc root) throws Exception {
        if (root.classes().length == 0) {
            configuration.message.
                    error("doclet.No_Public_Classes_To_Document");
            return;
        }
        rstConfiguration.setOptions();
        configuration.getDocletSpecificMsg().notice("doclet.build_version",
                configuration.getDocletSpecificBuildDate());
        ClassTree classtree = new ClassTree(configuration, configuration.nodeprecated);

        generateClassFiles(root, classtree);
        Util.copyDocFiles(configuration, DocPaths.DOC_FILES);

        generatePackageFiles(classtree);

        configuration.tagletManager.printReport();
    }

    /**
     * Generate the package documentation.
     *
     * @param classTree the data structure representing the class tree.
     */
    protected abstract void generatePackageFiles(ClassTree classTree) throws Exception;

    /**
     * Generate the class documentation.
     *
     * @param classTree the data structure representing the class tree.
     */
    protected abstract void generateClassFiles(ClassDoc[] arr, ClassTree classTree);

    /**
     * Iterate through all classes and construct documentation for them.
     *
     * @param root      the RootDoc of source to document.
     * @param classTree the data structure representing the class tree.
     */
    protected void generateClassFiles(RootDoc root, ClassTree classTree) {
        generateClassFiles(classTree);
        PackageDoc[] packages = root.specifiedPackages();
        for (int i = 0; i < packages.length; i++) {
            generateClassFiles(packages[i].allClasses(), classTree);
        }
    }

    /**
     * Generate the class files for single classes specified on the command line.
     *
     * @param classTree the data structure representing the class tree.
     */
    private void generateClassFiles(ClassTree classTree) {
        String[] packageNames = configuration.classDocCatalog.packageNames();
        for (int packageNameIndex = 0; packageNameIndex < packageNames.length;
             packageNameIndex++) {
            generateClassFiles(configuration.classDocCatalog.allClasses(
                    packageNames[packageNameIndex]), classTree);
        }
    }

    private static class LocalHtmlDoclet extends HtmlDoclet {

    }

}
