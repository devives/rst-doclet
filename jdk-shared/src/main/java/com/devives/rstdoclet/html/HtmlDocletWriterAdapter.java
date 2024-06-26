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
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.DocPath;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public interface HtmlDocletWriterAdapter extends Wrapper {

    Content commentTagsToContent(DocTree holderTag,
                                 Element element,
                                 List<? extends DocTree> tags,
                                 boolean isFirstSentence);

    void addReceiverAnnotations(ExecutableElement member, TypeMirror rcvrType, List<? extends AnnotationMirror> descList, StringBuilder stringBuilder);

    boolean addAnnotationInfo(int i, ExecutableElement member, VariableElement param, StringBuilder stringBuilder);

    DocPath path();

    DocPath pathToRoot();

    String replaceDocRootDir(Element element, SeeTree see);

    Content getTypeParameterLinks(HtmlConfiguration configuration, ExecutableElement executableElement);

    Content getTypeParameterLinks(HtmlConfiguration configuration, TypeElement typeElement);

    Content getLink(HtmlConfiguration configuration, LinkInfoKind kind, TypeMirror returnType, boolean linkToSelf);

    List<? extends DocTree> getDescription(Element element, DocTree docTree);
}
