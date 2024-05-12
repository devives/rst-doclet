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
package com.devives.rstdoclet.html2rst;

import com.devives.html2rst.HtmlUtils;
import com.devives.rst.builder.BodyBuilders;
import com.devives.rst.builder.RstElementBuilder;
import com.devives.rst.document.Paragraph;
import com.devives.rst.document.inline.InlineElement;
import com.devives.rst.util.StringUtils;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.devives.sphinx.rst.Rst4Sphinx;
import com.devives.sphinx.rst.document.directive.Directives;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.SeeTree;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.Element;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TagUtils {

    private final Utils utils_;
    private final RstGeneratorContext docContext_;

    public TagUtils(RstGeneratorContext docContext) {
        docContext_ = Objects.requireNonNull(docContext);
        utils_ = docContext.getRstConfiguration().getHtmlConfiguration().utils;
    }

    public enum TagName {
        Since,
        Version,
        Deprecated,
        Author,
        See
    }

    /**
     * @see <a href="https://www.tutorialspoint.com/java/java_documentation.htm">The javadoc Tags</a>
     */
    public RstElementBuilder<?, ?, ?> appendTags(BodyBuilders<?, ?, ?, ?> builder, Element element, Collection<TagName> tagNames) {
        // Tag[] valueTags = doc.tags("@value");
        // Tag[] serialTags = doc.tags("@serial");
        tagNames.forEach(tagName -> {
            switch (tagName) {
                case Author:
                    appendAuthorTags(builder, element);
                    break;
                case Since:
                    appendSinceTags(builder, element);
                    break;
                case Version:
                    appendVersionTags(builder, element);
                    break;
                case Deprecated:
                    appendDeprecatedTags(builder, element);
                    break;
                case See:
                    appendSeeTags(builder, element);
                    break;
            }
        });
        return builder;
    }

    public RstElementBuilder<?, ?, ?> appendAuthorTags(BodyBuilders<?, ?, ?, ?> builder, Element element) {
        List<? extends DocTree> tags = utils_.getBlockTags(element, DocTree.Kind.AUTHOR);
        return builder.ifTrue(tags.size() > 0, () -> {
            for (DocTree tag : tags) {
                String text = new CommentBuilder(tag, element, docContext_).build().serialize();
                if (StringUtils.notNullOrEmpty(text)) {
                    builder.directive(Directives.SectionAuthor, text);
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendSinceTags(BodyBuilders<?, ?, ?, ?> builder, Element element) {
        List<? extends DocTree> tags = utils_.getBlockTags(element, DocTree.Kind.SINCE);
        return builder.ifTrue(tags.size() > 0, () -> {
            for (DocTree tag : tags) {
                String text = new CommentBuilder(tag, element, docContext_).build().serialize();
                if (StringUtils.notNullOrEmpty(text)) {
                    builder.directive(Directives.VersionAdded, text);
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendVersionTags(BodyBuilders<?, ?, ?, ?> builder, Element element) {
        List<? extends DocTree> tags = utils_.getBlockTags(element, DocTree.Kind.VERSION);
        return builder.ifTrue(tags.size() > 0, () -> {
            for (DocTree tag : tags) {
                String text = new CommentBuilder(tag, element, docContext_).build().serialize();
                if (StringUtils.notNullOrEmpty(text)) {
                    builder.directive(Directives.VersionChanged, text);
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendDeprecatedTags(BodyBuilders<?, ?, ?, ?> builder, Element element) {
        List<? extends DocTree> tags = utils_.getBlockTags(element, DocTree.Kind.DEPRECATED);
        return builder.ifTrue(tags.size() > 0, () -> {
            for (DocTree tag : tags) {
                String text = new CommentBuilder(tag, element, docContext_).build().serialize();
                if (StringUtils.notNullOrEmpty(text)) {
                    builder.directive(Directives.Deprecated, text);
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendSeeTags(BodyBuilders<?, ?, ?, ?> builder, Element element) {
        List<? extends DocTree> tags = utils_.getBlockTags(element, DocTree.Kind.SEE);
        return builder.ifTrue(tags.size() > 0, () -> {
            builder.directive(Directives.SeeAlso, seeAlsoBuilder -> {
                seeAlsoBuilder.lineBlock(lineBlockBuilder -> {
                    for (DocTree tag : tags) {
                        lineBlockBuilder.item(ib -> ib.addChild(seeTagToJavaRef(element, (SeeTree) tag)));
                    }
                });
            });
        });
    }

    public InlineElement seeTagToJavaRef(Element element, SeeTree see) {
        String seeText = docContext_.getHtmlDocletWriter().replaceDocRootDir(element, see);
        InlineElement result;
        if (seeText.startsWith("<")) {
            result = HtmlUtils.hrefToLink(seeText);
        } else if (seeText.startsWith("\"")) {
            result = Rst4Sphinx.elements().text("\\ " + StringUtils.dequote(seeText, '\"') + "\\ ");
        } else {
            result = new CommentBuilder(docContext_, element, see).buildBody().stream()
                    .filter(Paragraph.class::isInstance).findFirst()
                    .map(Paragraph.class::cast).orElse(Rst4Sphinx.elements().paragraph())
                    .getChildren().stream().findFirst().orElse(Rst4Sphinx.elements().text(""));
        }
        return result;
    }

}
