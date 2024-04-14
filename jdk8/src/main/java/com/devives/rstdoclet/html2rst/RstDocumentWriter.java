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
package com.devives.rstdoclet.html2rst;


import com.devives.html2rst.HtmlUtils;
import com.devives.sphinx.rst.document.JavaRef;
import com.devives.sphinx.rst.document.Ref;

public class RstDocumentWriter extends com.devives.html2rst.RstDocumentWriter {

    @Override
    public void visitLink(String href, String label) {
        if (href.startsWith("#")) {
            // ссылка на якорь в документе
            getTextBuilder().addChild(new Ref(href.substring(1), HtmlUtils.unescapeBrackets(label)));
            //getTextBuilder().role("ref", href.substring(1), unescapeBrackets(label));
        } else if (href.startsWith("@")) {
            getTextBuilder().addChild(new JavaRef(href.substring(1), HtmlUtils.unescapeBrackets(label)));
            //getTextBuilder().role("java:ref", href.substring(1), );
        } else {
            getTextBuilder().anonymousLink(href, label);
        }
    }

}
