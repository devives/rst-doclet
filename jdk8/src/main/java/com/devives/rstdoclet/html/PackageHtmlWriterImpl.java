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
import com.devives.sphinx.rst.document.JavaRef;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.LinkInfoImpl;
import com.sun.tools.doclets.formats.html.PackageWriterImpl;
import com.sun.tools.doclets.internal.toolkit.Content;

import java.io.IOException;

public class PackageHtmlWriterImpl extends PackageWriterImpl {

    public PackageHtmlWriterImpl(ConfigurationImpl configuration, PackageDoc packageDoc,
                                 PackageDoc packageDoc1, PackageDoc packageDoc2) throws IOException {
        super(configuration, packageDoc, packageDoc1, packageDoc2);
    }

    @Override
    public Content getLink(LinkInfoImpl linkInfo) {
        Content content = super.getLink(linkInfo);
        if (content instanceof RstContent) {
            return content;
        } else if (linkInfo.executableMemberDoc != null) {
            InlineElement inlineElement = new JavaMemberRefBuilder<>(linkInfo.executableMemberDoc).build();
            return  new RstContent(inlineElement, content);
        } else if (linkInfo.classDoc != null) {
            InlineElement inlineElement = new JavaTypeRefBuilder<>(linkInfo.classDoc).build();
            return  new RstContent(inlineElement, content);
        }
        return content;
    }

    @Override
    public Content getDocLink(LinkInfoImpl.Kind context, ClassDoc classDoc, MemberDoc doc,
                              Content label, boolean strong, boolean isProperty) {
        Content content = super.getDocLink(context, classDoc, doc, label, strong, isProperty);
        String text = HtmlUtils.removeCodeTags(label.toString());
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
        InlineElement inlineElement = new JavaRef(qualifiedClassName, label.toString());
        return new RstContent(inlineElement, content);
    }

}
