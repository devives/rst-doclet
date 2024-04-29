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
