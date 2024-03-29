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

import com.devives.rstdoclet.html2rst.DocUtils;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;

import java.util.Arrays;

public class JavaMemberRef extends JavaRef {

    public JavaMemberRef(MemberDoc memberDoc) {
        super(formatUri(memberDoc), formatText(memberDoc));
    }

    private static String formatText(MemberDoc memberDoc) {
        String member;
        if (memberDoc instanceof MethodDoc) {
            member = formatNameWithParamTypeNames((MethodDoc) memberDoc);
        } else if (memberDoc instanceof FieldDoc) {
            member = memberDoc.name();
        } else {
            member = memberDoc.name();
        }
        String type = memberDoc.containingClass().typeName();
        return type + "." + member;
    }

    private static String formatUri(MemberDoc memberDoc) {
        String member;
        if (memberDoc instanceof MethodDoc) {
            member = formatNameWithParamTypeNames((MethodDoc) memberDoc);
        } else if (memberDoc instanceof FieldDoc) {
            member = memberDoc.name();
        } else {
            member = memberDoc.name();
        }
        String type = memberDoc.containingClass().qualifiedTypeName();
        return type + "." + member;
    }


    public static String formatNameWithParamTypeNames(ExecutableMemberDoc methodDoc) {
        return new StringBuilder()
                .append(methodDoc.name())
                .append("(")
                .append(String.join(", ",
                        Arrays.stream(methodDoc.parameters())
                                .map(p -> DocUtils.formatTypeName(p.type()))
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
