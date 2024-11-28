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
package com.devives.rstdoclet.html;

import com.devives.html2rst.HtmlUtils;
import com.devives.rst.document.inline.InlineElement;
import com.devives.rstdoclet.rst.builder.JavaMemberRefBuilder;
import com.devives.rstdoclet.rst.builder.JavaTypeRefBuilder;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.SeeTree;
import jdk.javadoc.internal.doclets.formats.html.ClassWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.HtmlDocletWriter;
import jdk.javadoc.internal.doclets.formats.html.HtmlOptions;
import jdk.javadoc.internal.doclets.formats.html.markup.ContentBuilder;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTree;
import jdk.javadoc.internal.doclets.formats.html.markup.Text;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.builders.SerializedFormBuilder;
import jdk.javadoc.internal.doclets.toolkit.util.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TagletWriterImpl extends jdk.javadoc.internal.doclets.formats.html.TagletWriterImpl {

    private final boolean isFirstSentence;
    private final HtmlDocletWriter htmlWriter;
    private final HtmlConfiguration configuration;
    private final HtmlOptions options;
    private final Utils utils;
    private final Method seeTagOutputMethod_;

    public TagletWriterImpl(HtmlDocletWriter htmlWriter, boolean isFirstSentence) {
        super(htmlWriter, isFirstSentence);
        this.isFirstSentence = isFirstSentence;
        this.htmlWriter = htmlWriter;
        this.configuration = htmlWriter.configuration;
        this.options = configuration.getOptions();
        this.utils = configuration.utils;
        try {
            seeTagOutputMethod_ = jdk.javadoc.internal.doclets.formats.html.TagletWriterImpl.class
                    .getDeclaredMethod("seeTagOutput", new Class<?>[]{Element.class, SeeTree.class});
            seeTagOutputMethod_.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Element getLinkedElement(Element referer, String signature) {
        return super.getLinkedElement(referer, signature);
    }

    @Override
    public Content linkTagOutput(Element element, LinkTree tag) {
        return super.linkTagOutput(element, tag);
    }

    @Override
    public Content seeTagOutput(Element holder, List<? extends SeeTree> seeTags) {
        List<Content> links = new ArrayList<>();
        for (SeeTree dt : seeTags) {
            TagletWriterImpl t = new TagletWriterImpl(htmlWriter, isFirstSentence);
            links.add(t.seeTagOutput(holder, dt));
        }
        return links.get(0);
    }

    private Content seeTagOutput(Element element, SeeTree seeTag) {
        try {
            Content content = (Content) seeTagOutputMethod_.invoke(this, element, seeTag);
            if (content instanceof RstContent) {
                return content;
            }
            CommentHelper ch = utils.getCommentHelper(element);
            Element ref = ch.getReferencedElement(seeTag);
            Element refMem = ch.getReferencedMember(ref);
            if (refMem != null) {
                String text = getLabelDocTree(ch, seeTag)
                        .map(dText -> HtmlUtils.removeCodeTags(dText.toString()))
                        .orElseGet(() -> HtmlUtils.removeCodeTags(ref.toString()));
                InlineElement inlineElement = new JavaMemberRefBuilder<>(refMem, configuration.utils).setText(text).build();
                return new RstContent(inlineElement, content);
            }
            TypeElement refClass = ch.getReferencedClass(ref);
            if (refClass != null) {
                String text = getLabelDocTree(ch, seeTag)
                        .map(dText -> HtmlUtils.removeCodeTags(dText.toString()))
                        .orElseGet(() -> HtmlUtils.removeCodeTags(refClass.getSimpleName().toString()));
                InlineElement inlineElement = new JavaTypeRefBuilder<>(refClass).setText(text).build();
                return new RstContent(inlineElement, content);
            }
            return content;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private Optional<DocTree> getLabelDocTree(CommentHelper ch, DocTree seeTag) {
        List<? extends DocTree> list = ch.getDescription(seeTag);
        return list.size() == 1 ? Optional.empty() : Optional.of(list.get(1));
    }

}
