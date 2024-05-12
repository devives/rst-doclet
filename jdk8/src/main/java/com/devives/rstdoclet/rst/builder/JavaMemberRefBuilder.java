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
package com.devives.rstdoclet.rst.builder;

import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rstdoclet.html2rst.DocUtils;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;

import java.util.Arrays;

public class JavaMemberRefBuilder<
        PARENT extends RstNodeBuilder<?, ?, ?, ?>,
        SELF extends JavaMemberRefBuilder<PARENT, SELF>>
        extends JavaRoleBuilderAbst<PARENT, SELF> {

    private final MemberDoc memberDoc_;

    public JavaMemberRefBuilder(MemberDoc memberDoc) {
        this.memberDoc_ = memberDoc;
    }

    @Override
    protected String formatName() {
        return "java:ref";
    }

    @Override
    protected String formatTarget() {
        String member;
        if (memberDoc_ instanceof MethodDoc) {
            member = formatNameWithParamTypeNames((MethodDoc) memberDoc_);
        } else if (memberDoc_ instanceof FieldDoc) {
            member = memberDoc_.name();
        } else {
            member = memberDoc_.name();
        }
        String type = memberDoc_.containingClass().qualifiedTypeName();
        return type + "." + member;
    }

    @Override
    protected String formatText() {
        if (text_ != null) {
            return text_;
        } else {
            String member;
            if (memberDoc_ instanceof MethodDoc) {
                member = formatNameWithParamTypeNames((MethodDoc) memberDoc_);
            } else if (memberDoc_ instanceof FieldDoc) {
                member = memberDoc_.name();
            } else {
                member = memberDoc_.name();
            }
            String type = memberDoc_.containingClass().typeName();
            return type + "." + member;
        }
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

}
