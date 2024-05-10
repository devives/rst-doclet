/**
 * RstDoclet for JavaDoc Tool, generating reStructuredText for Sphinx.
 * Copyright (C) 2023-2024 Vladimir Ivanov <ivvlev@devives.com>.
 * <p>
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation..
 * <p>
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.devives.sphinx.java.doc;

import com.devives.rst.util.StringUtils;
import com.devives.sphinx.rst.Rst4Sphinx;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Supplier;

public class PackagesIndexRstGenerator implements Supplier<String> {

    private String packageIndexFileName_ = "package-index";
    private String title_;
    private String[] packages_;

    public PackagesIndexRstGenerator(String[] packages) {
        setPackages(packages);
    }

    @Override
    public String get() {
        return Rst4Sphinx.builders().document()
                .title(StringUtils.findFirstNotNullOrEmpty(title_, "JavaDoc"))
                .tocTree(tocTreeBuilder -> tocTreeBuilder
                        .setMaxDepth(2)
                        .ifTrue(packages_.length > 0, () -> {
                            tocTreeBuilder.paragraph(pb -> {
                                for (String packageName : packages_) {
                                    String path = Paths
                                            .get(packageName.replace(".", File.separator))
                                            .resolve(packageIndexFileName_ + ".rst")
                                            .toString().replace(File.separator, "/");
                                    pb.text(path)
                                            .lineBreak();
                                }
                            });
                        }))
                .build().serialize();
    }

    public String getPackageIndexFileName() {
        return packageIndexFileName_;
    }

    public PackagesIndexRstGenerator setPackageIndexFileName(String packageIndexFileName) {
        packageIndexFileName_ = StringUtils.requireNotNullOrEmpty(packageIndexFileName);
        return this;
    }

    public String getTitle() {
        return title_;
    }

    public PackagesIndexRstGenerator setTitle(String title) {
        title_ = title;
        return this;
    }

    public String[] getPackages() {
        return packages_;
    }

    public PackagesIndexRstGenerator setPackages(String[] packages) {
        packages_ = Objects.requireNonNull(packages);
        return this;
    }
}
