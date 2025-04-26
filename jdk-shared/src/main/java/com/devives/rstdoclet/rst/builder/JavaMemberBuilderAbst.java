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
import com.devives.rstdoclet.RstDocletComponentFactory;
import com.devives.rstdoclet.html2rst.CommentBuilder;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.devives.sphinx.rst.document.IncludeDocument;
import com.sun.source.doctree.DocTree;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.devives.rst.util.Constants.SPACE;

public abstract class JavaMemberBuilderAbst<
        PARENT extends RstNodeBuilder<?, ?, ?, ?>,
        SELF extends JavaMemberBuilderAbst<PARENT, SELF>>
        extends DirectiveBuilderAbst<PARENT, Directive, SELF> {

    private final Element memberDoc_;
    protected final Map<String, TypeElement> imports_;
    protected final RstGeneratorContext docContext_;
    protected final Utils utils_;

    public JavaMemberBuilderAbst(Directive.Type type, Element element, RstGeneratorContext docContext) {
        super(type);
        this.memberDoc_ = Objects.requireNonNull(element);
        this.docContext_ = Objects.requireNonNull(docContext);
        this.utils_ = docContext_.getRstConfiguration().getHtmlConfiguration().utils;
        this.imports_ = RstDocletComponentFactory.getInstance().newImportsCollector(utils_).collect(element).getImportsMap();
    }

    public JavaMemberBuilderAbst<PARENT, SELF> fillImports(Map<String, TypeElement> imports) {
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

        directive.getOptions().put("outertype", utils_.getSimpleName(memberDoc_.getEnclosingElement()));

        BlockQuoteBuilder<?> bodyBuilder = new BlockQuoteBuilderImpl<>();
        fillElements(bodyBuilder);
        bodyBuilder.build().getChildren().forEach(directive.getChildren()::add);
    }

    protected abstract void fillArguments(List<String> argumentList);

    protected void fillElements(BlockQuoteBuilder<?> bodyBuilder) {
        List<? extends DocTree> tags = utils_.getBody(memberDoc_);
        List<? extends DocTree> inlineTags = utils_.getBlockTags(memberDoc_).stream()
                .filter(tag -> tag.getKind() == DocTree.Kind.SINCE || tag.getKind() == DocTree.Kind.DEPRECATED)
                .collect(Collectors.toList());
        bodyBuilder
                .ifTrue(inlineTags.size() > 0, shiftBuilder -> {
                    new TagUtils(docContext_).appendTags(bodyBuilder, memberDoc_, Arrays.asList(TagUtils.TagName.Since, TagUtils.TagName.Deprecated));
                })
                .ifTrue(tags.size() > 0, quoteBuilder -> {
                    IncludeDocument includeDocument = new IncludeDocument();
                    includeDocument.getChildren().add(new CommentBuilder(
                            memberDoc_,
                            docContext_).build());
                    quoteBuilder.addChild(includeDocument);
                });
    }

    protected String collapseNamespaces(String content) {
        String[] values = new String[]{content};
        imports_.forEach((name, classDoc) -> {
            values[0] = values[0].replace(name, utils_.getSimpleName(classDoc));
        });
        return values[0];
    }

    protected String formatAnnotations(Element programElementDoc) {
        String result = "";
        if (programElementDoc.getAnnotationMirrors().size() > 0) {
            result = programElementDoc.getAnnotationMirrors().stream()
                    .map(a -> "@" + a.getAnnotationType().asElement().getSimpleName().toString())
                    .collect(Collectors.joining(" "));
        }
        return result;
    }

    protected String formatModifiers(Element programElementDoc) {
        Set<Modifier> modifiers;
        if (programElementDoc.getEnclosingElement().getKind().isInterface()) {
            modifiers = programElementDoc.getModifiers().stream().filter(m -> m != Modifier.PUBLIC && m != Modifier.ABSTRACT).collect(Collectors.toSet());
        } else {
            modifiers = programElementDoc.getModifiers();
        }
        return modifiers.stream().map(Modifier::toString).collect(Collectors.joining(" "));
    }


    private static final Pattern COMMA_PATTERN = Pattern.compile(",\\S");

    protected static String reformatCommas(String text) {
        text = text.replace("<wbr>", " ");
        return COMMA_PATTERN.matcher(text).replaceAll((m) -> m.group(0).replaceAll(",", ", "));
    }


}
