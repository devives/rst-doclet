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
package com.devives.rstdoclet.rst.document;

import com.devives.rst.util.StringUtils;
import com.devives.rstdoclet.html2rst.DocUtils;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class JavaMemberRef extends JavaRef {

    public JavaMemberRef(Element memberDoc, Utils utils) {
        super(formatUri(memberDoc, utils), formatText(memberDoc, utils));
    }

    public JavaMemberRef(Element memberDoc, Utils utils, String label) {
        super(formatUri(memberDoc, utils), StringUtils.requireNotNullOrEmpty(label));
    }

    private static String formatText(Element memberDoc, Utils utils) {
        String member;
        if (memberDoc instanceof ExecutableElement) {
            member = formatNameWithParamTypeNames((ExecutableElement) memberDoc, utils);
        } else if (memberDoc instanceof VariableElement) {
            member = memberDoc.getSimpleName().toString();
        } else {
            member = memberDoc.getSimpleName().toString();
        }
        String type = memberDoc.getEnclosingElement().getSimpleName().toString();
        return type + "." + member;
    }

    private static String formatUri(Element memberDoc, Utils utils) {
        String member;
        if (memberDoc instanceof ExecutableElement) {
            member = formatNameWithParamTypeNames((ExecutableElement) memberDoc, utils);
        } else if (memberDoc instanceof VariableElement) {
            member = memberDoc.getSimpleName().toString();
        } else {
            member = memberDoc.getSimpleName().toString();
        }
        String type = ((TypeElement) memberDoc.getEnclosingElement()).getQualifiedName().toString();
        return type + "." + member;
    }


    public static String formatNameWithParamTypeNames(ExecutableElement methodDoc, Utils utils) {
        return new StringBuilder()
                .append(methodDoc.getSimpleName())
                .append("(")
                .append(String.join(", ",
                        methodDoc.getParameters().stream()
                                .map(p -> DocUtils.formatTypeName(p.asType(), utils))
                                .toArray(String[]::new)))
                .append(")")
                .toString();
    }
//    private static String formatText(String type, String method) {
//        StringUtils.requireNotNullOrEmpty(type);
//        StringUtils.requireNotNullOrEmpty(method);
//        return type + "." + method + "()";
//    }
//
//    private static String formatUri(String type, String method) {
//        StringUtils.requireNotNullOrEmpty(type);
//        StringUtils.requireNotNullOrEmpty(method);
//        return type + "." + method + "()";
//    }
}
