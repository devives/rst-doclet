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
package com.devives.rstdoclet.util;

import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor14;

public class ImportsCollectorImpl extends ImportsCollectorAbst {

    public ImportsCollectorImpl(Utils utils) {
        super(utils);
    }

    protected void collectTypeParameters(TypeMirror typeMirror) {
        typeMirror.accept(new SimpleTypeVisitor14<Content, Void>() {
            @Override
            public Content visitDeclared(DeclaredType t, Void unused) {
                for (TypeMirror argTypeMirror : t.getTypeArguments()) {
                    collect(argTypeMirror);
                }
                return super.visitDeclared(t, unused);
            }
        }, null);
    }
}
