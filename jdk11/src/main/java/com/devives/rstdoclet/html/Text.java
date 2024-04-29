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

import com.devives.html2rst.HtmlUtils;
import jdk.javadoc.internal.doclets.formats.html.markup.RawHtml;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.DocletConstants;

import java.io.IOException;
import java.io.Writer;

/**
 * Class for containing immutable string content for HTML tags of javadoc output.
 */
public class Text extends Content {
    private final String string;

    /**
     * Constructs an immutable text object.
     *
     * @param content content for the object
     */
    public Text(CharSequence content) {
        string = HtmlUtils.escapeHTML(content.toString());
    }

    @Override
    public boolean isEmpty() {
        return string.isEmpty();
    }

    @Override
    public int charCount() {
        return new RawHtml(string).charCount();
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public void addContent(Content content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addContent(CharSequence stringContent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean write(Writer out, boolean atNewline) throws IOException {
        out.write(string);
        return string.endsWith(DocletConstants.NL);
    }

}
