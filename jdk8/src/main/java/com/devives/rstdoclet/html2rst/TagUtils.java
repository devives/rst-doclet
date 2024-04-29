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
import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

import java.util.Collection;
import java.util.Objects;

public class TagUtils {

    private final RstGeneratorContext docContext_;

    public TagUtils(RstGeneratorContext docContext) {
        docContext_ = Objects.requireNonNull(docContext);
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
    public RstElementBuilder<?, ?, ?> appendTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc, Collection<TagName> tagNames) {
        // Tag[] valueTags = doc.tags("@value");
        // Tag[] serialTags = doc.tags("@serial");
        tagNames.forEach(tagName -> {
            switch (tagName) {
                case Author:
                    appendAuthorTags(builder, doc);
                    break;
                case Since:
                    appendSinceTags(builder, doc);
                    break;
                case Version:
                    appendVersionTags(builder, doc);
                    break;
                case Deprecated:
                    appendDeprecatedTags(builder, doc);
                    break;
                case See:
                    appendSeeTags(builder, doc);
                    break;
            }
        });
        return builder;
    }

    public RstElementBuilder<?, ?, ?> appendAuthorTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@author");
        return builder.ifTrue(tags.length > 0, () -> {
            for (Tag tag : tags) {
                if (StringUtils.notNullOrEmpty(tag.text())) {
                    builder.directive(Directives.SectionAuthor, tag.text());
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendSinceTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@since");
        return builder.ifTrue(tags.length > 0, () -> {
            for (Tag tag : tags) {
                if (StringUtils.notNullOrEmpty(tag.text())) {
                    builder.directive(Directives.VersionAdded, tag.text());
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendVersionTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@version");
        return builder.ifTrue(tags.length > 0, () -> {
            for (Tag tag : tags) {
                if (StringUtils.notNullOrEmpty(tag.text())) {
                    builder.directive(Directives.VersionChanged, tag.text());
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendDeprecatedTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@deprecated");
        return builder.ifTrue(tags.length > 0, () -> {
            for (Tag tag : tags) {
                if (StringUtils.notNullOrEmpty(tag.text())) {
                    builder.directive(Directives.Deprecated, tag.text());
                }
            }
        });
    }

    public RstElementBuilder<?, ?, ?> appendSeeTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@see");
        return builder.ifTrue(tags.length > 0, () -> {
            builder.directive(Directives.SeeAlso, seeAlsoBuilder -> {
                seeAlsoBuilder.lineBlock(lineBlockBuilder -> {
                    for (Tag tag : tags) {
                        if (StringUtils.notNullOrEmpty(tag.text())) {
                            lineBlockBuilder.item(ib -> ib.addChild(seeTagToJavaRef(doc, (SeeTag) tag)));
                        }
                    }
                });
            });
        });
    }

    public InlineElement seeTagToJavaRef(Doc doc, SeeTag see) {
        String seeText = docContext_.getHtmlDocletWriter().replaceDocRootDir(see.text());
        InlineElement result;
        if (seeText.startsWith("<")) {
            result = HtmlUtils.hrefToLink(seeText);
        } else if (seeText.startsWith("\"")) {
            result = Rst4Sphinx.elements().text("\\ " + StringUtils.dequote(seeText, '\"') + "\\ ");
        } else {
            result = new CommentBuilder(docContext_, doc, new Tag[]{see}).buildBody().stream()
                    .filter(Paragraph.class::isInstance).findFirst()
                    .map(Paragraph.class::cast).orElse(Rst4Sphinx.elements().paragraph())
                    .getChildren().stream().findFirst().orElse(Rst4Sphinx.elements().text(""));

            // InlineElement result = docContext_.getHtmlDocletWriter().seeTagToContent(element, holderTag, see);
        }
        return result;

    }

}
