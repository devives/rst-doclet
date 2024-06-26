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


import com.devives.html2rst.HtmlUtils;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.LinkInfoImpl;
import com.sun.tools.doclets.internal.toolkit.Content;

import java.util.List;

public class JavaMethodBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends JavaExecutableBuilderAbst<PARENT, JavaMethodBuilder<PARENT>> {

    private final MethodDoc methodDoc_;

    public JavaMethodBuilder(MethodDoc methodDoc, RstGeneratorContext docContext) {
        super(new Directive.Type("java:method"), methodDoc, docContext);
        methodDoc_ = methodDoc;
    }

    @Override
    protected void fillArguments(List<String> argumentList) {
        argumentList.add(formatAnnotations(methodDoc_));
        argumentList.add(formatModifiers(methodDoc_));
        argumentList.add(formatMethodTypeParameters(methodDoc_));
        argumentList.add(formatReturnType(methodDoc_));
        argumentList.add(formatNameWithParameters(methodDoc_, true));
        argumentList.add(formatThrows(methodDoc_));
    }

    public String formatMethodTypeParameters(ExecutableMemberDoc methodDoc) {
        LinkInfoImpl linkInfo = new LinkInfoImpl(docContext_.getHtmlConfiguration(), LinkInfoImpl.Kind.MEMBER_TYPE_PARAMS, methodDoc);
        linkInfo.linkToSelf = false;
        Content content = docContext_.getHtmlDocletWriter().getTypeParameterLinks(linkInfo);
        String className = HtmlUtils.removeATags(content.toString());
        String result = HtmlUtils.unescapeLtRtAmpBSlash(className);
        result = collapseNamespaces(result);
        return result;
    }

    public String formatReturnType(MethodDoc methodDoc) {
        String result;
        Type returnType = methodDoc.returnType();
        if (returnType.isPrimitive()) {
            result = returnType.qualifiedTypeName();
        } else if (returnType.asWildcardType() != null) {
            result = returnType.qualifiedTypeName();
        } else if (returnType.asTypeVariable() != null) {
            result = returnType.qualifiedTypeName();
        } else if (returnType.asParameterizedType() != null) {
            LinkInfoImpl linkInfo = new LinkInfoImpl(docContext_.getHtmlConfiguration(), LinkInfoImpl.Kind.RETURN_TYPE, methodDoc.returnType());
            Content content = docContext_.getHtmlDocletWriter().getLink(linkInfo);
            String className = HtmlUtils.removeATags(content.toString());
            result = HtmlUtils.unescapeLtRtAmpBSlash(className);
        } else {
            result = returnType.qualifiedTypeName();
        }
        result = collapseNamespaces(result) + returnType.dimension();
        return result;
    }
}
