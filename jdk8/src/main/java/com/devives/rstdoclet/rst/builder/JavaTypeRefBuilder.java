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
import com.sun.javadoc.ClassDoc;

import java.util.Objects;

public class JavaTypeRefBuilder<
        PARENT extends RstNodeBuilder<?, ?, ?, ?>,
        SELF extends JavaTypeRefBuilder<PARENT, SELF>>
        extends JavaRoleBuilderAbst<PARENT, SELF> {

    private final ClassDoc classDoc_;

    public JavaTypeRefBuilder(ClassDoc classDoc) {
        this.classDoc_ = Objects.requireNonNull(classDoc);
    }

    @Override
    protected String formatName() {
        return "java:ref";
    }

    @Override
    protected String formatTarget() {
        return classDoc_.qualifiedTypeName();
    }

    @Override
    protected String formatText() {
        return text_ != null ? text_ : classDoc_.name();
    }
}
