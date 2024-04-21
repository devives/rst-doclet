/**
 * RstDoclet for JavaDoc Tool, generating reStructuredText for Sphinx.
 * Copyright (C) 2023-2024 Vladimir Ivanov <ivvlev@devives.com>.
 * <p>
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation..
 * <p>
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.devives.html2rst;

import com.devives.rst.Rst;
import com.devives.rst.builder.*;
import com.devives.rst.builder.directive.ParsedLiteralBlockBuilder;
import com.devives.rst.builder.inline.InlineBuilders;
import com.devives.rst.builder.list.BulletListBuilder;
import com.devives.rst.builder.list.EnumeratedListBuilder;
import com.devives.rst.builder.list.ListBuilder;
import com.devives.rst.builder.list.ListItemBuilder;
import com.devives.rst.builder.table.GridTableBuilder;
import com.devives.rst.builder.table.TableCellBuilder;
import com.devives.rst.builder.table.TableRowBuilder;
import com.devives.rst.document.RstDocument;
import com.devives.rst.document.RstElement;
import com.devives.rst.document.RstNode;
import com.devives.rst.util.StringUtils;

import java.util.Stack;

import static com.devives.html2rst.HtmlUtils.escapeUnderlines;
import static com.devives.html2rst.HtmlUtils.unescapeLtRtAmpBSlash;
import static com.devives.rst.util.StringUtils.*;

public class RstDocumentWriter implements HtmlVisitor {
    private final RstDocumentBuilder<?> docBuilder_ = Rst.builders().document();
    private RstElementBuilder<?, ?, ?> curBuilder_;
    private final Stack<String> emphasisStack = new Stack<>();
    private final Stack<String> headerStack = new Stack<>();

    public RstDocument getDocument() {
        endUntil(RstDocumentBuilder.class);
        return docBuilder_.build();
    }

    @Override
    public void beginBody() {
        curBuilder_ = docBuilder_;
    }

    @Override
    public void endBody() {
        endUntil(RstDocumentBuilder.class);
    }

    @Override
    public void beginParagraph() {
        curBuilder_ = getBodyBuilder().beginParagraph();
    }

    @Override
    public void endParagraph() {
        if (curBuilder_ instanceof ParagraphBuilder && ((ParagraphBuilder<?>) curBuilder_).isEmpty()) {
            curBuilder_ = curBuilder_.getParentBuilder();
        } else {
            endUntil(ParagraphBuilder.class);
        }
    }

    @Override
    public void visitText(String text) {
        if (!strip(text, SPACE).isEmpty()) {
            appendText(getTextBuilder(), unescapeLtRtAmpBSlash(escapeRstEmphasis(text)));
        }
    }

    @Override
    public void visitBreakLine() {
        appendText(getTextBuilder(), NL);
    }

    private void appendText(InlineBuilders<?, ?, ?, ?> inlineBuilder, String text) {
        if (!headerStack.isEmpty()) {
            endUntil(RstDocumentBuilder.class);
            String tag = headerStack.peek();
            switch (tag) {
                case "h1":
                    docBuilder_.title(text, 1);
                    break;
                case "h2":
                    docBuilder_.title(text, 2);
                    break;
                case "h3":
                    docBuilder_.title(text, 3);
                    break;
                case "h4":
                    docBuilder_.title(text, 4);
                    break;
                case "h5":
                    docBuilder_.title(text, 5);
                    break;
                case "h6":
                    docBuilder_.title(text, 6);
                    break;
                case "hr":
                    // Transitions not allowed
                    break;
            }
        } else if (!emphasisStack.isEmpty()) {
            String tag = emphasisStack.peek();
            String textWOStart = stripStart(text, SPACE + NL);
            String textWOEnd = stripEnd(text, SPACE + NL);
            String texStart = text.substring(0, text.length() - textWOStart.length());
            String textEnd = text.substring(textWOEnd.length());
            text = text.trim();
            if (!texStart.isEmpty()) inlineBuilder.text(texStart);
            switch (tag) {
                case "b":
                case "strong":
                    inlineBuilder.bold(text);
                    break;
                case "i":
                case "em":
                    inlineBuilder.italic(text);
                    break;
                case "tt":
                    inlineBuilder.interpreted(text);
                    break;
                case "sub":
                case "sup":
                    inlineBuilder.role(tag, text);
                    break;
                default:
                    inlineBuilder.text(text);
                    break;
            }
            if (!textEnd.isEmpty()) inlineBuilder.text(textEnd);
        } else {
            inlineBuilder.text(text);
        }
    }

    @Override
    public void visitNode(String outerHtml) {
        getTextBuilder().lineBreak().text(outerHtml).lineBreak();
    }

    @Override
    public void beginEmphasis(String tag) {
        emphasisStack.push(tag);
    }

    @Override
    public void endEmphasis(String tag) {
        emphasisStack.pop();
    }

    /**
     * <a href="https://reflectoring.io/howto-format-code-snippets-in-javadoc/">...</a>
     */
    @Override
    public void beginPreformatted() {
        curBuilder_ = getBodyBuilder().beginParsedLiteralBlock();
    }

    @Override
    public void endPreformatted() {
        endUntil(ParsedLiteralBlockBuilder.class);
    }

    @Override
    public void beginBlockQuote() {
        curBuilder_ = getBodyBuilder().beginBlockQuote();
    }

    @Override
    public void endBlockQuote() {
        endUntil(BlockQuoteBuilder.class);
    }

    private <BUILDER extends RstNodeBuilder<?, ?, ?, ?>> boolean isIn(Class<BUILDER> builderClass) {
        RstElementBuilder<?, ?, ?> builder = curBuilder_;
        while (builder != docBuilder_) {
            if (builderClass.isInstance(builder)) {
                return true;
            }
            builder = builder.getParentBuilder();
        }
        return false;
    }

    @Override
    public void visitAnchor(String name, String label) {
        boolean isInParagraph = isIn(ParagraphBuilder.class);
        InlineBuilders<?, ?, ?, ?> inlineBuilder = getTextBuilder();
        if (isInParagraph) {
            getBodyBuilder(inlineBuilder).target(name);
            appendText(inlineBuilder, label);
        } else {
            getBodyBuilder(inlineBuilder).target(name);
            appendText(inlineBuilder, label);
        }
    }

    @Override
    public void visitLink(String href, String label) {
        getTextBuilder().anonymousLink(href, label);
    }

    @Override
    public void beginHeader(String tag) {
        endUntil(RstDocumentBuilder.class);
        headerStack.push(tag);
    }

    @Override
    public void endHeader(String tag) {
        String current = headerStack.pop();
    }

    @Override
    public void visitHorizontalRule() {
        curBuilder_ = getTextBuilder().lineBreak().lineBreak().end();
    }

    @Override
    public void visitCode(String text) {
        if (isIn(ParsedLiteralBlockBuilder.class)) {
            getTextBuilder().text(escapeUnderlines(unescapeLtRtAmpBSlash(text)));
        } else {
            // Literal can not have beginning or ending spaces.
            getTextBuilder().literal(escapeUnderlines(unescapeLtRtAmpBSlash(text)).trim());
        }
    }

    @Override
    public void beginOrderedList() {
        curBuilder_ = getBodyBuilder().beginNumberedList();
    }

    @Override
    public void endOrderedList() {
        endUntil(EnumeratedListBuilder.class);
    }

    @Override
    public void beginUnorderedList() {
        curBuilder_ = getBodyBuilder().beginBulletList();
    }

    @Override
    public void endUnorderedList() {
        endUntil(BulletListBuilder.class);
    }

    @Override
    public void beginListItem() {
        curBuilder_ = ((ListBuilder<?, ?, ?, ?>) curBuilder_).beginItem();
    }

    @Override
    public void endListItem() {
        endUntil(ListItemBuilder.class);
    }

    @Override
    public void beginTable() {
        curBuilder_ = getBodyBuilder().beginGridTable();
    }

    @Override
    public void endTable() {
        endUntil(GridTableBuilder.class);
    }

    @Override
    public void beginTableRow() {
        if (curBuilder_ instanceof GridTableBuilder) {
            curBuilder_ = ((GridTableBuilder) curBuilder_).beginRow();
        } else {
            throw new RuntimeException("Can't create a TableRow. The Table must be defined before.");
        }
    }

    @Override
    public void endTableRow() {
        endUntil(TableRowBuilder.class);
    }

    @Override
    public void visitTableHeader(String text) {
        if (curBuilder_ instanceof TableRowBuilder) {
            curBuilder_ = ((TableRowBuilder<?>) curBuilder_).head(cell -> cell.paragraph(text));
        } else {
            throw new RuntimeException("Can't create a TableCell. The TableRow must be defined before.");
        }
    }

    @Override
    public void beginTableData(int rowspan, int colspan) {
        if (curBuilder_ instanceof TableRowBuilder) {
            curBuilder_ = ((TableRowBuilder<?>) curBuilder_).beginCell();
        } else {
            throw new RuntimeException("Can't create a TableCell. The TableRow must be defined before.");
        }
    }

    @Override
    public void endTableData() {
        endUntil(TableCellBuilder.class);
    }

    @Override
    public void beginDefinitionList() {
        curBuilder_ = getBodyBuilder().beginGridTable();
    }

    @Override
    public void endDefinitionList() {
        endUntil(GridTableBuilder.class);
    }

    @Override
    public void beginDefinitionTerm() {
        if (curBuilder_ instanceof GridTableBuilder) {
            curBuilder_ = ((GridTableBuilder<?>) curBuilder_).beginRow();
            curBuilder_ = ((TableRowBuilder<?>) curBuilder_).beginCell();
        } else if (curBuilder_ instanceof TableRowBuilder) {
            curBuilder_ = ((TableRowBuilder<?>) curBuilder_).beginCell();
        } else {
            while (curBuilder_ != docBuilder_ && !(curBuilder_ instanceof GridTableBuilder)) {
                curBuilder_ = curBuilder_.end();
            }
            curBuilder_ = ((GridTableBuilder<?>) curBuilder_).beginRow();
            curBuilder_ = ((TableRowBuilder<?>) curBuilder_).beginCell();
        }
    }

    @Override
    public void endDefinitionTerm() {
        endUntil(TableCellBuilder.class);
    }

    @Override
    public void beginDefinitionDescription() {
        if (curBuilder_ instanceof GridTableBuilder) {
            curBuilder_ = ((GridTableBuilder<?>) curBuilder_).beginRow();
            curBuilder_ = ((TableRowBuilder<?>) curBuilder_).beginCell();
        } else if (curBuilder_ instanceof TableRowBuilder) {
            curBuilder_ = ((TableRowBuilder<?>) curBuilder_).beginCell();
        } else if (curBuilder_ instanceof TableCellBuilder) {
            // do nothing
        } else {
            while (curBuilder_ != docBuilder_ && !(curBuilder_ instanceof TableCellBuilder)) {
                curBuilder_ = curBuilder_.end();
            }
        }
    }

    @Override
    public void endDefinitionDescription() {
        //endUntil(TableCellBuilder.class);
    }

    protected <PARENT extends RstNodeBuilder<?, ?, ?, ?>,
            CHILD extends RstElement,
            RESULT extends RstNode<CHILD>,
            BUILDER extends InlineBuilders<PARENT, CHILD, RESULT, BUILDER>>
    BUILDER getTextBuilder() {
        if (curBuilder_ instanceof InlineBuilders) {
            return (BUILDER) curBuilder_;
        } else if (curBuilder_ instanceof BodyBuilders) {
            curBuilder_ = ((BodyBuilders<?, ?, ?, ?>) curBuilder_).beginParagraph();
        }
        return (BUILDER) curBuilder_;
    }

    protected <PARENT extends RstNodeBuilder<?, ?, ?, ?>,
            CHILD extends RstNode<CHILD>,
            RESULT extends RstNode<CHILD>,
            BUILDER extends BodyBuilders<PARENT, CHILD, RESULT, BUILDER>>
    BUILDER getBodyBuilder() {
        while (!(curBuilder_ instanceof BodyBuilders)) {
            curBuilder_ = curBuilder_.end();
        }
        return (BUILDER) curBuilder_;
    }

    protected <PARENT extends RstNodeBuilder<?, ?, ?, ?>,
            CHILD extends RstNode<CHILD>,
            RESULT extends RstNode<CHILD>,
            BUILDER extends BodyBuilders<PARENT, CHILD, RESULT, BUILDER>>
    BUILDER getBodyBuilder(InlineBuilders<?, ?, ?, ?> inlineBuilder) {
        RstElementBuilder<?, ?, ?> builder = inlineBuilder;
        while (!(builder instanceof BodyBuilders)) {
            builder = curBuilder_.getParentBuilder();
        }
        return (BUILDER) builder;
    }

    protected <BUILDER extends RstNodeBuilder<?, ?, ?, ?>> void endUntil(Class<BUILDER> builderClass) {
        boolean isParagraphClosed = false;
        while (curBuilder_ != docBuilder_ && !isParagraphClosed) {
            isParagraphClosed = builderClass.isInstance(curBuilder_);
            curBuilder_ = curBuilder_.end();
        }
    }


    public String _escape_inline(String s) {
        return "\\ " + s + "\\ ";
    }


    public String _directive(String directive, String body) {
        String header = "\n\n.. " + directive + "::\n\n";

        if (body != null) {
            return header + leftJustify(body, 3) + "\n\n";
        } else {
            return header + NL;
        }
    }

    private String leftJustify(String s, int indent) {
        String[] lines = s.split(NL);
        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            sb.append(StringUtils.buildString(' ', indent)).append(line).append(NL);
        }

        return sb.toString();
    }

    private String escapeRstEmphasis(String text) {
        if (text == null || text.isEmpty()) return text;
        if (text.trim().startsWith(".. ")) return text;
        text = text
                .replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\\*", "\\\\*")
        //.replaceAll("`", "\\\\`") // Проблема с :ref:`link`
        ;
        text = HtmlUtils.escapeUnderlines(text);
        return text;
    }

}
