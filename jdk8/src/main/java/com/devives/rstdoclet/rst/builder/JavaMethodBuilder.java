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
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.ConfigurationImpl;
import com.devives.html2rst.HtmlUtils;
import com.devives.rstdoclet.html2rst.jdkloans.LinkInfoImpl;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Content;

import java.util.List;

public class JavaMethodBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends JavaExecutableBuilderAbst<PARENT, JavaMethodBuilder<PARENT>> {

    private final MethodDoc methodDoc_;

    public JavaMethodBuilder(MethodDoc methodDoc, ConfigurationImpl configuration) {
        super(new Directive.Type("java:method"), methodDoc, configuration);
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
        LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_, LinkInfoImpl.Kind.MEMBER_TYPE_PARAMS, methodDoc);
        linkInfo.linkToSelf = false;
        Content parameterLinks = docContext_.getTypeParameterLinks(linkInfo);
        String result = HtmlUtils.unescapeLtRtAmpBSlash(parameterLinks.toString());
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
            LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_, LinkInfoImpl.Kind.RETURN_TYPE, methodDoc.returnType());
            Content parameterLinks = docContext_.getLink(linkInfo);
            result = HtmlUtils.unescapeLtRtAmpBSlash(parameterLinks.toString());
        } else {
            result = returnType.qualifiedTypeName();
        }
        result = collapseNamespaces(result) + returnType.dimension();
        return result;
    }
}
