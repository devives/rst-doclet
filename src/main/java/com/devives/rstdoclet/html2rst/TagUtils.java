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

import com.devives.rst.builder.BodyBuilders;
import com.devives.rst.builder.RstElementBuilder;
import com.devives.rst.document.inline.InlineElement;
import com.devives.rst.document.inline.Link;
import com.devives.rst.util.StringUtils;
import com.devives.rstdoclet.rst.document.JavaMemberRef;
import com.devives.rstdoclet.rst.document.JavaPackageRef;
import com.devives.rstdoclet.rst.document.JavaTypeRef;
import com.devives.rstdoclet.rst.document.Ref;
import com.devives.sphinx.rst.Rst4Sphinx;
import com.devives.sphinx.rst.document.directive.Directives;
import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagUtils {

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
    public static RstElementBuilder<?, ?, ?> appendTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc, Collection<TagName> tagNames) {
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

    public static RstElementBuilder<?, ?, ?> appendAuthorTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@author");
        return builder.ifTrue(tags.length > 0, () -> {
            for (Tag tag : tags) {
                if (StringUtils.notNullOrEmpty(tag.text())) {
                    builder.directive(Directives.SectionAuthor, tag.text());
                }
            }
        });
    }

    public static RstElementBuilder<?, ?, ?> appendSinceTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@since");
        return builder.ifTrue(tags.length > 0, () -> {
            for (Tag tag : tags) {
                if (StringUtils.notNullOrEmpty(tag.text())) {
                    builder.directive(Directives.VersionAdded, tag.text());
                }
            }
        });
    }

    public static RstElementBuilder<?, ?, ?> appendVersionTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@version");
        return builder.ifTrue(tags.length > 0, () -> {
            for (Tag tag : tags) {
                if (StringUtils.notNullOrEmpty(tag.text())) {
                    builder.directive(Directives.VersionChanged, tag.text());
                }
            }
        });
    }

    public static RstElementBuilder<?, ?, ?> appendDeprecatedTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@deprecated");
        return builder.ifTrue(tags.length > 0, () -> {
            for (Tag tag : tags) {
                if (StringUtils.notNullOrEmpty(tag.text())) {
                    builder.directive(Directives.Deprecated, tag.text());
                }
            }
        });
    }

    public static RstElementBuilder<?, ?, ?> appendSeeTags(BodyBuilders<?, ?, ?, ?> builder, Doc doc) {
        Tag[] tags = doc.tags("@see");
        return builder.ifTrue(tags.length > 0, () -> {
            builder.directive(Directives.SeeAlso, seeAlsoBuilder -> {
                seeAlsoBuilder.lineBlock(lineBlockBuilder -> {
                    for (Tag tag : tags) {
                        if (StringUtils.notNullOrEmpty(tag.text())) {
                            lineBlockBuilder.item(ib -> ib.addChild(TagUtils.seeTagToJavaRef((SeeTag) tag)));
                        }
                    }
                });
            });
        });
    }

    public static InlineElement seeTagToJavaRef(SeeTag see) {
        String text = see.text().trim();
        String label = see.label().trim();
        if (text.contains("<a")) {
            return hrefToLink(text);
        } else if (text.startsWith("\"")) {
            return Rst4Sphinx.elements().text("\\ " + StringUtils.dequote(text, '\"') + "\\ ");
        } else if (see.referencedMember() != null) {
            return (label.isEmpty())
                    ? new JavaMemberRef(see.referencedMember())
                    : new JavaMemberRef(see.referencedMember(), label);
        } else if (see.referencedClass() != null) {
            return (label.isEmpty())
                    ? new JavaTypeRef(see.referencedClass())
                    : new JavaTypeRef(see.referencedClass(), label);
        } else if (see.referencedPackage() != null) {
            return (label.isEmpty())
                    ? new JavaPackageRef(see.referencedPackage())
                    : new JavaPackageRef(see.referencedPackage(), label);
        } else {
            return Rst4Sphinx.elements().text("\\ " + text + "\\ ");
        }
    }

    /**
     * <a href="http://google.ru"/>
     * <a href="http://google.ru"></a>
     * <a href="http://google.ru">Google</a>
     */
    private static final Pattern PATTERN_A_HREF = Pattern.compile("href\\s*=\\s*\\\"(.*?)\\\"");
    private static final Pattern PATTERN_A_TEXT = Pattern.compile("<\\s*a\\s+.*?>(.*?)</\\s*a\\s*>");

    /**
     * @param a "<a href="url">Text</a>" tag.
     * @return Instance of {@link Link}
     */
    public static InlineElement hrefToLink(String a) {
        Matcher hrefMatcher = PATTERN_A_HREF.matcher(a);
        Matcher textMatcher = PATTERN_A_TEXT.matcher(a);

        String href = hrefMatcher.find() ? hrefMatcher.group(1) : a;
        String text = textMatcher.find() ? textMatcher.group(1) : null;

        String relativeLinkLowerCase = href.toLowerCase(Locale.US);
        if (relativeLinkLowerCase.startsWith("mailto:") ||
                relativeLinkLowerCase.startsWith("http:") ||
                relativeLinkLowerCase.startsWith("https:") ||
                relativeLinkLowerCase.startsWith("file:") ||
                relativeLinkLowerCase.startsWith("#") ||
                relativeLinkLowerCase.contains(".html")) {
            return new Link(href, StringUtils.findFirstNotNullOrEmpty(text, href));
        } else {
            return new Ref(href, StringUtils.findFirstNotNullOrEmpty(text, href));
        }

    }


}
