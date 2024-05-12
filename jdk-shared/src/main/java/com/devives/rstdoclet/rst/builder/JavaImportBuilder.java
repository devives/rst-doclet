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
import com.devives.rstdoclet.util.ElementUtils;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.TypeElement;

import static com.devives.rst.util.Constants.SPACE;

public class JavaImportBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends DirectiveBuilderAbst<PARENT, Directive, JavaImportBuilder<PARENT>> {

    private final TypeElement classDoc_;
    private final Utils utils_;

    public JavaImportBuilder(TypeElement classDoc, Utils utils) {
        super(new Directive.Type("java:import"));
        classDoc_ = classDoc;
        utils_ = utils;
    }

    @Override
    protected void onBuild(Directive directive) {
        String pkgName = utils_.getPackageName(ElementUtils.getPackageOfType(classDoc_));
        String name = utils_.getSimpleName(classDoc_);
        directive.getArguments().addAll(pkgName, SPACE, name);
    }
}
