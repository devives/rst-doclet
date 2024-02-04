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
package com.devives;

import com.devives.rst.Rst;
import com.devives.rst.document.RstDocument;
import com.devives.rst.document.directive.Directives;
import com.devives.rst.util.Constants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class ReadMeGenerator implements Constants {

    private final Path rootPath = Paths.get("").toAbsolutePath().endsWith("RstProjectGroup")
            ? Paths.get("rst-doclet").toAbsolutePath()
            : Paths.get("").toAbsolutePath();
    private final Path resultPath = rootPath.resolve("README.rst");

    public static void main(String[] args) throws IOException {
        new ReadMeGenerator().run();
    }

    private void run() throws IOException {
        RstDocument document = Rst.builders().document()
                .title("RstDoclet for JavaDoc Tool", true)
                .paragraph(p -> p
                        .text("This project is the extension of ")
                        .link("https://www.oracle.com/java/technologies/javase/javadoc-tool.html", "Javadoc Tool").text(". "))
                .paragraph(p -> p
                        .text("It's Implement ").link("https://docs.oracle.com/javase/8/docs/technotes/guides/javadoc/doclet/overview.html", "Doclet").text(" ").lineBreak()
                        .text("witch generates ").link("https://www.sphinx-doc.org/en/master/usage/restructuredtext/index.html", "reStructuredText").lineBreak()
                        .text("files based on ").link("https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html", "javadoc").lineBreak()
                        .text(" comments in code."))
                .paragraph(p -> p
                        .text("Generated ").literal("*.rst").text(" files can be published using ").lineBreak()
                        .link("https://www.sphinx-doc.org/en/master/", "Sphinx").text(". "))
                .note(a -> a.paragraph(p -> p
                        .text("The ")
                        .link("https://bronto-javasphinx.readthedocs.io/en/latest/", "javasphinx")
                        .text(" extension must be installed in Sphinx.")))
                .beginDirective(Directives.Contents).setArguments("Overview").options(opts -> opts.item("depth", 2))
                .end()
                .subTitle("Limitations")
                .paragraph(p -> p.bold("Current version works only with Java 8, because require %JAVA_HOME%\\lib\\tools.jar for extracting javadoc comments."))
                .subTitle("Quick Start")
                .numberedList(list -> list
                        .item(itm -> itm
                                .paragraph(p -> p.text("Add ").literal("mavencentral()").text(" repository to your root ").literal("build.gradle").text(":"))
                                .code("gradle", "repositories {\n" +
                                        "    mavenCentral()\n" +
                                        "}"))
                        .item(itm -> itm
                                .paragraph(p -> p.text("Add ").literal("rstDoclet").text("configuration to configurations:"))
                                .code("gradle", "configurations {\n" +
                                        "    rstDoclet\n" +
                                        "}"))
                        .item(itm -> itm
                                .paragraph("Add library to dependencies:")
                                .code("gradle", "dependencies {\n" +
                                        "    rstDoclet('com.devives:devive-rst-doclet-jdk8-all:0.2.0')\n" +
                                        "}"))
                        .item(itm -> itm
                                .paragraph(p -> p.text("Register gradle task ").literal("javadoc2rst").text(":"))
                                .code("gradle", "tasks.register('javadoc2rst', Javadoc) {\n" +
                                        "    description = 'Generate rst files based on javadoc comments in code.'\n" +
                                        "    group = 'documentation'\n" +
                                        "    source = sourceSets.main.allJava\n" +
                                        "    destinationDir = file(\"$docsDir/javadoc2rst\")\n" +
                                        "    options.docletpath = configurations.rstDoclet.files.asType(List)\n" +
                                        "    options.doclet = \"com.devives.rstdoclet.RstDoclet\"\n" +
                                        "    options.encoding = \"UTF-8\"\n" +
                                        "    options.showFromPackage()\n" +
                                        "    failOnError = false\n" +
                                        "    (options as CoreJavadocOptions).addStringOption(\"packageindexfilename\", \"package-summary\")\n" +
                                        "}"))
                        .item("Reload All Gradle Projects.")
                        .item(itm -> itm.paragraph(p -> p.text("Execute gradle task ").literal("documentation \\ javadoc2rst").text(".")))
                        .item(itm -> itm.paragraph(p -> p.text("Find generated files at ").literal("$project.build/docs/javadoc2rst/").text(".")))
                )
                .subTitle("License")
                .paragraph(p -> p
                        .text("The code of project distributed under the GNU General Public License version 2 only. ").lineBreak()
                        .text("The source code is available on ").link("https://github.com/devives/rst-doclet", "GitHub").text("."))
                .paragraph(p -> p.text("Why ").literal("GNU General Public License version 2 only").text("?"))
                .indentedLiteralBlock(lb -> lb
                        .text("The RstDoclet project has several code borrowings from JDK, which redistribute under GNU General Public License version 2 only.")
                        .lineBreak()
                        .text("May be later, borrowings will be removed and license can be changed. All depends on future and demand of the project.")
                )
                .subTitle("Links")
                .beginBulletList()
                .item(itm -> itm.paragraph(p -> p.link("https://www.oracle.com/java/technologies/javase/javadoc-tool.html", "Javadoc Tool")))
                .item(itm -> itm.paragraph(p -> p.link("https://github.com/devives/rst-document-for-sphinx", "ReStructuredText Document & Builder for Sphinx")))
                .item(itm -> itm.paragraph(p -> p.link("https://github.com/devives/rst-document", "ReStructuredText Document & Builder")))
                .end()
                .directive(Directives.Footer, a -> a.paragraph(p -> p
                        .text("This document generated using ")
                        .link("https://github.com/devives/rst-doclet/blob/main/src/test/java/com/devives/ReadMeGenerator.java", "this code")
                        .text(".")))
                .build();


        String rstText = document.serialize();
        Files.createDirectories(resultPath.getParent());
        Files.write(resultPath, Arrays.asList(rstText.split(NL)), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
