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

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.internal.doclets.formats.html.HtmlOptions;
import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;
import jdk.javadoc.internal.doclets.toolkit.Resources;

import java.util.*;
import java.util.stream.Collectors;

public class RstOptions {

    private final HtmlOptions htmlOptions_;
    private final BaseConfiguration config_;
    private final LocalBaseOptions internalOptions_;
    /**
     * Argument for command line option "-packageindexfilename".
     */
    private String packageIndexFileName = "package-index";

    /**
     * Argument for command-line option {@code -d}.
     * Destination directory name, in which doclet will generate the entire
     * documentation. Default is current directory.
     */
    private String destDirName = "";

    /**
     * Argument for command-line option {@code -doctitle}.
     */
    private String docTitle = "";

    /**
     * Argument for hidden command-line option {@code --dump-on-error}.
     */
    private boolean dumpOnError = false;

    protected RstOptions(HtmlOptions htmlOptions, BaseConfiguration config) {
        config_ = Objects.requireNonNull(config);
        htmlOptions_ = Objects.requireNonNull(htmlOptions);
        internalOptions_ = new LocalBaseOptions(config_);
    }

    public HtmlOptions getHtmlOptions() {
        return htmlOptions_;
    }

    /**
     * Argument for command line option "-packageindexfilename".
     */
    public String getPackageIndexFileName() {
        return packageIndexFileName;
    }

    public Set<? extends Doclet.Option> getSupportedOptions() {
        return internalOptions_.overrideOptions();
    }

    public String destDirName() {
        return destDirName;
    }

    public boolean dumpOnError() {
        return dumpOnError;
    }

    public String docTitle() {
        return docTitle;
    }

    private class LocalBaseOptions extends jdk.javadoc.internal.doclets.toolkit.BaseOptions {

        protected LocalBaseOptions(BaseConfiguration config) {
            super(config);
        }

        private String formatOptionNames(Doclet.Option opt) {
            return String.join(" ", opt.getNames());
        }

        public Set<? extends Doclet.Option> overrideOptions() {
            Set<? extends Option> superOptions = htmlOptions_.getSupportedOptions();

            Set<String> baseOptionNames = this.getSupportedOptions().stream()
                    .map(this::formatOptionNames)
                    .collect(Collectors.toSet());
            Map<String, Option> superBaseOptions = superOptions.stream()
                    .filter(opt -> baseOptionNames.contains(formatOptionNames(opt)))
                    .collect(Collectors.toMap(this::formatOptionNames, opt -> opt));
            Map<String, Option> superHtmlOptions = superOptions.stream()
                    .filter(opt -> !superBaseOptions.containsKey(formatOptionNames(opt)))
                    .collect(Collectors.toMap(this::formatOptionNames, opt -> opt));

            Resources resources = config_.getDocResources();

            List<Doclet.Option> options = List.of(
                    new LocalBaseOptions.Option(resources, "-packageindexfilename", 1) {
                        @Override
                        public boolean process(String opt, List<String> args) {
                            packageIndexFileName = args.get(0);
                            return true;
                        }
                    },
                    new OverrideOption(resources, superBaseOptions.get("-d")) {
                        @Override
                        public boolean process(String opt, List<String> args) {
                            boolean result = super.process(opt, args);
                            destDirName = addTrailingFileSep(args.get(0));
                            return result;
                        }
                    },
                    new OverrideOption(resources, superBaseOptions.get("--dump-on-error")) {
                        @Override
                        public boolean process(String opt, List<String> args) {
                            boolean result = super.process(opt, args);
                            dumpOnError = true;
                            return result;
                        }
                    },
                    new OverrideOption(resources, superHtmlOptions.get("-doctitle")) {
                        @Override
                        public boolean process(String opt, List<String> args) {
                            boolean result = super.process(opt, args);
                            docTitle = args.get(0);
                            return result;
                        }
                    },
                    // For compatibility with gradle javadoc task.
                    new OverrideOption(resources, superHtmlOptions.get("-windowtitle")) {
                        @Override
                        public Kind getKind() {
                            return Kind.OTHER;
                        }
                    }
            );

            Set<Doclet.Option> allOptions = new TreeSet<>(options);
            baseOptionFilter.forEach(name -> allOptions.add(superBaseOptions.get(name)));
            htmlOptionFilter.forEach(name -> allOptions.add(superHtmlOptions.get(name)));
            return allOptions;
        }

        private class OverrideOption extends Option {

            private final Option option;

            public OverrideOption(Resources resources, Option option) {
                super(resources, formatOptionNames(option), option.getArgumentCount());
                this.option = Objects.requireNonNull(option);
            }

            @Override
            public int getArgumentCount() {
                return option.getArgumentCount();
            }

            @Override
            public String getDescription() {
                return option.getDescription();
            }

            @Override
            public Doclet.Option.Kind getKind() {
                return option.getKind();
            }

            @Override
            public List<String> getNames() {
                return option.getNames();
            }

            @Override
            public String getParameters() {
                return option.getParameters();
            }

            @Override
            public boolean process(String opt, List<String> args) {
                return option.process(opt, args);
            }

            @Override
            public int compareTo(Option o) {
                return option.compareTo(o);
            }
        }

        private final Set<String> baseOptionFilter = new HashSet<>(Arrays.asList(
//                "--allow-script-in-comments",
//                "--disable-javafx-strict-checks",
//                "--dump-on-error",
//                "--javafx -javafx",
//                "--link-platform-properties",
//                "--no-platform-links",
//                "--override-methods",
//                "--show-taglets",
//                "--since",
//                "--since-label",
//                "-author",
//                "-d",
                "-docencoding",
//                "-docfilessubdirs",
                "-encoding",
//                "-excludedocfilessubdir",
//                "-group",
//                "-keywords",
                "-link",
                "-linkoffline",
                "-linksource",
//                "-nocomment",
                "-nodeprecated",
//                "-noqualifier",
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
                "-charset"
//                "-doctitle",
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
}
