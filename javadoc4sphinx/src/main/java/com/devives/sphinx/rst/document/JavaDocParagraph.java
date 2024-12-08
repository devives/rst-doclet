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
package com.devives.sphinx.rst.document;

import com.devives.rst.document.Paragraph;

public class JavaDocParagraph extends Paragraph {

    @Override
    protected void onCollectChildText(StringBuilder stringBuilder, String itemText) {
        if (stringBuilder.length() > 0 && !itemText.isEmpty()) {
            if (!(itemText.startsWith("\r\n") || itemText.startsWith("\n") || itemText.startsWith(SPACE))) {
                final String ending = stringBuilder.substring(Math.min(stringBuilder.length(), Math.max(2, stringBuilder.length() - NL.length())));
                if (ending.endsWith("*") || ending.endsWith("`") || ending.endsWith("`_")) {
                    stringBuilder.append(HIDDEN_SPACE);
                }
            }
        }
        super.onCollectChildText(stringBuilder, itemText);
    }

    @Override
    protected String serializeElements() {
        String text = super.serializeElements();
        // Trim will remove NLs before and after paragraph text. NL will append to the end in Paragraph#serialize()
        return text.trim();
    }

}
