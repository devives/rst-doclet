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

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.SeeTree;
import jdk.javadoc.internal.doclets.formats.html.HtmlLinkInfo;
import jdk.javadoc.internal.doclets.formats.html.markup.ContentBuilder;
import jdk.javadoc.internal.doclets.formats.html.markup.RawHtml;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.CommentHelper;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class HtmlDocletWriter extends jdk.javadoc.internal.doclets.formats.html.HtmlDocletWriter {

    private final jdk.javadoc.internal.doclets.formats.html.HtmlDocletWriter delegateWriter;

    public HtmlDocletWriter(jdk.javadoc.internal.doclets.formats.html.HtmlDocletWriter delegateWriter) {
        super(delegateWriter.configuration, delegateWriter.path);
        this.delegateWriter = delegateWriter;
    }

    @Override
    public Content commentTagsToContent(DocTree holderTag, Element element, List<? extends DocTree> tags, boolean isFirstSentence) {
        return delegateWriter.commentTagsToContent(holderTag, element, tags, isFirstSentence);
    }

    public boolean addAnnotationInfo(VariableElement param,
                                     StringBuilder stringBuilder) {
        boolean lineBreak = false;
        List<Content> annotations = getAnnotations(param.getAnnotationMirrors(), lineBreak);
        String sep = "";
        if (annotations.isEmpty()) {
            return false;
        }
        Content htmltree = new ContentBuilder();
        for (Content annotation : annotations) {
            htmltree.add(sep);
            htmltree.add(annotation);
            if (!lineBreak) {
                sep = " ";
            }
        }
        stringBuilder.append(htmltree);
        return true;
    }

    private boolean addAnnotationInfo(List<? extends AnnotationMirror> descList,
                                      boolean lineBreak,
                                      Content htmltree) {
        List<Content> annotations = getAnnotations(descList, lineBreak);
        String sep = "";
        if (annotations.isEmpty()) {
            return false;
        }
        for (Content annotation: annotations) {
            htmltree.add(sep);
            htmltree.add(annotation);
            if (!lineBreak) {
                sep = " ";
            }
        }
        return true;
    }

    public void addReceiverAnnotations(TypeMirror rcvrType,
                                       List descList,
                                       StringBuilder stringBuilder) {
        Content tree = new ContentBuilder();
        addAnnotationInfo(descList, false, tree);
        tree.add(nbsp);
        //todo
        //tree.add(rcvrType.typeName());
        HtmlLinkInfo linkInfo = new HtmlLinkInfo(configuration,
                HtmlLinkInfo.Kind.CLASS_SIGNATURE, rcvrType);
        tree.add(getTypeParameterLinks(linkInfo));
        tree.add(nbsp);
        tree.add("this");
        stringBuilder.append(tree.toString());
    }


    public static final Content nbsp = new RawHtml("&nbsp;");

    public static final Content zws = new RawHtml("&#8203;");

    public String replaceDocRootDir(Element element, SeeTree see) {
        CommentHelper ch = utils.getCommentHelper(element);
        return replaceDocRootDir(utils.normalizeNewlines(ch.getText(see)).toString());
    }

}
