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
import com.devives.rstdoclet.html2rst.jdkloans.HtmlDocletWriter;
import com.sun.source.doctree.*;
import jdk.javadoc.internal.doclets.toolkit.Content;
import org.jsoup.Jsoup;

import javax.lang.model.element.Element;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link HtmlDocletWriter#commentTagsToContent(DocTree, Element, List, boolean)}
 */
public class CommentBuilder {

    private final Element element_;
    private final DocTree holderTag;
    private final HtmlDocletWriter docContext_;

    public CommentBuilder(Element element, HtmlDocletWriter docContext) {
        this.element_ = Objects.requireNonNull(element);
        this.docContext_ = Objects.requireNonNull(docContext);
        this.holderTag = null;
    }

    public CommentBuilder(DocTree holderTag, Element element, HtmlDocletWriter docContext) {
        this.holderTag = Objects.requireNonNull(holderTag);
        this.element_ = Objects.requireNonNull(element);
        this.docContext_ = Objects.requireNonNull(docContext);
    }

    public RstDocument build() {
        List<? extends DocTree> tags = holderTag != null
                ? getDescription(holderTag)
                : docContext_.configuration.utils.getBody(element_);
        //DocTree[] tags = doc != null ? doc.inlineTags() : holderTag.inlineTags();
        Content content = docContext_.commentTagsToContent(holderTag, element_, tags, false);
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

    private List<? extends DocTree> getDescription(DocTree docTree) {
        if (docTree instanceof ParamTree paramTree) {
            return paramTree.getDescription();
        } else if (docTree instanceof ThrowsTree throwsTree) {
            return throwsTree.getDescription();
        } else if (docTree instanceof ReturnTree returnTree) {
            return returnTree.getDescription();
        } else if (docTree instanceof VersionTree versionTree) {
            return versionTree.getBody();
        } else if (docTree instanceof AuthorTree authorTree) {
            return authorTree.getName();
        } else if (docTree instanceof SinceTree sinceTree) {
            return sinceTree.getBody();
        } else if (docTree instanceof SeeTree sinceTree) {
            return sinceTree.getReference();
        } else if (docTree instanceof DeprecatedTree deprecatedTree) {
            return deprecatedTree.getBody();
//        } else if (docTree instanceof TypeParameterElement typeParameterElement) {
//            return typeParameterElement.getDescription();
        } else {
            return Collections.emptyList();
        }
    }
}
