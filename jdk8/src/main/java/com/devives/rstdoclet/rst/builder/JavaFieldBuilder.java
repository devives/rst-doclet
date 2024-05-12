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

import com.devives.rst.builder.BlockQuoteBuilder;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.sun.javadoc.FieldDoc;

import java.util.Arrays;
import java.util.List;

public class JavaFieldBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>>
        extends JavaMemberBuilderAbst<PARENT, JavaFieldBuilder<PARENT>> {

    private final FieldDoc fieldDoc_;

    public JavaFieldBuilder(FieldDoc fieldDoc, RstGeneratorContext docContext) {
        super(new Directive.Type("java:field"), fieldDoc, docContext);
        this.fieldDoc_ = fieldDoc;
    }

    @Override
    protected void fillArguments(List<String> argumentList) {
        argumentList.add(formatAnnotations(fieldDoc_));
        argumentList.add(formatModifiers(fieldDoc_));
        argumentList.add(formatReturnType(fieldDoc_));
        argumentList.add(fieldDoc_.name());
    }

    @Override
    protected void fillElements(BlockQuoteBuilder<?> bodyBuilder) {
        super.fillElements(bodyBuilder);
        if (fieldDoc_.tags().length > 0) {
            new TagUtils(docContext_).appendTags(bodyBuilder, fieldDoc_, Arrays.asList(TagUtils.TagName.Author, TagUtils.TagName.See));
        }
    }

    public String formatReturnType(FieldDoc fieldDoc) {
        String result = fieldDoc.type().toString();
        result = collapseNamespaces(result);
        return result;
    }
}
