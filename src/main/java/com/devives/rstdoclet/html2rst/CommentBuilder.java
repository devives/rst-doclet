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

import com.devives.rst.Rst;
import com.devives.rst.document.RstDocument;
import com.devives.rst.document.RstNode;
import com.devives.rstdoclet.ConfigurationImpl;
import com.devives.rstdoclet.html2rst.jdkloans.DocContext;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link DocContext#commentTagsToContent(Tag, Doc, Tag[], boolean)}
 */
public class CommentBuilder {

    private final Doc doc;
    private final Tag holderTag;
    private final DocContext docContext_;

    public CommentBuilder(Doc doc, Doc parentDoc, ConfigurationImpl configuration) {
        this.docContext_ = new DocContext(parentDoc, configuration);
        this.doc = Objects.requireNonNull(doc);
        this.holderTag = null;
    }

    public CommentBuilder(Tag holderTag, Doc parentDoc, ConfigurationImpl configuration) {
        this.docContext_ = new DocContext(parentDoc, configuration);
        this.doc = null;
        this.holderTag = Objects.requireNonNull(holderTag);
    }

    public RstDocument build() {
        Tag[] tags = doc != null ? doc.inlineTags() : holderTag.inlineTags();
        Content content = docContext_.commentTagsToContent(holderTag, docContext_.getParentDoc(), tags, false);
        String htmlText = content.toString();
        if (!htmlText.trim().isEmpty()) {
            RstDocumentWriter visitor = new RstDocumentWriter();
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
