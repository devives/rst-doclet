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
import com.devives.rstdoclet.RstConfiguration;
import com.devives.rstdoclet.html2rst.HtmlUtils;
import com.devives.rstdoclet.html2rst.jdkloans.LinkInfoImpl;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

public class JavaMethodBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends JavaExecutableBuilderAbst<PARENT, JavaMethodBuilder<PARENT>> {

    private final ExecutableElement executableElement_;

    public JavaMethodBuilder(ExecutableElement executableElement, RstConfiguration configuration) {
        super(new Directive.Type("java:method"), executableElement, configuration);
        executableElement_ = executableElement;
    }

    @Override
    protected void fillArguments(List<String> argumentList) {
        argumentList.add(formatAnnotations(executableElement_));
        argumentList.add(formatModifiers(executableElement_));
        argumentList.add(formatMethodTypeParameters(executableElement_));
        argumentList.add(formatReturnType(executableElement_));
        argumentList.add(formatNameWithParameters(executableElement_, true));
        argumentList.add(formatThrows(executableElement_));
    }

    public String formatMethodTypeParameters(ExecutableElement executableElement) {
        LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_.getHtmlConfiguration(), LinkInfoImpl.Kind.MEMBER_TYPE_PARAMS, executableElement);
        linkInfo.linkToSelf = false;
        String content = docContext_.getTypeParameterLinks(linkInfo).toString();
        String className =  HtmlUtils.extractATextOrElse(content, () -> content);
        String result = HtmlUtils.unescapeLtRtAmpBSlash(className);
        result = collapseNamespaces(result);
        return result;
    }

    public String formatReturnType(ExecutableElement executableElement) {
        LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_.getHtmlConfiguration(), LinkInfoImpl.Kind.RETURN_TYPE, executableElement.getReturnType());
        String content = docContext_.getLink(linkInfo).toString();
        String className =  HtmlUtils.extractATextOrElse(content, () -> content);
        String result = HtmlUtils.unescapeLtRtAmpBSlash(className);
        result = collapseNamespaces(result);
        return reformatCommas(result);
    }
}
