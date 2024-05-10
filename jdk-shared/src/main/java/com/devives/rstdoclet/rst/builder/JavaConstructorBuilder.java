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


import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.rst.RstGeneratorContext;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

public class JavaConstructorBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends JavaExecutableBuilderAbst<PARENT, JavaConstructorBuilder<PARENT>> {
    private final ExecutableElement constructorDoc_;

    public JavaConstructorBuilder(ExecutableElement constructorDoc, RstGeneratorContext docContext) {
        super(new Directive.Type("java:constructor"), constructorDoc, docContext);
        constructorDoc_ = constructorDoc;
    }

    @Override
    protected void fillArguments(List<String> argumentList) {
        argumentList.add(formatAnnotations(constructorDoc_));
        argumentList.add(formatModifiers(constructorDoc_));
        argumentList.add(formatNameWithParameters(constructorDoc_, true));
        argumentList.add(formatThrows(constructorDoc_));
    }

}
