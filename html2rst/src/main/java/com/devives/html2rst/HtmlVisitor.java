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
package com.devives.html2rst;

import java.util.Map;

public interface HtmlVisitor {
    void beginBody();

    void endBody();

    void beginParagraph();

    void endParagraph();

    void visitText(String text);

    void visitBreakLine();

    void visitNode(String outerHtml);

    void beginPreformatted();

    void endPreformatted();

    void beginBlockQuote();

    void endBlockQuote();

    void visitAnchor(Map<String, String> attributes, String text);

    void beginOrderedList();

    void endOrderedList();

    void beginUnorderedList();

    void endUnorderedList();

    void beginListItem();

    void endListItem();

    void beginHeader(String tag);

    void endHeader(String tag);

    void beginEmphasis(String tag);

    void endEmphasis(String tag);

    void visitHorizontalRule();

    void visitCode(String text);

    void beginTable();

    void endTable();

    void beginTableRow();

    void endTableRow();

    void visitTableHeader(String text);

    void beginTableData(int rowspan, int colspan);

    void endTableData();

    void beginDefinitionList();

    void endDefinitionList();

    void beginDefinitionTerm();

    void endDefinitionTerm();

    void beginDefinitionDescription();

    void endDefinitionDescription();
}
