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

import com.devives.html2rst.HtmlDocumentReader;
import com.devives.rst.Rst;
import com.devives.rst.document.RstDocument;
import com.devives.rst.document.RstNode;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommentBuilder {

    private final RstGeneratorContext docContext_;
    private final Doc doc_;
    private final Tag holderTag_;
    private final Tag[] tags_;

    public CommentBuilder(RstGeneratorContext docContext, Doc doc) {
        this.docContext_ = Objects.requireNonNull(docContext);
        this.doc_ = Objects.requireNonNull(doc);
        this.holderTag_ = null;
        this.tags_ = doc.inlineTags();
    }

    public CommentBuilder(RstGeneratorContext docContext, Doc doc, Tag holderTag) {
        this.docContext_ = Objects.requireNonNull(docContext);
        this.doc_ = Objects.requireNonNull(doc);
        this.holderTag_ = Objects.requireNonNull(holderTag);
        this.tags_ = holderTag.inlineTags();
    }

    public CommentBuilder(RstGeneratorContext docContext, Doc doc, Tag[] tags) {
        this.docContext_ = Objects.requireNonNull(docContext);
        this.doc_ = Objects.requireNonNull(doc);
        this.holderTag_ = null;
        this.tags_ = tags;
    }

    public RstDocument build() {
        Content content = docContext_.getHtmlDocletWriter().commentTagsToContent(holderTag_, doc_, tags_, false);
        String htmlText = content.toString();
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
