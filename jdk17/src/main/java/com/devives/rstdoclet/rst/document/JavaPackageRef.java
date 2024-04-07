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

import javax.lang.model.element.PackageElement;

public class JavaPackageRef extends JavaRef {

    public JavaPackageRef(PackageElement packageDoc) {
        super(formatUri(packageDoc), formatText(packageDoc));
    }

    public JavaPackageRef(PackageElement packageDoc, String label) {
        super(formatUri(packageDoc), StringUtils.requireNotNullOrEmpty(label));
    }

    private static String formatText(PackageElement packageDoc) {
        return packageDoc.getQualifiedName().toString();
    }

    private static String formatUri(PackageElement packageDoc) {
        return packageDoc.getQualifiedName().toString();
    }
}
