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
package com.devives.rstdoclet.html;

import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.HtmlDocletWriter;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public abstract class HtmlAdaptersFactory {

    private static volatile HtmlAdaptersFactory INSTANCE;

    public static void setInstance(HtmlAdaptersFactory instance) {
        INSTANCE = instance;
    }

    public static HtmlAdaptersFactory getInstance() {
        return INSTANCE;
    }

    public abstract HtmlDocletWriter newHtmlDocletWriter(HtmlDocletWriter delegateWriter);

    public abstract HtmlDocletWriter newHtmlClassWriter(HtmlConfiguration configuration, TypeElement typeElement, ClassTree classTree);

    public abstract HtmlDocletWriter newHtmlPackageWriter(HtmlConfiguration configuration, PackageElement packageElement);

    public abstract HtmlDocletWriterAdapter newHtmlDocletWriterAdapter(HtmlDocletWriter docletWriter);

}
