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
package com.devives.rstdoclet.rst.builder;

import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.builder.directive.DirectiveBuilderAbst;
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.sun.javadoc.PackageDoc;

import java.util.Objects;

public class JavaPackageBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends DirectiveBuilderAbst<PARENT, Directive, JavaPackageBuilder<PARENT>> {

    private final PackageDoc packageDoc_;
    private final RstGeneratorContext docContext_;

    public JavaPackageBuilder(PackageDoc packageDoc, RstGeneratorContext docContext) {
        super(new Directive.Type("java:package"));
        packageDoc_ = Objects.requireNonNull(packageDoc);
        docContext_ = Objects.requireNonNull(docContext);
    }

    public JavaPackageBuilder<PARENT> setNoIndex(boolean noIndex) {
        getRstElement().getOptions().put("noindex", noIndex);
        return this;
    }

    @Override
    protected void onBuild(Directive directive) {
        directive.getArguments().add(packageDoc_.name());
    }
}
