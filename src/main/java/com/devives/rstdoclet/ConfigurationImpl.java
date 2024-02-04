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

import com.devives.rstdoclet.html2rst.jdkloans.ContentBuilder;
import com.devives.rstdoclet.util.CompositeMessageRetriever;
import com.sun.javadoc.*;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.WriterFactory;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.StringUtils;
import com.sun.tools.javadoc.RootDocImpl;

import javax.tools.JavaFileManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Configure the output based on the command line options.
 * <p>
 * Also determine the length of the command line option. For example,
 * for a option "-header" there will be a string argument associated, then the
 * the length of option "-header" is two. But for option "-nohelp" no argument
 * is needed so it's length is 1.
 * </p>
 * <p>
 * Also do the error checking on the options used. For example it is illegal to
 * use "-helpfile" option when already "-nohelp" option is used.
 * </p>
 *
 * <p><b>This is NOT part of any supported API.
 * If you write code that depends on this, you do so at your own risk.
 * This code and its internal interfaces are subject to change or
 * deletion without notice.</b>
 *
 * @author Robert Field.
 * @author Atul Dambalkar.
 * @author Jamie Ho
 * @author Bhavesh Patel (Modified)
 */
public class ConfigurationImpl extends Configuration {

    /**
     * The build date.  Note: For now, we will use
     * a version number instead of a date.
     */
    public static final String BUILD_DATE = System.getProperty("java.version");

    /**
     * Argument for command line option "-Xdocrootparent".
     */
    public String docrootparent = "";

    /**
     * False if command line option "-noindex" is used. Default value is true.
     */
    public boolean createIndex = true;

    /**
     * Argument for command line option "-packageindexfilename".
     */
    public String packageIndexFileName = "package-index";

    /**
     * True if command line option "-nodeprecated" is used. Default value is
     * false.
     */
    public boolean noDeprecatedList = false;

    /**
     * Unique Resource Handler for this package.
     */
    public final MessageRetriever standardmessage;

    /**
     * Constructor. Initializes resource for the
     * {@link com.sun.tools.doclets.internal.toolkit.util.MessageRetriever MessageRetriever}.
     */
    public ConfigurationImpl() {
        standardmessage = new CompositeMessageRetriever(this,
                "com.devives.rstdoclet.resources.standard",
                "com.sun.tools.doclets.formats.html.resources.standard");
    }

    private final String versionRBName = "com.sun.tools.javadoc.resources.version";
    private ResourceBundle versionRB;

    /**
     * Return the build date for the doclet.
     */
    @Override
    public String getDocletSpecificBuildDate() {
        if (versionRB == null) {
            try {
                versionRB = ResourceBundle.getBundle(versionRBName);
            } catch (MissingResourceException e) {
                return BUILD_DATE;
            }
        }

        try {
            return versionRB.getString("release");
        } catch (MissingResourceException e) {
            return BUILD_DATE;
        }
    }

    /**
     * Depending upon the command line options provided by the user, set
     * configure the output generation environment.
     *
     * @param options The array of option names and values.
     */
    @Override
    public void setSpecificDocletOptions(String[][] options) {
        for (int oi = 0; oi < options.length; ++oi) {
            String[] os = options[oi];
            String opt = StringUtils.toLowerCase(os[0]);
            if (opt.equals("-charset")) {
                charset = os[1];
            } else if (opt.equals("-xdocrootparent")) {
                docrootparent = os[1];
            } else if (opt.equals("-noindex")) {
                createIndex = false;
            } else if (opt.equals("-nodeprecatedlist")) {
                noDeprecatedList = true;
            } else if (opt.equals("-packageindexfilename")) {
                packageIndexFileName = os[1];
            } else if (opt.equals("-footer")) {
                // Ignore standard doclet options
            } else if (opt.equals("-header")) {
            } else if (opt.equals("-packagesheader")) {
            } else if (opt.equals("-doctitle")) {
            } else if (opt.equals("-windowtitle")) {
            } else if (opt.equals("-top")) {
            } else if (opt.equals("-bottom")) {
            } else if (opt.equals("-helpfile")) {
            } else if (opt.equals("-stylesheetfile")) {
            } else if (opt.equals("-nohelp")) {
            } else if (opt.equals("-splitindex")) {
            } else if (opt.equals("-use")) {
            } else if (opt.equals("-notree")) {
            } else if (opt.equals("-nonavbar")) {
            } else if (opt.equals("-nooverview")) {
            } else if (opt.equals("-overview")) {
            } else if (opt.equals("-xdoclint")) {
            } else if (opt.startsWith("-xdoclint:")) {
            } else if (opt.equals("--allow-script-in-comments")) {
            }
        }

        if (root.specifiedClasses().length > 0) {
            Map<String, PackageDoc> map = new HashMap<String, PackageDoc>();
            PackageDoc pd;
            ClassDoc[] classes = root.classes();
            for (int i = 0; i < classes.length; i++) {
                pd = classes[i].containingPackage();
                if (!map.containsKey(pd.name())) {
                    map.put(pd.name(), pd);
                }
            }
        }
    }

    /**
     * Returns the "length" of a given option. If an option takes no
     * arguments, its length is one. If it takes one argument, it's
     * length is two, and so on. This method is called by JavaDoc to
     * parse the options it does not recognize. It then calls
     * {@link #validOptions(String[][], DocErrorReporter)} to
     * validate them.
     * <b>Note:</b><br>
     * The options arrive as case-sensitive strings. For options that
     * are not case-sensitive, use toLowerCase() on the option string
     * before comparing it.
     *
     * @return number of arguments + 1 for a option. Zero return means
     * option not known.  Negative value means error occurred.
     */
    public int optionLength(String option) {
        int result = -1;
        if ((result = super.optionLength(option)) > 0) {
            return result;
        }
        // otherwise look for the options we have added
        option = StringUtils.toLowerCase(option);
        if (option.equals("-nodeprecatedlist") ||
                option.equals("-noindex") ||
                option.equals("-notree") ||
                option.equals("-nohelp") ||
                option.equals("-splitindex") ||
                option.equals("-serialwarn") ||
                option.equals("-use") ||
                option.equals("-nonavbar") ||
                option.equals("-nooverview") ||
                option.equals("-xdoclint") ||
                option.startsWith("-xdoclint:") ||
                option.equals("--allow-script-in-comments")) {
            return 1;
        } else if (option.equals("-help")) {
            // Uugh: first, this should not be hidden inside optionLength,
            // and second, we should not be writing directly to stdout.
            // But we have no access to a DocErrorReporter, which would
            // allow use of reporter.printNotice
            System.out.println(getText("rstdoclet.usage"));
            return 1;
        } else if (option.equals("-x")) {
            // Uugh: first, this should not be hidden inside optionLength,
            // and second, we should not be writing directly to stdout.
            // But we have no access to a DocErrorReporter, which would
            // allow use of reporter.printNotice
            System.out.println(getText("rstdoclet.X.usage"));
            return 1;
        } else if (option.equals("-packageindexfilename") ||
                option.equals("-footer") ||
                option.equals("-header") ||
                option.equals("-packagesheader") ||
                option.equals("-doctitle") ||
                option.equals("-windowtitle") ||
                option.equals("-top") ||
                option.equals("-bottom") ||
                option.equals("-helpfile") ||
                option.equals("-stylesheetfile") ||
                option.equals("-charset") ||
                option.equals("-overview") ||
                option.equals("-xdocrootparent")) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validOptions(String options[][],
                                DocErrorReporter reporter) {
        // check shared options
        if (!generalValidOptions(options, reporter)) {
            return false;
        }
        // otherwise look at our options
        for (int oi = 0; oi < options.length; ++oi) {
            String[] os = options[oi];
            String opt = StringUtils.toLowerCase(os[0]);
            if (opt.equals("-xdocrootparent")) {
                try {
                    new URL(os[1]);
                } catch (MalformedURLException e) {
                    reporter.printError(getText("doclet.MalformedURL", os[1]));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRetriever getDocletSpecificMsg() {
        return standardmessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WriterFactory getWriterFactory() {
        throw new RuntimeException("Not supported method.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Comparator<ProgramElementDoc> getMemberComparator() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
        if (root instanceof RootDocImpl)
            return ((RootDocImpl) root).getLocale();
        else
            return Locale.getDefault();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaFileManager getFileManager() {
        if (fileManager == null) {
            if (root instanceof RootDocImpl)
                fileManager = ((RootDocImpl) root).getFileManager();
            else
                fileManager = new JavacFileManager(new Context(), false, null);
        }
        return fileManager;
    }

    private JavaFileManager fileManager;

    @Override
    public boolean showMessage(SourcePosition pos, String key) {
        if (root instanceof RootDocImpl) {
            return pos == null || ((RootDocImpl) root).showTagMessages();
        }
        return true;
    }

    @Override
    public Content newContent() {
        return new ContentBuilder();
    }

}
