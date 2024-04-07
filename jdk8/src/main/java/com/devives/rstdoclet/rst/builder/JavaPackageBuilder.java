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
package com.devives.rstdoclet.rst.builder;

import com.devives.rst.Rst;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.builder.directive.DirectiveBuilderAbst;
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.ConfigurationImpl;
import com.sun.javadoc.PackageDoc;

import java.util.Collections;
import java.util.Objects;

public class JavaPackageBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends DirectiveBuilderAbst<PARENT, Directive, JavaPackageBuilder<PARENT>> {

    private final PackageDoc packageDoc_;
    private final ConfigurationImpl configuration_;

    public JavaPackageBuilder(PackageDoc packageDoc, ConfigurationImpl configuration) {
        super(new Directive.Type("java:package"));
        packageDoc_ = Objects.requireNonNull(packageDoc);
        configuration_ = Objects.requireNonNull(configuration);
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
