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

import javax.lang.model.element.PackageElement;
import java.util.Objects;

public class JavaPackageRefBuilder<
        PARENT extends RstNodeBuilder<?, ?, ?, ?>,
        SELF extends JavaPackageRefBuilder<PARENT, SELF>>
        extends JavaRoleBuilderAbst<PARENT, SELF> {

    private final PackageElement packageElement_;

    public JavaPackageRefBuilder(PackageElement packageElement) {
        this.packageElement_ = Objects.requireNonNull(packageElement);
    }

    @Override
    protected String formatName() {
        return "java:ref";
    }

    @Override
    protected String formatTarget() {
        return packageElement_.getQualifiedName().toString();
    }

    @Override
    protected String formatText() {
        return text_ != null ? text_ : packageElement_.getQualifiedName().toString();
    }
}
