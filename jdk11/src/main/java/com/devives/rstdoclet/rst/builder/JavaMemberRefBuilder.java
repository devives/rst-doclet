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
package com.devives.rstdoclet.rst.builder;

import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rstdoclet.html2rst.DocUtils;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class JavaMemberRefBuilder<
        PARENT extends RstNodeBuilder<?, ?, ?, ?>,
        SELF extends JavaMemberRefBuilder<PARENT, SELF>>
        extends JavaRoleBuilderAbst<PARENT, SELF> {

    private final Element memberElement_;
    private final Utils utils_;

    public JavaMemberRefBuilder(Element memberElement, Utils utils) {
        this.memberElement_ = memberElement;
        this.utils_ = utils;
    }

    @Override
    protected String formatName() {
        return "java:ref";
    }

    @Override
    protected String formatTarget() {
        String member;
        if (memberElement_ instanceof ExecutableElement) {
            member = formatNameWithParamTypeNames((ExecutableElement) memberElement_, utils_);
        } else if (memberElement_ instanceof VariableElement) {
            member = memberElement_.getSimpleName().toString();
        } else {
            member = memberElement_.getSimpleName().toString();
        }
        String type = ((TypeElement) memberElement_.getEnclosingElement()).getQualifiedName().toString();
        return type + "." + member;
    }

    @Override
    protected String formatText() {
        if (text_ != null) {
            return text_;
        } else {
            String member;
            if (memberElement_ instanceof ExecutableElement) {
                member = formatNameWithParamTypeNames((ExecutableElement) memberElement_, utils_);
            } else if (memberElement_ instanceof VariableElement) {
                member = memberElement_.getSimpleName().toString();
            } else {
                member = memberElement_.getSimpleName().toString();
            }
            String type = memberElement_.getEnclosingElement().getSimpleName().toString();
            return type + "." + member;
        }
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

}
