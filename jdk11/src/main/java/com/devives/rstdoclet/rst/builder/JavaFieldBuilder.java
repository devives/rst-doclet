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

import com.devives.rst.builder.BlockQuoteBuilder;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.RstConfiguration;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.html2rst.jdkloans.HtmlDocletWriter;
import com.sun.source.doctree.DocTree;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import java.util.Arrays;
import java.util.List;

public class JavaFieldBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends JavaMemberBuilderAbst<PARENT, JavaFieldBuilder<PARENT>> {

    private final VariableElement variableElement_;

    public JavaFieldBuilder(VariableElement variableElement, HtmlDocletWriter docContext) {
        super(new Directive.Type("java:field"), variableElement, docContext);
        this.variableElement_ = variableElement;
    }

    @Override
    protected void fillArguments(List<String> argumentList) {
        argumentList.add(formatAnnotations(variableElement_));
        argumentList.add(formatModifiers(variableElement_));
        argumentList.add(formatReturnType(variableElement_));
        argumentList.add(variableElement_.getSimpleName().toString());
    }

    @Override
    protected void fillElements(BlockQuoteBuilder<?> bodyBuilder) {
        super.fillElements(bodyBuilder);
        List<? extends DocTree> tags = docContext_.configuration.utils.getBlockTags(variableElement_);
        if (tags.size() > 0) {
            new TagUtils(docContext_).appendTags(bodyBuilder, variableElement_, Arrays.asList(TagUtils.TagName.Author, TagUtils.TagName.See));
        }
    }

    protected String formatReturnType(Element fieldDoc) {
        String result = reformatCommas(fieldDoc.asType().toString());
        result = collapseNamespaces(result);
        return result;
    }

}
