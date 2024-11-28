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
import com.devives.rstdoclet.html.HtmlAdaptersFactory;
import com.devives.rstdoclet.html.HtmlAdaptersFactoryImpl;
import com.devives.sphinx.rst.document.JavaDocRstElementFactoryImpl;
import com.sun.source.util.DocTreePath;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
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
import java.io.PrintWriter;
import java.util.*;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * An abstract implementation of a Doclet.
 *
 * <p><b>This is NOT part of any supported API.
 * If you write code that depends on this, you do so at your own risk.
 * This code and its internal interfaces are subject to change or
 * deletion without notice.</b>
 */
public abstract class AbstractRstDoclet implements Doclet {

    private final HtmlDoclet htmlDoclet_;
    protected RstConfigurationImpl configuration;
    protected HtmlConfiguration htmlConfiguration;
    protected Messages messages;
    protected Utils utils;

    public AbstractRstDoclet() {
        htmlDoclet_ = new LocalHtmlDoclet(this);
        Rst.setElementFactory(new JavaDocRstElementFactoryImpl());
        RstDocletComponentFactory.setInstance(new RstDocletComponentFactoryImpl());
        HtmlAdaptersFactory.setInstance(new HtmlAdaptersFactoryImpl());
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        htmlDoclet_.init(locale, new ReporterFilter(reporter));
        htmlConfiguration = htmlDoclet_.getConfiguration();
        configuration = new RstConfigurationImpl(htmlConfiguration);
        messages = htmlDoclet_.getConfiguration().getMessages();
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return configuration.getOptions().getSupportedOptions();
    }

    /**
     * The method that starts the execution of the doclet.
     *
     * @param docEnv the {@link DocletEnvironment}.
     * @return true if the doclet executed without error.  False otherwise.
     */
    @Override
    public boolean run(DocletEnvironment docEnv) {
        htmlDoclet_.run(docEnv);
        utils = htmlDoclet_.getConfiguration().utils;
        configuration.utils = htmlDoclet_.getConfiguration().utils;
        RstOptions options = configuration.getOptions();
        try {
            try {
                startGeneration();
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
            dumpStack(options.dumpOnError(), e);

        } catch (ResourceIOException e) {
            messages.error("doclet.exception.read.resource",
                    e.resource.getPath(), e.getCause());
            dumpStack(options.dumpOnError(), e);

        } catch (SimpleDocletException e) {
            htmlConfiguration.reporter.print(ERROR, e.getMessage());
            dumpStack(options.dumpOnError(), e);

        } catch (InternalException e) {
            htmlConfiguration.reporter.print(ERROR, e.getMessage());
            reportInternalError(e.getCause());

        } catch (DocletException | RuntimeException | Error e) {
            messages.error("doclet.internal.exception", e);
            reportInternalError(e);
        }

        return false;
    }

    private void reportInternalError(Throwable t) {
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
        return configuration;
    }

    /**
     * Start the generation of files. Call generate methods in the individual
     * writers, which will in turn generate the documentation files. Call the
     * TreeWriter generation first to ensure the Class Hierarchy is built
     * first and then can be used in the later generation.
     *
     * @throws DocletException if there is a problem while generating the documentation
     */
    private void startGeneration() throws DocletException {

        // Modules with no documented classes may be specified on the
        // command line to specify a service provider, allow these.
        if (htmlConfiguration.getSpecifiedModuleElements().isEmpty() &&
                htmlConfiguration.getIncludedTypeElements().isEmpty()) {
            messages.error("doclet.No_Public_Classes_To_Document");
            return;
        }
        if (!htmlConfiguration.setOptions()) {
            return;
        }
        messages.notice("doclet.build_version",
                htmlConfiguration.getDocletVersion());
        ClassTree classtree = new ClassTree(htmlConfiguration);

        generateClassFiles(classtree);

        ElementListWriter.generate(htmlConfiguration);
        generatePackageFiles(classtree);

        htmlConfiguration.tagletManager.printReport();
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
     * @param arr the set of types to be documented
     * @param classtree the data structure representing the class tree
     * @throws DocletException if there is a problem while generating the documentation
     */
    protected abstract void generateClassFiles(SortedSet<TypeElement> arr, ClassTree classtree)
            throws DocletException;

    /**
     * Iterate through all classes and construct documentation for them.
     *
     * @param classtree the data structure representing the class tree
     * @throws DocletException if there is a problem while generating the documentation
     */
    protected void generateClassFiles(ClassTree classtree)
            throws DocletException {

        SortedSet<TypeElement> classes = new TreeSet<>(utils.comparators.makeGeneralPurposeComparator());

        // handle classes specified as files on the command line
        for (PackageElement pkg : htmlConfiguration.typeElementCatalog.packages()) {
            classes.addAll(htmlConfiguration.typeElementCatalog.allClasses(pkg));
        }

        // handle classes specified in modules and packages on the command line
        SortedSet<PackageElement> packages = new TreeSet<>(utils.comparators.makePackageComparator());
        packages.addAll(htmlConfiguration.getSpecifiedPackageElements());
        htmlConfiguration.modulePackages.values().stream().forEach(packages::addAll);
        for (PackageElement pkg : packages) {
            classes.addAll(utils.getAllClasses(pkg));
        }

        generateClassFiles(classes, classtree);
    }


    private static class LocalHtmlDoclet extends HtmlDoclet {
        public LocalHtmlDoclet(Doclet parent) {
            super(parent);
        }
    }

    public class ReporterFilter implements Reporter {

        private final Reporter delegate_;
        private Set<String> filteredMessages_;

        public ReporterFilter(Reporter delegate) {
            delegate_ = delegate;
        }

        private Set<String> getFilteredMessages() {
            if (filteredMessages_ == null) {
                filteredMessages_ = new HashSet<>() {{
                    add(htmlDoclet_.getConfiguration().getDocResources().getText(
                            "doclet.Toolkit_Usage_Violation",
                            HtmlDoclet.class.getName()));
                }};
            }
            return filteredMessages_;
        }

        @Override
        public void print(Diagnostic.Kind kind, String msg) {
            if (getFilteredMessages().contains(msg)) {
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

        @Override // Reporter
        public PrintWriter getStandardWriter() {
            return delegate_.getStandardWriter();
        }

        @Override // Reporter
        public PrintWriter getDiagnosticWriter() {
            return delegate_.getDiagnosticWriter();
        }
    }
}
