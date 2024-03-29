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
import com.devives.rst.builder.BlockQuoteBuilderImpl;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.builder.directive.DirectiveBuilderAbst;
import com.devives.rst.document.directive.Directive;
import com.devives.rst.document.inline.Text;
import com.devives.rstdoclet.ConfigurationImpl;
import com.devives.rstdoclet.html2rst.CommentBuilder;
import com.devives.rstdoclet.html2rst.HtmlUtils;
import com.devives.rstdoclet.html2rst.ImportsCollector;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.html2rst.jdkloans.DocContext;
import com.devives.rstdoclet.html2rst.jdkloans.LinkInfoImpl;
import com.devives.rstdoclet.rst.document.IncludeDocument;
import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.internal.toolkit.Content;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.devives.rst.util.Constants.SPACE;

public class JavaTypeBuilder<PARENT extends RstNodeBuilder<?, ?, ?, ?>> extends DirectiveBuilderAbst<PARENT, Directive, JavaTypeBuilder<PARENT>> {
    private final ClassDoc classDoc_;
    private final ConfigurationImpl configuration_;
    private final DocContext docContext_;
    private final Map<String, ClassDoc> imports_;

    public JavaTypeBuilder(ClassDoc classDoc, ConfigurationImpl configuration) {
        super(new Directive.Type("java:type"));
        classDoc_ = Objects.requireNonNull(classDoc);
        configuration_ = Objects.requireNonNull(configuration);
        docContext_ = new DocContext(classDoc_, configuration_);
        imports_ = new ImportsCollector().collect(classDoc_, true).getImportsMap();
    }

    public JavaTypeBuilder fillImports(Map<String, ClassDoc> imports) {
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
        if (classDoc_.containingClass() != null) {
            directive.getOptions().put("outertype", classDoc_.containingClass().typeName());
        }
        if (classDoc_.tags().length > 0) {
            BlockQuoteBuilder<?> bodyBuilder = new BlockQuoteBuilderImpl<>();
            TagUtils.appendTags(bodyBuilder, classDoc_, Arrays.asList(TagUtils.TagName.Since, TagUtils.TagName.Version, TagUtils.TagName.Deprecated));
            bodyBuilder.build().getChildren().forEach(directive.getChildren()::add);
        }
        if (classDoc_.inlineTags().length > 0) {
            IncludeDocument includeDocument = new IncludeDocument();
            includeDocument.getChildren().add(new CommentBuilder(
                    classDoc_,
                    classDoc_,
                    configuration_).build());
            directive.getChildren().add(includeDocument);
        }
        if (classDoc_.tags().length > 0) {
            BlockQuoteBuilder<?> bodyBuilder = new BlockQuoteBuilderImpl<>();
            TagUtils.appendTags(bodyBuilder, classDoc_, Arrays.asList(TagUtils.TagName.Author, TagUtils.TagName.See));
            bodyBuilder.build().getChildren().forEach(directive.getChildren()::add);
        }
    }

    private String formatTypeName(ClassDoc classDoc) {
        String result = classDoc_.simpleTypeName();
        LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_, LinkInfoImpl.Kind.CLASS_SIGNATURE, classDoc);
        linkInfo.linkToSelf = false;
        Content content = docContext_.getTypeParameterLinks(linkInfo);
        result += HtmlUtils.unescapeLtRtAmpBSlash(content.toString());
        return result;
    }

    private String formatExtends(ClassDoc classDoc) {
        String result = "";
        if (!classDoc.isEnum()
                && !classDoc.isInterface()
                && classDoc.superclass() != null
                && !classDoc.superclass().qualifiedTypeName().equals(Object.class.getCanonicalName())) {
            result += "extends ";
            LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_, LinkInfoImpl.Kind.CLASS_SIGNATURE_PARENT_NAME, classDoc.superclass());
            linkInfo.linkToSelf = false;
            Content content = docContext_.getLink(linkInfo);
            result += HtmlUtils.unescapeLtRtAmpBSlash(content.toString());
            result = collapseNamespaces(result);
        }
        return result;
    }

    private String formatImplements(ClassDoc classDoc) {
        String result = "";
        if (!classDoc.isAnnotationType()) {
            if (classDoc.interfaceTypes().length > 0) {
                result += classDoc.isInterface() ? "extends " : "implements ";
                result += Arrays.stream(classDoc.interfaces())
                        .map(interfaceDoc -> {
                            LinkInfoImpl linkInfo = new LinkInfoImpl(configuration_, LinkInfoImpl.Kind.IMPLEMENTED_INTERFACES, interfaceDoc);
                            Content content = docContext_.getLink(linkInfo);
                            return collapseNamespaces(HtmlUtils.unescapeLtRtAmpBSlash(content.toString()));

                        })
                        .collect(Collectors.joining(", "));
            }
        }
        return result;
    }

    private String formatModifiers(ClassDoc classDoc) {
        if (classDoc.isEnum()) {
            return classDoc.modifiers().replace("final", "").replace("static", "").trim();
        } else if (classDoc.isAnnotationType()) {
            return classDoc.modifiers().replace("interface", "@interface");
        } else {
            return classDoc.modifiers();
        }
    }

    private String formatQualifier(ClassDoc classDoc) {
        if (classDoc.isEnum()) {
            return "enum";
        } else if (classDoc.isClass()) {
            return "class";
        } else if (classDoc.isException()) {
            return "exception";
        } else if (classDoc.isError()) {
            return "exception";
        } else if (classDoc.isInterface()) {
            return ""; //interface contains in modifiers
        } else if (classDoc.isAnnotationType()) {
            return ""; //@interface contains in modifiers
        } else {
            throw new RuntimeException("Not implemented: qualifier for " + classDoc.name());
        }
    }

    protected String collapseNamespaces(String content) {
        String[] values = new String[]{content};
        imports_.forEach((name, classDoc) -> {
            values[0] = values[0].replace(name, classDoc.typeName());
        });
        return values[0];
    }


}
