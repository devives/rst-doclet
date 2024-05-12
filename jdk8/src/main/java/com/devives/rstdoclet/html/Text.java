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

import com.devives.html2rst.HtmlUtils;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;

import java.io.IOException;
import java.io.Writer;

/**
 * Class for containing immutable string content for HTML tags of javadoc output.
 */
public class Text extends Content {
    private final String string;

    /**
     * Creates a new object containing immutable text.
     *
     * @param content the text content
     * @return the object
     */
    public static Text of(CharSequence content) {
        return new Text(content);
    }

    /**
     * Constructs an immutable text object.
     *
     * @param content content for the object
     */
    private Text(CharSequence content) {
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
    public void addContent(String stringContent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean write(Writer out, boolean atNewline) throws IOException {
        out.write(string);
        return string.endsWith(DocletConstants.NL);
    }

}
