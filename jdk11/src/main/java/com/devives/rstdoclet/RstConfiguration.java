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

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;
import jdk.javadoc.internal.doclets.toolkit.Resources;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import java.util.List;
import java.util.Set;

public class RstConfiguration {

    private final HtmlConfiguration htmlConfiguration_;
    public Utils utils;

    /**
     * Argument for command line option "-packageindexfilename".
     */
    private String packageIndexFileName = "package-index";

    public RstConfiguration(HtmlConfiguration htmlConfiguration) {
        htmlConfiguration_ = htmlConfiguration;
    }

    public Set<Doclet.Option> getSupportedOptions() {
        Set<Doclet.Option> options = htmlConfiguration_.getSupportedOptions();
        Resources resources = htmlConfiguration_.getResources();
        options.add(new BaseConfiguration.Option(resources, "-packageIndexFileName", 1) {
            @Override
            public boolean process(String opt, List<String> args) {
                packageIndexFileName = args.get(0);
                return true;
            }
        });
        return options;
    }

    public HtmlConfiguration getHtmlConfiguration() {
        return htmlConfiguration_;
    }

    /**
     * Argument for command line option "-packageindexfilename".
     */
    public String getPackageIndexFileName() {
        return packageIndexFileName;
    }

}
