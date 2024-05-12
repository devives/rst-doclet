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
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.HtmlDocletWriter;
import jdk.javadoc.internal.doclets.formats.html.HtmlLinkInfo;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.DocPath;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class HtmlDocletWriterAdapterImpl implements HtmlDocletWriterAdapter {

    private final HtmlDocletWriter docletWriter_;

    public HtmlDocletWriterAdapterImpl(HtmlDocletWriter docletWriter) {
        docletWriter_ = docletWriter;
    }

    @Override
    public Content commentTagsToContent(DocTree holderTag,
                                        Element element,
                                        List<? extends DocTree> tags,
                                        boolean isFirstSentence) {
        return docletWriter_.commentTagsToContent(holderTag,
                element,
                tags,
                isFirstSentence);
    }

    @Override
    public void addReceiverAnnotations(ExecutableElement member, TypeMirror rcvrType, List<? extends AnnotationMirror> descList, StringBuilder stringBuilder) {
        ((com.devives.rstdoclet.html.HtmlDocletWriter) docletWriter_).addReceiverAnnotations(rcvrType, descList, stringBuilder);
    }

    @Override
    public boolean addAnnotationInfo(int i, ExecutableElement member, VariableElement param, StringBuilder stringBuilder) {
        return ((com.devives.rstdoclet.html.HtmlDocletWriter) docletWriter_).addAnnotationInfo(param, stringBuilder);
    }

    @Override
    public DocPath path() {
        return docletWriter_.path;
    }

    @Override
    public DocPath pathToRoot() {
        return docletWriter_.pathToRoot;
    }

    @Override
    public String replaceDocRootDir(Element element, SeeTree see) {
        return ((com.devives.rstdoclet.html.HtmlDocletWriter) docletWriter_).replaceDocRootDir(element, see);
    }

    @Override
    public Content getTypeParameterLinks(HtmlConfiguration configuration, ExecutableElement executableElement) {
        HtmlLinkInfo linkInfo = new HtmlLinkInfo(configuration, HtmlLinkInfo.Kind.MEMBER_TYPE_PARAMS, executableElement);
        linkInfo.linkToSelf = false;
        return docletWriter_.getTypeParameterLinks(linkInfo);
    }

    @Override
    public Content getTypeParameterLinks(HtmlConfiguration configuration, TypeElement typeElement) {
        HtmlLinkInfo linkInfo = new HtmlLinkInfo(configuration, HtmlLinkInfo.Kind.CLASS_SIGNATURE, typeElement);
        linkInfo.linkToSelf = false;
        return docletWriter_.getTypeParameterLinks(linkInfo);
    }

    @Override
    public Content getLink(HtmlConfiguration configuration, LinkInfoKind kind, TypeMirror returnType, boolean linkToSelf) {
        HtmlLinkInfo linkInfo = new HtmlLinkInfo(configuration, fromLinkInfoKind(kind), returnType);
        linkInfo.linkToSelf = linkToSelf;
        return docletWriter_.getLink(linkInfo);
    }

    private HtmlLinkInfo.Kind fromLinkInfoKind(LinkInfoKind kind) {
        switch (kind) {
            case RETURN_TYPE:
                return HtmlLinkInfo.Kind.RETURN_TYPE;
            case IMPLEMENTED_INTERFACES:
                return HtmlLinkInfo.Kind.IMPLEMENTED_INTERFACES;
            case CLASS_SIGNATURE_PARENT_NAME:
                return HtmlLinkInfo.Kind.CLASS_SIGNATURE_PARENT_NAME;
            default:
                throw new RuntimeException("Unexpected LinkInfoKind '" + kind.name() + "'");
        }
    }

    @Override
    public List<? extends DocTree> getDescription(Element element, DocTree docTree) {
        return docletWriter_.configuration.utils.getCommentHelper(element).getDescription(docTree);
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        if (iface.isInstance(docletWriter_)) {
            return (T) docletWriter_;
        }
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(docletWriter_);
    }
}
