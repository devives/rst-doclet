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
import com.devives.rstdoclet.html.LinkInfoKind;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import jdk.javadoc.internal.doclets.toolkit.Content;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

public class JavaMethodBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends JavaExecutableBuilderAbst<PARENT, JavaMethodBuilder<PARENT>> {

    private final ExecutableElement executableElement_;

    public JavaMethodBuilder(ExecutableElement executableElement, RstGeneratorContext docContext) {
        super(new Directive.Type("java:method"), executableElement, docContext);
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
        Content content = docContext_.getHtmlDocletWriter().getTypeParameterLinks(
                docContext_.getRstConfiguration().getHtmlConfiguration(), executableElement);
        String className = HtmlUtils.removeATags(content.toString());
        String result = HtmlUtils.unescapeLtRtAmpBSlash(className);
        result = collapseNamespaces(result);
        return result;
    }

    public String formatReturnType(ExecutableElement executableElement) {
        Content content = docContext_.getHtmlDocletWriter().getLink(
                docContext_.getRstConfiguration().getHtmlConfiguration(),
                LinkInfoKind.RETURN_TYPE,
                executableElement.getReturnType(),
                true);
        String className = HtmlUtils.removeATags(content.toString());
        String result = HtmlUtils.unescapeLtRtAmpBSlash(className);
        result = collapseNamespaces(result);
        return reformatCommas(result);
    }
}
