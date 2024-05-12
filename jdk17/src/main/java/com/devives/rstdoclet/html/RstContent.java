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

import com.devives.rst.document.inline.InlineElement;
import jdk.javadoc.internal.doclets.formats.html.markup.Text;
import jdk.javadoc.internal.doclets.toolkit.Content;

import java.io.IOException;
import java.io.Writer;

public class RstContent extends Content {

    private final Content htmlContent_;
    private final InlineElement inlineElement_;

    public RstContent(InlineElement inlineElement, Content htmlContent) {
        inlineElement_ = inlineElement;
        htmlContent_ = htmlContent;
    }

    public Content getHtmlContent() {
        return htmlContent_;
    }

    public InlineElement getInlineElement() {
        return inlineElement_;
    }

    @Override
    public boolean write(Writer out, boolean atNewline) throws IOException {
        Content textContent = Text.of(inlineElement_.serialize());
        return textContent.write(out, atNewline);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
