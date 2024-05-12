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

public class HtmlAdaptersFactoryImpl extends HtmlAdaptersFactory {

    @Override
    public HtmlDocletWriter newHtmlDocletWriter(HtmlDocletWriter delegateWriter) {
        return new com.devives.rstdoclet.html.HtmlDocletWriter(delegateWriter);
    }

    @Override
    public HtmlDocletWriter newHtmlClassWriter(HtmlConfiguration configuration, TypeElement typeElement, ClassTree classTree) {
        return new ClassHtmlWriterImpl(configuration, typeElement, classTree);
    }

    @Override
    public HtmlDocletWriter newHtmlPackageWriter(HtmlConfiguration configuration, PackageElement packageElement) {
        return new PackageHtmlWriterImpl(configuration, packageElement);
    }

    @Override
    public HtmlDocletWriterAdapter newHtmlDocletWriterAdapter(HtmlDocletWriter docletWriter) {
        return new HtmlDocletWriterAdapterImpl(docletWriter);
    }
}
