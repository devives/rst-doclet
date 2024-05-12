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
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.sun.javadoc.ConstructorDoc;

import java.util.List;

public class JavaConstructorBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends JavaExecutableBuilderAbst<PARENT, JavaConstructorBuilder<PARENT>> {
    private final ConstructorDoc constructorDoc_;

    public JavaConstructorBuilder(ConstructorDoc constructorDoc, RstGeneratorContext docContext) {
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
