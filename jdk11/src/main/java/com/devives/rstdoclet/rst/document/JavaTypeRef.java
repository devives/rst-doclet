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

import javax.lang.model.element.TypeElement;

public class JavaTypeRef extends JavaRef {

    public JavaTypeRef(TypeElement typeElement) {
        super(formatUri(typeElement), formatText(typeElement));
    }

    public JavaTypeRef(TypeElement typeElement, String label) {
        super(formatUri(typeElement), StringUtils.requireNotNullOrEmpty(label));
    }

    private static String formatText(TypeElement typeElement) {
        return typeElement.getSimpleName().toString();
    }

    private static String formatUri(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString();
    }
}
