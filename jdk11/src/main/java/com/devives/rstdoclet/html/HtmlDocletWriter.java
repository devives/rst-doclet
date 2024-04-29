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
package com.devives.rstdoclet.html;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.SeeTree;
import jdk.javadoc.internal.doclets.formats.html.LinkInfoImpl;
import jdk.javadoc.internal.doclets.formats.html.markup.ContentBuilder;
import jdk.javadoc.internal.doclets.formats.html.markup.RawHtml;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.CommentHelper;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
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

    public boolean addAnnotationInfo(int indent, Element element, VariableElement param, StringBuilder stringBuilder) {
        Content htmltree = new ContentBuilder();
        boolean result = addAnnotationInfo(indent, element, param, htmltree);
        if (result) {
            stringBuilder.append(htmltree.toString());
        }
        return result;
    }

    public void addReceiverAnnotations(ExecutableElement member, TypeMirror rcvrType,
                                       List descList, StringBuilder stringBuilder) {
        Content tree = new ContentBuilder();
        addReceiverAnnotationInfo(member, descList, tree);
        tree.addContent(nbsp);
        //todo
        //tree.add(rcvrType.typeName());
        LinkInfoImpl linkInfo = new LinkInfoImpl(configuration,
                LinkInfoImpl.Kind.CLASS_SIGNATURE, rcvrType);
        tree.addContent(getTypeParameterLinks(linkInfo));
        tree.addContent(nbsp);
        tree.addContent("this");
        stringBuilder.append(tree.toString());
    }

    public static final Content nbsp = new RawHtml("&nbsp;");

    public static final Content zws = new RawHtml("&#8203;");

    public String replaceDocRootDir(Element element, SeeTree see) {
        CommentHelper ch = utils.getCommentHelper(element);
        return replaceDocRootDir(utils.normalizeNewlines(ch.getText(see)).toString());
    }

}
