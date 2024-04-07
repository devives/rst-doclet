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
package com.devives.rstdoclet;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import java.util.Locale;
import java.util.function.Function;

public abstract class BaseConfiguration extends jdk.javadoc.internal.doclets.toolkit.BaseConfiguration {

    public BaseConfiguration(Doclet doclet, Locale locale, Reporter reporter) {
        super(doclet, locale, reporter);
    }

    @Override
    protected void initConfiguration(DocletEnvironment docEnv, Function<String, String> resourceKeyMapper) {
        super.initConfiguration(docEnv, resourceKeyMapper);
    }
}
