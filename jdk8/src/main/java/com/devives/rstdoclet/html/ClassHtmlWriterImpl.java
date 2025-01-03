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
import com.devives.rst.util.StringUtils;
import com.devives.rstdoclet.rst.builder.JavaMemberRefBuilder;
import com.devives.rstdoclet.rst.builder.JavaTypeRefBuilder;
import com.devives.sphinx.rst.document.JavaRef;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.tools.doclets.formats.html.ClassWriterImpl;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.LinkInfoImpl;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;

import java.io.IOException;

public class ClassHtmlWriterImpl extends ClassWriterImpl {

    public ClassHtmlWriterImpl(ConfigurationImpl configuration, ClassDoc classDoc,
                               ClassDoc prevClass, ClassDoc nextClass, ClassTree classTree) throws IOException {
        super(configuration, classDoc, prevClass, nextClass, classTree);
    }

    @Override
    public Content getLink(LinkInfoImpl linkInfo) {
        Content content = super.getLink(linkInfo);
        if (content instanceof RstContent) {
            return content;
        } else if (linkInfo.executableMemberDoc != null) {
            InlineElement inlineElement = new JavaMemberRefBuilder<>(linkInfo.executableMemberDoc).build();
            return new RstContent(inlineElement, content);
        } else if (linkInfo.classDoc != null) {
            String text = null;
            if (linkInfo.label != null) {
                text = HtmlUtils.removeCodeTags((linkInfo.label != null) ? linkInfo.label.toString() : "");
            }
            InlineElement inlineElement = new JavaTypeRefBuilder<>(linkInfo.classDoc).setText(text).build();
            return new RstContent(inlineElement, content);
        }
        return content;
    }

    @Override
    public Content getDocLink(LinkInfoImpl.Kind context, ClassDoc classDoc, MemberDoc doc,
                              Content label, boolean strong, boolean isProperty) {
        Content content = super.getDocLink(context, classDoc, doc, label, strong, isProperty);
        String text = HtmlUtils.removeCodeTags((label != null) ? label.toString() : "");
        InlineElement inlineElement = new JavaMemberRefBuilder<>(doc).setText(text).build();
        return new RstContent(inlineElement, content);
    }

    @Override
    public Content getDocLink(LinkInfoImpl.Kind context, ClassDoc classDoc, MemberDoc doc,
                              Content label){
        return super.getDocLink(context, classDoc, doc, label);
    }

    @Override
    public Content getCrossClassLink(String qualifiedClassName, String refMemName,
                                     Content label, boolean strong, String style,
                                     boolean code) {
        Content content = super.getCrossClassLink(qualifiedClassName, refMemName, label, strong, style, code);
        if (StringUtils.notNullOrEmpty(qualifiedClassName)) {
            String text = HtmlUtils.removeCodeTags((label != null) ? label.toString() : "");
            InlineElement inlineElement = new JavaRef(qualifiedClassName, StringUtils.findFirstNotNullOrEmpty(text, qualifiedClassName));
            return new RstContent(inlineElement, content);
        } else {
            return null;
        }
    }

}
