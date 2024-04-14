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

import com.devives.html2rst.HtmlUtils;
import com.devives.rst.builder.BlockQuoteBuilder;
import com.devives.rst.builder.BlockQuoteBuilderImpl;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.builder.directive.DirectiveBuilderAbst;
import com.devives.rst.document.directive.Directive;
import com.devives.rst.document.inline.Text;
import com.devives.rstdoclet.RstConfiguration;
import com.devives.rstdoclet.html2rst.CommentBuilder;
import com.devives.rstdoclet.html2rst.ImportsCollector;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.html2rst.jdkloans.HtmlDocletWriter;
import com.devives.rstdoclet.html2rst.jdkloans.LinkInfoImpl;
import com.devives.sphinx.rst.document.IncludeDocument;
import com.sun.source.doctree.DocTree;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.devives.rst.util.Constants.SPACE;

public class JavaTypeBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>> extends DirectiveBuilderAbst<PARENT, Directive, JavaTypeBuilder<PARENT>> {
    private final TypeElement classDoc_;
    private final RstConfiguration configuration_;
    private final HtmlDocletWriter docContext_;
    private final Map<String, TypeElement> imports_;

    public JavaTypeBuilder(TypeElement classDoc, RstConfiguration configuration) {
        super(new Directive.Type("java:type"));
        classDoc_ = Objects.requireNonNull(classDoc);
        configuration_ = Objects.requireNonNull(configuration);
        docContext_ = new HtmlDocletWriter(classDoc_, configuration_.getHtmlConfiguration());
        imports_ = new ImportsCollector(configuration.utils).collect(classDoc_, true).getImportsMap();
    }

    public JavaTypeBuilder<PARENT> fillImports(Map<String, TypeElement> imports) {
        imports.putAll(imports_);
        return this;
    }

    @Override
    protected void onBuild(Directive directive) {
        directive.setArguments(
                Stream.of(formatModifiers(classDoc_),
                                formatQualifier(classDoc_),
                                formatTypeName(classDoc_),
                                formatExtends(classDoc_),
                                formatImplements(classDoc_)
                        )
                        .filter(s -> !s.isEmpty())
                        .map(s -> s.concat(SPACE))
                        .map(Text::new)
                        .collect(Collectors.toList()));
        if (classDoc_.getNestingKind() == NestingKind.MEMBER) {
            directive.getOptions().put("outertype", configuration_.utils.getSimpleName(configuration_.utils.getEnclosingTypeElement(classDoc_)));
        }
        List<? extends DocTree> tags = configuration_.utils.getBlockTags(classDoc_);
        if (!tags.isEmpty()) {
            BlockQuoteBuilder<?> bodyBuilder = new BlockQuoteBuilderImpl<>();
            new TagUtils(configuration_, docContext_).appendTags(bodyBuilder, classDoc_, Arrays.asList(TagUtils.TagName.Since, TagUtils.TagName.Version, TagUtils.TagName.Deprecated));
            bodyBuilder.build().getChildren().forEach(directive.getChildren()::add);
        }
        List<? extends DocTree> inlineTags = configuration_.utils.getBody(classDoc_);
        if (!inlineTags.isEmpty()) {
            IncludeDocument includeDocument = new IncludeDocument();
            includeDocument.getChildren().add(new CommentBuilder(
                    classDoc_,
                    classDoc_,
                    configuration_).build());
            directive.getChildren().add(includeDocument);
        }
        if (!tags.isEmpty()) {
            BlockQuoteBuilder<?> bodyBuilder = new BlockQuoteBuilderImpl<>();
            new TagUtils(configuration_, docContext_).appendTags(bodyBuilder, classDoc_, Arrays.asList(TagUtils.TagName.Author, TagUtils.TagName.See));
            bodyBuilder.build().getChildren().forEach(directive.getChildren()::add);
        }
    }

    private String formatTypeName(TypeElement classDoc) {
        String result = classDoc_.getSimpleName().toString();
        LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_.getHtmlConfiguration(), LinkInfoImpl.Kind.CLASS_SIGNATURE, classDoc);
        linkInfo.linkToSelf = false;
        String content = docContext_.getTypeParameterLinks(linkInfo).toString();
        String className =  HtmlUtils.removeATags(content);
        result += HtmlUtils.unescapeLtRtAmpBSlash(className);
        result = collapseNamespaces(result);
        return result;
    }

    private String formatExtends(TypeElement classDoc) {
        String result = "";
        if (classDoc.getKind() != ElementKind.ENUM
                && classDoc.getKind() != ElementKind.INTERFACE
                && classDoc.getKind() != ElementKind.ANNOTATION_TYPE
                && classDoc.getSuperclass() != null
                && !classDoc.getSuperclass().toString().equals(Object.class.getCanonicalName())) {
            result += "extends ";
            LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_.getHtmlConfiguration(), LinkInfoImpl.Kind.CLASS_SIGNATURE_PARENT_NAME, classDoc.getSuperclass());
            linkInfo.linkToSelf = false;
            String content = docContext_.getLink(linkInfo).toString();
            String className = HtmlUtils.extractATextOrElse(content, () -> content);
            result += HtmlUtils.unescapeLtRtAmpBSlash(className);
            result = collapseNamespaces(result);
        }
        return result;
    }

    private String formatImplements(TypeElement classDoc) {
        String result = "";
        if (classDoc.getKind() != ElementKind.ANNOTATION_TYPE) {
            if (!classDoc.getInterfaces().isEmpty()) {
                result += classDoc.getKind().isInterface() ? "extends " : "implements ";
                result += classDoc.getInterfaces().stream()
                        .map(interfaceDoc -> {
                            LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_.getHtmlConfiguration(), LinkInfoImpl.Kind.IMPLEMENTED_INTERFACES, interfaceDoc);
                            String content = docContext_.getLink(linkInfo).toString();
                            String className = HtmlUtils.extractATextOrElse(content, () -> content);
                            return collapseNamespaces(HtmlUtils.unescapeLtRtAmpBSlash(className));

                        })
                        .collect(Collectors.joining(", "));
            }
        }
        return result;
    }

    private String formatModifiers(TypeElement classDoc) {
        if (classDoc.getKind() == ElementKind.ENUM
                || classDoc.getKind() == ElementKind.INTERFACE
                || classDoc.getKind() == ElementKind.ANNOTATION_TYPE) {
            return classDoc.getModifiers().stream()
                    .filter(m -> !(Modifier.ABSTRACT == m || Modifier.STATIC == m || Modifier.FINAL == m))
                    .map(Modifier::toString)
                    .collect(Collectors.joining(" "))
                    .trim();
        } else {
            return classDoc.getModifiers().stream()
                    .map(Modifier::toString)
                    .collect(Collectors.joining(" "));
        }
    }

    private String formatQualifier(TypeElement classDoc) {
        switch (classDoc.getKind()) {
            case PACKAGE:
                return "package";
            case ENUM:
                return "enum";
            case CLASS:
                return "class";
            case ANNOTATION_TYPE:
                return "@interface";
            case INTERFACE:
                return "interface";
            default:
                throw new RuntimeException("Not implemented: qualifier for " + classDoc.getKind().name());
        }
    }

    protected String collapseNamespaces(String content) {
        String[] values = new String[]{content};
        imports_.forEach((name, classDoc) -> {
            values[0] = values[0].replace(name, classDoc.getSimpleName().toString());
        });
        return values[0];
    }

}
