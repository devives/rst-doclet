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
package com.devives.rstdoclet.html;

import com.devives.html2rst.HtmlUtils;
import com.devives.rst.document.inline.InlineElement;
import com.devives.rstdoclet.rst.builder.JavaMemberRefBuilder;
import com.devives.rstdoclet.rst.builder.JavaTypeRefBuilder;
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.HtmlLinkInfo;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class ClassHtmlWriterImpl extends jdk.javadoc.internal.doclets.formats.html.ClassWriterImpl {

    public ClassHtmlWriterImpl(HtmlConfiguration configuration, TypeElement typeElement, ClassTree classTree) {
        super(configuration, typeElement, classTree);
    }

    @Override
    public Content getLink(HtmlLinkInfo linkInfo) {
        Content content = super.getLink(linkInfo);
        if (content instanceof RstContent) {
            return content;
        } else if (linkInfo.executableElement != null) {
            InlineElement inlineElement = new JavaMemberRefBuilder<>(linkInfo.executableElement, linkInfo.utils).build();
            return  new RstContent(inlineElement, content);
        } else if (linkInfo.typeElement != null) {
            InlineElement inlineElement = new JavaTypeRefBuilder<>(linkInfo.typeElement).build();
            return  new RstContent(inlineElement, content);
        }
        return content;
    }

    @Override
    public Content getDocLink(HtmlLinkInfo.Kind context, TypeElement typeElement, Element element, Content
            label, HtmlStyle style, boolean isProperty) {
        Content content = super.getDocLink(context, typeElement, element, label, style, isProperty);
        String text = HtmlUtils.removeCodeTags((label != null) ? label.toString() : "");
        InlineElement inlineElement = new JavaMemberRefBuilder<>(element, configuration.utils).setText(text).build();
        return new RstContent(inlineElement, content);
    }

    @Override
    public Content getCrossClassLink(TypeElement classElement, String refMemName, Content label, HtmlStyle style,
                                     boolean code) {
        Content content = super.getCrossClassLink(classElement, refMemName, label, style, code);
        InlineElement inlineElement = new JavaTypeRefBuilder<>(classElement).build();
        return new RstContent(inlineElement, content);
    }

}
