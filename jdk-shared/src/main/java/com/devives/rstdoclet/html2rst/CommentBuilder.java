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

import com.devives.html2rst.HtmlDocumentReader;
import com.devives.rst.Rst;
import com.devives.rst.document.RstDocument;
import com.devives.rst.document.RstNode;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.SeeTree;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;
import org.jsoup.Jsoup;

import javax.lang.model.element.Element;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link jdk.javadoc.internal.doclets.formats.html.HtmlDocletWriter#commentTagsToContent(DocTree, Element, List, boolean)}
 */
public class CommentBuilder {

    private final Element element_;
    private final DocTree holderTag;
    private final RstGeneratorContext docContext_;
    private final List<? extends DocTree> tags_;
    private final Utils utils_;

    public CommentBuilder(Element element, RstGeneratorContext docContext) {
        this.element_ = Objects.requireNonNull(element);
        this.docContext_ = Objects.requireNonNull(docContext);
        this.holderTag = null;
        this.utils_ = docContext_.getRstConfiguration().getHtmlConfiguration().utils;
        this.tags_ = utils_.getBody(element_);
    }

    public CommentBuilder(DocTree holderTag, Element element, RstGeneratorContext docContext) {
        this.holderTag = Objects.requireNonNull(holderTag);
        this.element_ = Objects.requireNonNull(element);
        this.docContext_ = Objects.requireNonNull(docContext);
        this.utils_ = docContext_.getRstConfiguration().getHtmlConfiguration().utils;
        this.tags_ = docContext_.getHtmlDocletWriter().getDescription(element_, holderTag);
    }

    public CommentBuilder(RstGeneratorContext docContext, Element element, SeeTree seeTag) {
        this.docContext_ = Objects.requireNonNull(docContext);
        this.element_ = Objects.requireNonNull(element);
        this.tags_ = Collections.singletonList(Objects.requireNonNull(seeTag));
        this.utils_ = docContext_.getRstConfiguration().getHtmlConfiguration().utils;
        this.holderTag = null;
    }

    public RstDocument build() {
        String htmlText = docContext_.getHtmlDocletWriter().commentTagsToContent(holderTag, element_, tags_, false).toString();
        if (!htmlText.trim().isEmpty()) {
            HrefConverter hrefConverter = new HrefConverterImpl(docContext_);
            RstDocumentWriter visitor = new RstDocumentWriter(hrefConverter);
            new HtmlDocumentReader(Jsoup.parse(htmlText)).accept(visitor);
            return visitor.getDocument();
        } else {
            return Rst.builders().document().build();
        }
    }

    public Collection<RstNode> buildBody() {
        RstDocument rstDocument = build();
        return rstDocument.getChildren().stream()
                .filter(RstNode.class::isInstance)
                .map(RstNode.class::cast)
                .collect(Collectors.toCollection(ArrayList<RstNode>::new));
    }

}
