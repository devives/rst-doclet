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
import com.devives.rst.builder.BlockQuoteBuilderImpl;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.builder.directive.DirectiveBuilderAbst;
import com.devives.rst.document.directive.Directive;
import com.devives.rst.document.inline.Text;
import com.devives.rstdoclet.html2rst.CommentBuilder;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.devives.rstdoclet.util.ImportsCollectorImpl;
import com.devives.sphinx.rst.document.IncludeDocument;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.ProgramElementDoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.devives.rst.util.Constants.SPACE;

public abstract class JavaMemberBuilderAbst<
        PARENT extends RstNodeBuilder<?, ?, ?, ?>,
        SELF extends JavaMemberBuilderAbst<PARENT, SELF>>
        extends DirectiveBuilderAbst<PARENT, Directive, SELF> {

    private final MemberDoc memberDoc_;
    protected final Map<String, ClassDoc> imports_;
    protected final RstGeneratorContext docContext_;

    public JavaMemberBuilderAbst(Directive.Type type, MemberDoc memberDoc, RstGeneratorContext docContext) {
        super(type);
        this.memberDoc_ = memberDoc;
        this.docContext_ = docContext;
        this.imports_ = new ImportsCollectorImpl().collect(memberDoc).getImportsMap();
    }

    public JavaMemberBuilderAbst<PARENT, SELF> fillImports(Map<String, ClassDoc> imports) {
        imports.putAll(imports_);
        return this;
    }

    @Override
    protected void onBuild(Directive directive) {
        List<String> argumentList = new ArrayList<>();
        fillArguments(argumentList);
        directive.setArguments(argumentList
                .stream()
                .filter(s -> !s.isEmpty())
                .map(s -> s.concat(SPACE))
                .map(Text::new)
                .collect(Collectors.toList()));

        directive.getOptions().put("outertype", memberDoc_.containingClass().typeName()
        );

        BlockQuoteBuilder<?> bodyBuilder = new BlockQuoteBuilderImpl<>();
        fillElements(bodyBuilder);
        bodyBuilder.build().getChildren().forEach(directive.getChildren()::add);
    }

    protected abstract void fillArguments(List<String> argumentList);

    protected void fillElements(BlockQuoteBuilder<?> bodyBuilder) {
        bodyBuilder
                .ifTrue(memberDoc_.tags().length > 0, shiftBuilder -> {
                    new TagUtils(docContext_).appendTags(bodyBuilder, memberDoc_, Arrays.asList(TagUtils.TagName.Since, TagUtils.TagName.Version, TagUtils.TagName.Deprecated));
                })
                .ifTrue(memberDoc_.inlineTags().length > 0, quoteBuilder -> {
                    IncludeDocument includeDocument = new IncludeDocument();
                    includeDocument.getChildren().add(
                            new CommentBuilder(
                                    docContext_,
                                    memberDoc_).build());
                    quoteBuilder.addChild(includeDocument);
                });
    }

    protected String collapseNamespaces(String content) {
        String[] values = new String[]{content};
        imports_.forEach((name, classDoc) -> {
            values[0] = values[0].replace(name, classDoc.typeName());
        });
        return values[0];
    }

    protected static String formatAnnotations(ProgramElementDoc programElementDoc) {
        String result = "";
        if (programElementDoc.annotations().length > 0) {
            result = Arrays.stream(programElementDoc.annotations())
                    .map(a -> "@" + a.annotationType().typeName())
                    .collect(Collectors.joining(" "));
        }
        return result;
    }

    protected static String formatModifiers(ProgramElementDoc programElementDoc) {
        if (programElementDoc.containingClass().isInterface()) {
            return programElementDoc.modifiers().replace("public", "").trim();
        } else {
            return programElementDoc.modifiers();
        }
    }


}
