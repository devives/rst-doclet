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
package com.devives.rstdoclet.html2rst;


import com.devives.html2rst.HtmlUtils;
import com.devives.sphinx.rst.document.JavaRef;
import com.devives.sphinx.rst.document.Ref;

import java.util.Map;

import static com.devives.html2rst.HtmlUtils.escapeUnderlines;
import static com.devives.html2rst.HtmlUtils.unescapeLtRtAmpBSlash;

public class RstDocumentWriter extends com.devives.html2rst.RstDocumentWriter {

    private final HrefConverter linkResolver;

    public RstDocumentWriter(HrefConverter linkResolver) {
        this.linkResolver = linkResolver;
    }

    @Override
    public void doVisitAnchor(String href, Map<String, String> attributes, String text) {
        if (href.startsWith("#")) {
            // ссылка на якорь в документе
            getTextBuilder().addChild(new Ref(href.substring(1), HtmlUtils.unescapeBrackets(text)));
        } else if (href.startsWith("@")) {
            getTextBuilder().addChild(new JavaRef(href.substring(1), HtmlUtils.unescapeBrackets(text)));
        } else {
            super.doVisitAnchor(href, attributes, text);
        }
    }

    @Override
    protected void doVisitLink(String href, Map<String, String> attributes, String text) {
        getTextBuilder().addChild(linkResolver.resolve(href, attributes, text));
    }

    @Override
    public void visitCode(String text) {
        if (text != null && text.startsWith(":java:")) {
            getTextBuilder().text(escapeUnderlines(unescapeLtRtAmpBSlash(text)));
        } else {
            super.visitCode(text);
        }

    }
}
