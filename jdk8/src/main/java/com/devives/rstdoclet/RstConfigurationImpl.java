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

import com.sun.javadoc.DocErrorReporter;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;
import com.sun.tools.javac.util.StringUtils;

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
public class RstConfigurationImpl {

    private final ConfigurationImpl htmlConfiguration_;
    /**
     * Unique Resource Handler for this package.
     */
    public final MessageRetriever standardMessages;

    /**
     * Argument for command line option "-packageindexfilename".
     */
    public String packageIndexFileName = "package-index";

    public RstConfigurationImpl(ConfigurationImpl htmlConfiguration) {
        htmlConfiguration_ = htmlConfiguration;
        standardMessages = new CompositeMessageRetriever(
                htmlConfiguration.message,
                htmlConfiguration,
                "com.devives.rstdoclet.resources.standard");
        htmlConfiguration_.message = standardMessages;
    }

    public ConfigurationImpl getHtmlConfiguration() {
        return htmlConfiguration_;
    }

    /**
     * Return the build date for the doclet.
     */
    public String getDocletSpecificBuildDate() {
        return htmlConfiguration_.getDocletSpecificBuildDate();
    }

    /**
     * Depending upon the command line options provided by the user, set
     * configure the output generation environment.
     *
     * @param options The array of option names and values.
     */
    public void setSpecificDocletOptions(String[][] options) {
        for (int oi = 0; oi < options.length; ++oi) {
            String[] os = options[oi];
            String opt = StringUtils.toLowerCase(os[0]);
            if (opt.equals("-packageindexfilename")) {
                packageIndexFileName = os[1];
            }
        }
//        String[][] filteredOptions = Arrays.stream(options)
//                .filter(itm -> !StringUtils.toLowerCase(itm[0]).equals("-packageindexfilename"))
//                .toArray(String[][]::new);
//        htmlConfiguration_.setSpecificDocletOptions(filteredOptions);
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
        if ((result = htmlConfiguration_.optionLength(option)) > 0) {
            return result;
        }
        // otherwise look for the options we have added
        option = StringUtils.toLowerCase(option);
        if (option.equals("-packageindexfilename")) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * Perform error checking on the given options.
     *
     * @param options  the given options to check.
     * @param reporter the reporter used to report errors.
     */
    public boolean validOptions(String[][] options, DocErrorReporter reporter) {
        return htmlConfiguration_.validOptions(options, reporter);
    }

    public MessageRetriever getDocletSpecificMsg() {
        return standardMessages;
    }

    public void setOptions() throws Configuration.Fault {
        htmlConfiguration_.setOptions();
        setSpecificDocletOptions(htmlConfiguration_.root.options());
    }

}
