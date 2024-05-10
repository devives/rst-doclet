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
import com.devives.rstdoclet.html.HtmlAdaptersFactory;
import com.devives.rstdoclet.html.HtmlAdaptersFactoryImpl;
import com.devives.sphinx.rst.document.JavaDocRstElementFactoryImpl;
import com.sun.source.util.DocTreePath;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.HtmlDoclet;
import jdk.javadoc.internal.doclets.toolkit.DocletException;
import jdk.javadoc.internal.doclets.toolkit.Messages;
import jdk.javadoc.internal.doclets.toolkit.util.*;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;

import static javax.tools.Diagnostic.Kind.ERROR;

abstract class AbstractRstDoclet implements Doclet {

    private final HtmlDoclet htmlDoclet_;
    protected RstConfigurationImpl rstConfiguration;
    protected HtmlConfiguration configuration;
    protected final Messages messages;
    protected Utils utils;

    public AbstractRstDoclet() {
        htmlDoclet_ = new LocalHtmlDoclet(this);
        configuration = htmlDoclet_.getConfiguration();
        messages = htmlDoclet_.getConfiguration().getMessages();
        Rst.setElementFactory(new JavaDocRstElementFactoryImpl());
        RstDocletComponentFactory.setInstance(new RstDocletComponentFactoryImpl());
        HtmlAdaptersFactory.setInstance(new HtmlAdaptersFactoryImpl());
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        htmlDoclet_.init(locale, new ReporterFilter(reporter));
        rstConfiguration = new RstConfigurationImpl(htmlDoclet_.getConfiguration());
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<Option> getSupportedOptions() {
        return rstConfiguration.getSupportedOptions();
    }

    @Override
    public boolean run(DocletEnvironment docEnv) {
        htmlDoclet_.run(docEnv);
        utils = htmlDoclet_.getConfiguration().utils;
        rstConfiguration.utils = htmlDoclet_.getConfiguration().utils;
        try {
            try {
                startGeneration(docEnv);
                return true;
            } catch (UncheckedDocletException e) {
                throw (DocletException) e.getCause();
            }

        } catch (DocFileIOException e) {
            switch (e.mode) {
                case READ:
                    messages.error("doclet.exception.read.file",
                            e.fileName.getPath(), e.getCause());
                    break;
                case WRITE:
                    messages.error("doclet.exception.write.file",
                            e.fileName.getPath(), e.getCause());
            }
            dumpStack(configuration.dumpOnError, e);

        } catch (ResourceIOException e) {
            messages.error("doclet.exception.read.resource",
                    e.resource.getPath(), e.getCause());
            dumpStack(configuration.dumpOnError, e);

        } catch (SimpleDocletException e) {
            configuration.reporter.print(ERROR, e.getMessage());
            dumpStack(configuration.dumpOnError, e);

        } catch (InternalException e) {
            configuration.reporter.print(ERROR, e.getMessage());
            reportInternalError(e.getCause());

        } catch (DocletException | RuntimeException | Error e) {
            messages.error("doclet.internal.exception", e);
            reportInternalError(e);
        }

        return false;
    }

    private void reportInternalError(Throwable t) {
        if (getClass().equals(StandardDoclet.class) || getClass().equals(HtmlDoclet.class)) {
            System.err.println(configuration.getResources().getText("doclet.internal.report.bug"));
        }
        dumpStack(true, t);
    }

    private void dumpStack(boolean enabled, Throwable t) {
        if (enabled && t != null) {
            t.printStackTrace(System.err);
        }
    }

    /**
     * Returns the SourceVersion indicating the features supported by this doclet.
     *
     * @return SourceVersion
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_9;
    }

    /**
     * Create the configuration instance and returns it.
     *
     * @return the configuration of the doclet.
     */
    public RstConfigurationImpl getConfiguration() {
        return rstConfiguration;
    }

    /**
     * Start the generation of files. Call generate methods in the individual
     * writers, which will in turn generate the documentation files. Call the
     * TreeWriter generation first to ensure the Class Hierarchy is built
     * first and then can be used in the later generation.
     *
     * @throws DocletException if there is a problem while generating the documentation
     */
    private void startGeneration(DocletEnvironment docEnv) throws DocletException {

        // Modules with no documented classes may be specified on the
        // command line to specify a service provider, allow these.
        if (configuration.getSpecifiedModuleElements().isEmpty() &&
                configuration.getIncludedTypeElements().isEmpty()) {
            messages.error("doclet.No_Public_Classes_To_Document");
            return;
        }
        if (!configuration.setOptions()) {
            return;
        }
        messages.notice("doclet.build_version",
                configuration.getDocletVersion());
        ClassTree classtree = new ClassTree(configuration, configuration.nodeprecated);

        generateClassFiles(docEnv, classtree);

        ElementListWriter.generate(configuration);
        generatePackageFiles(classtree);

        configuration.tagletManager.printReport();
    }

    /**
     * Generate the package documentation.
     *
     * @param classtree the data structure representing the class tree
     * @throws DocletException if there is a problem while generating the documentation
     */
    protected abstract void generatePackageFiles(ClassTree classtree) throws DocletException;

    /**
     * Generate the class documentation.
     *
     * @param arr       the set of types to be documented
     * @param classtree the data structure representing the class tree
     * @throws DocletException if there is a problem while generating the documentation
     */
    protected abstract void generateClassFiles(SortedSet<TypeElement> arr, ClassTree classtree)
            throws DocletException;

    /**
     * Iterate through all classes and construct documentation for them.
     *
     * @param docEnv    the DocletEnvironment
     * @param classtree the data structure representing the class tree
     * @throws DocletException if there is a problem while generating the documentation
     */
    protected void generateClassFiles(DocletEnvironment docEnv, ClassTree classtree)
            throws DocletException {
        generateClassFiles(classtree);
        SortedSet<PackageElement> packages = new TreeSet<>(configuration.utils.makePackageComparator());
        packages.addAll(configuration.getSpecifiedPackageElements());
        configuration.modulePackages.values().stream().forEach(packages::addAll);
        for (PackageElement pkg : packages) {
            generateClassFiles(configuration.utils.getAllClasses(pkg), classtree);
        }
    }

    /**
     * Generate the class files for single classes specified on the command line.
     *
     * @param classtree the data structure representing the class tree
     * @throws DocletException if there is a problem while generating the documentation
     */
    private void generateClassFiles(ClassTree classtree) throws DocletException {
        SortedSet<PackageElement> packages = configuration.typeElementCatalog.packages();
        for (PackageElement pkg : packages) {
            generateClassFiles(configuration.typeElementCatalog.allClasses(pkg), classtree);
        }
    }

    private static class LocalHtmlDoclet extends HtmlDoclet {
        public LocalHtmlDoclet(Doclet parent) {
            super(parent);
        }
    }

    public class ReporterFilter implements Reporter {

        private final Reporter delegate_;
        private final Set<String> filteredMessages_;

        public ReporterFilter(Reporter delegate) {
            delegate_ = delegate;
            filteredMessages_ = new HashSet<>() {{
                add(htmlDoclet_.getConfiguration().getResources().getText(
                        "doclet.Toolkit_Usage_Violation",
                        HtmlDoclet.class.getName()));
            }};
        }

        @Override
        public void print(Diagnostic.Kind kind, String msg) {
            if (filteredMessages_.contains(msg)) {
                return;
            }
            delegate_.print(kind, msg);
        }

        @Override
        public void print(Diagnostic.Kind kind, DocTreePath path, String msg) {
            delegate_.print(kind, path, msg);
        }

        @Override
        public void print(Diagnostic.Kind kind, Element e, String msg) {
            delegate_.print(kind, e, msg);
        }
    }
}
