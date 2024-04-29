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

import java.util.*;
import java.util.stream.Collectors;

public class RstConfigurationImpl implements RstConfiguration {

    private final HtmlConfiguration htmlConfiguration_;
    public Utils utils;

    /**
     * Argument for command line option "-packageindexfilename".
     */
    private String packageIndexFileName = "package-index";

    public RstConfigurationImpl(HtmlConfiguration htmlConfiguration) {
        htmlConfiguration_ = htmlConfiguration;
    }

    private String formatOptionNames(Doclet.Option opt) {
        return String.join(" ", opt.getNames());
    }

    public Set<Doclet.Option> getSupportedOptions() {
        Map<String, Doclet.Option> superOptions = htmlConfiguration_.getSupportedOptions().stream()
                .collect(Collectors.toMap(this::formatOptionNames, option -> option));

        Resources resources = htmlConfiguration_.getResources();

        List<Doclet.Option> options = List.of(
                new BaseConfiguration.Option(resources, "-packageindexfilename", 1) {
                    @Override
                    public boolean process(String opt, List<String> args) {
                        packageIndexFileName = args.get(0);
                        return true;
                    }
                },
                // For compatibility with gradle javadoc task.
                new BaseConfiguration.Option(resources, "-windowtitle", 1) {

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        return true;
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.OTHER;
                    }
                }
        );

        Set<Doclet.Option> allOptions = new TreeSet<>(options);
        baseOptionFilter.forEach(name -> allOptions.add(superOptions.get(name)));
        htmlOptionFilter.forEach(name -> allOptions.add(superOptions.get(name)));
        return allOptions;
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


    private final Set<String> baseOptionFilter = new HashSet<>(Arrays.asList(
//                "--allow-script-in-comments",
//                "--disable-javafx-strict-checks",
            "--dump-on-error",
//                "--javafx -javafx",
//                "--link-platform-properties",
//                "--no-platform-links",
//                "--override-methods",
//                "--show-taglets",
//                "--since",
//                "--since-label",
//                "-author",
            "-d",
            "-docencoding",
//                "-docfilessubdirs",
            "-encoding",
//                "-excludedocfilessubdir",
//                "-group",
//                "-keywords",
            "-link",
            "-linkoffline",
            "-linksource",
//            "-nocomment",
            "-nodeprecated",
//            "-noqualifier",
            "-nosince",
            "-notimestamp",
            "-quiet"
//                "-serialwarn",
//                "-sourcetab",
//                "-tag",
//                "-taglet",
//                "-tagletpath",
//                "-version"
    ));

    private final Set<String> htmlOptionFilter = new HashSet<>(Arrays.asList(
//                "--add-stylesheet",
//                "--legal-notices",
//                "--main-stylesheet -stylesheetfile",
//                "--no-frames",
//                "-Xdoclint",
//                "-Xdoclint/package:",
//                "-Xdoclint:",
            "-Xdocrootparent",
//                "-bottom",
            "-charset",
            "-doctitle"
//                "-footer",
//                "-header",
//                "-helpfile",
//                "-html5",
//                "-nodeprecatedlist",
//                "-nohelp",
//                "-noindex",
//                "-nonavbar",
//                "-nooverview",
//                "-notree",
//                "-overview",
//                "-packagesheader",
//                "-splitindex",
//                "-top",
//                "-use",
//                "-windowtitle"
    ));
}
