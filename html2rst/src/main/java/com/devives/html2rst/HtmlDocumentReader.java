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

import com.devives.rst.util.Constants;
import com.devives.rst.util.StringUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.devives.rst.util.StringUtils.stripStart;

public class HtmlDocumentReader implements Constants {

    private static final Pattern whitespaceWithNewline = Pattern.compile("[\\s\\r?\\n]+");
    /**
     * https://stackoverflow.com/questions/3445326/regex-in-java-how-to-deal-with-newline
     */
    private static final Pattern whitespace = Pattern.compile("[^\\S\\r?\\n]+");
    private final Document document_;

    public HtmlDocumentReader(Document document) {
        document_ = document;
    }

    public void accept(HtmlVisitor visitor) {
        visitor.beginBody();
        acceptChildren(document_.childNodes(), visitor);
        visitor.endBody();
    }

    private void acceptChildren(List<Node> children, HtmlVisitor visitor) {
        int num = 0;
        for (Node c : children) {
            acceptNode(c, visitor, num++);
        }
    }

    private final static String NL_SPACE = NL + SPACE;
    private final static String SPACE_NL = SPACE + NL;


    private void acceptNode(Node node, HtmlVisitor visitor, int num) {
        String nodeName = node.nodeName();
        switch (nodeName) {
            case "html":
            case "body":
                acceptChildren(node.childNodes(), visitor);
                break;
            case "head":
                break;
            case "b":
            case "strong":
            case "i":
            case "em":
            case "tt":
            case "sub":
            case "sup":
                visitor.beginEmphasis(nodeName);
                acceptChildren(node.childNodes(), visitor);
                visitor.endEmphasis(nodeName);
                break;
            case "h1":
            case "h2":
            case "h3":
            case "h4":
            case "h5":
            case "h6":
                visitor.beginHeader(nodeName);
                acceptChildren(node.childNodes(), visitor);
                visitor.endHeader(nodeName);
                break;
            case "code":
                visitor.visitCode(getInnerText(node));
                break;
            case "hr":
                visitor.visitHorizontalRule();
                break;
            case "br":
                visitor.visitBreakLine();
                break;
            case "p":
                visitor.beginParagraph();
                acceptChildren(node.childNodes(), visitor);
                visitor.endParagraph();
                break;
            case "blockquote":
                visitor.beginBlockQuote();
                acceptChildren(node.childNodes(), visitor);
                visitor.endBlockQuote();
                break;
            case "pre":
                visitor.beginPreformatted();
                acceptChildren(node.childNodes(), visitor);
                visitor.endPreformatted();
                break;
            case "#text":
                TextNode textNode = (TextNode) node;
                String parentNodeName = textNode.parentNode() != null ? textNode.parentNode().nodeName() : null;
                if (!("ul".equals(parentNodeName) || "ol".equals(parentNodeName))) {
                    //if ("body".equals(parentNodeName)) {
                    //visitor.visitText(stripStart(textNode.outerHtml(), "\r\n"));
                    String text = textNode.getWholeText();
                    text = text.replaceAll(NL_SPACE, NL);
                    if (num == 0 && !"pre".equals(parentNodeName)) {
                        text = stripStart(text, SPACE);
                    }
                    visitor.visitText(text);
                }
                break;
            case "table":
                acceptTable(node, visitor);
                break;
            case "dl":
                acceptDefinitionList(node, visitor);
                break;
            case "a":
                String textContent = _compress_whitespace(getInnerText(node).trim(), " ", true);
                visitor.visitAnchor(
                        node.attributes().asList().stream()
                                .collect(Collectors.toMap(Attribute::getKey, Attribute::getValue)),
                        textContent);
                break;
            case "ol":
                visitor.beginOrderedList();
                acceptChildren(node.childNodes(), visitor);
                visitor.endOrderedList();
                break;
            case "ul":
                visitor.beginUnorderedList();
                acceptChildren(node.childNodes(), visitor);
                visitor.endUnorderedList();
                break;
            case "li":
                visitor.beginListItem();
                acceptChildren(node.childNodes(), visitor);
                visitor.endListItem();
                break;
            default:
                visitor.visitNode("Unprocessed: '" + nodeName + "'");
        }
    }

    public String _compress_whitespace(String s, String replace, boolean newlines) {
        if (newlines) {
            return whitespaceWithNewline.matcher(s).replaceAll(replace);
        } else {
            return whitespace.matcher(s).replaceAll(replace);
        }
    }

    private void acceptTable(Node tableNode, HtmlVisitor visitor) {
        visitor.beginTable();
        for (int i = 0; i < tableNode.childNodeSize(); i++) {
            Node childNode = tableNode.childNode(i);
            if ("thead".equals(childNode.nodeName())) {
                acceptTableHeadOrBody(childNode, visitor);
            } else if ("tbody".equals(childNode.nodeName())) {
                acceptTableHeadOrBody(childNode, visitor);
            } else if ("tr".equals(childNode.nodeName())) {
                acceptTableRow(childNode, visitor);
            }
        }
        visitor.endTable();
    }

    private void acceptDefinitionList(Node tableNode, HtmlVisitor visitor) {
        visitor.beginDefinitionList();
        for (int i = 0; i < tableNode.childNodeSize(); i++) {
            Node childNode = tableNode.childNode(i);
            if ("dt".equals(childNode.nodeName())) {
                visitor.beginDefinitionTerm();
                acceptChildren(childNode.childNodes(), visitor);
                visitor.endDefinitionTerm();
                //acceptDefinitionTerm(childNode, visitor);
            } else if ("dd".equals(childNode.nodeName())) {
                visitor.beginDefinitionDescription();
                acceptChildren(childNode.childNodes(), visitor);
                visitor.endDefinitionDescription();
                //acceptDefinitionDescription(childNode, visitor);
            }
        }
        visitor.endDefinitionList();
    }

    private void acceptDefinitionTerm(Node childNode, HtmlVisitor visitor) {
        visitor.beginTableRow();
        for (Node c : childNode.childNodes()) {
            String cellType = c.nodeName();
            if (cellType.equals("dl")) {
                visitor.beginDefinitionTerm();
                acceptChildren(c.childNodes(), visitor);
                visitor.endDefinitionTerm();
            } else if (cellType.equals("dd")) {
                visitor.beginDefinitionDescription();
                acceptChildren(c.childNodes(), visitor);
                visitor.endDefinitionDescription();
            }
        }
        visitor.endTableRow();
    }

    private void acceptDefinitionDescription(Node childNode, HtmlVisitor visitor) {

    }

    private void acceptTableHeadOrBody(Node headOrBody, HtmlVisitor visitor) {
        for (Node trNode : headOrBody.childNodes()) {
            if ("tr".equals(trNode.nodeName())) {
                acceptTableRow(trNode, visitor);
            }
        }
    }

    private void acceptTableRow(Node trNode, HtmlVisitor visitor) {
        visitor.beginTableRow();
        for (Node c : trNode.childNodes()) {
            String cellType = c.nodeName();
            if (!cellType.equals("td") && !cellType.equals("th")) {
                continue;
            }
            int rowspan = c.hasAttr("rowspan")
                    ? Integer.parseInt(StringUtils.getNullOrEmptyDef(c.attr("rowspan"), "0"))
                    : 0;
            int colspan = c.hasAttr("rowspan")
                    ? Integer.parseInt(StringUtils.getNullOrEmptyDef(c.attr("colspan"), "0"))
                    : 0;

            if (cellType.equals("th")) {
                visitor.visitTableHeader(getInnerText(c));
            } else {
                visitor.beginTableData(rowspan, colspan);
                acceptChildren(c.childNodes(), visitor);
                visitor.endTableData();
            }
        }
        visitor.endTableRow();
    }

    private String getInnerText(Node node) {
        final StringBuilder sb = new StringBuilder();
        node.forEachNode(cNode -> {
            if ("#text".equals(cNode.nodeName())) {
                sb.append(((TextNode) cNode).getWholeText());
            }
        });
        return sb.toString();
        //return node.childNodes().stream().map(Node::outerHtml).collect(Collectors.joining());
    }

}
