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

import com.devives.html2rst.HtmlUtils;
import com.devives.rst.document.inline.InlineElement;
import com.devives.rstdoclet.rst.builder.JavaMemberRefBuilder;
import com.devives.rstdoclet.rst.builder.JavaTypeRefBuilder;
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.LinkInfoImpl;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class ClassHtmlWriterImpl extends jdk.javadoc.internal.doclets.formats.html.ClassWriterImpl {

    public ClassHtmlWriterImpl(HtmlConfiguration configuration, TypeElement typeElement, ClassTree classTree) {
        super(configuration, typeElement, classTree);
    }

    @Override
    public Content getLink(LinkInfoImpl linkInfo) {
        Content content = super.getLink(linkInfo);
        if (content instanceof RstContent) {
            return content;
        } else if (linkInfo.executableElement != null) {
            InlineElement inlineElement = new JavaMemberRefBuilder<>(linkInfo.executableElement, configuration.utils).build();
            return  new RstContent(inlineElement, content);
        } else if (linkInfo.typeElement != null) {
            String text = null;
            if (linkInfo.label != null) {
                text = HtmlUtils.removeCodeTags((linkInfo.label != null) ? linkInfo.label.toString() : "");
            }
            InlineElement inlineElement = new JavaTypeRefBuilder<>(linkInfo.typeElement).setText(text).build();
            return  new RstContent(inlineElement, content);
        }
        return content;
    }

    @Override
    public Content getDocLink(LinkInfoImpl.Kind context, TypeElement typeElement, Element element, Content label, boolean strong, boolean isProperty) {
        Content content = super.getDocLink(context, typeElement, element, label, strong, isProperty);
        String text = HtmlUtils.removeCodeTags((label != null) ? label.toString() : "");
        InlineElement inlineElement = new JavaMemberRefBuilder<>(element, configuration.utils).setText(text).build();
        return new RstContent(inlineElement, content);
    }

    @Override
    public Content getCrossClassLink(TypeElement classElement, String refMemName, Content label, boolean strong, boolean code) {
        Content content = super.getCrossClassLink(classElement, refMemName, label, strong, code);
        InlineElement inlineElement = new JavaTypeRefBuilder<>(classElement).build();
        return new RstContent(inlineElement, content);
    }

}
