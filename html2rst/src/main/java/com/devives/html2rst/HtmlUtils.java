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

import com.devives.rst.document.inline.InlineElement;
import com.devives.rst.document.inline.Link;
import com.devives.rst.document.inline.Role;
import com.devives.rst.util.StringUtils;
import com.devives.sphinx.rst.Rst4Sphinx;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class HtmlUtils {

    public static String unescapeLtRtAmpBSlash(String htmlContent) {
        return htmlContent
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&#8203;", " ")
                .replaceAll("&bslash;", "\\\\")
                ;
        //.replaceAll("\\u00a0", " ") // &NBSP
    }


    public static String unescapeBrackets(String text) {
        return (text == null || text.isEmpty())
                ? text
                : unescapeLtRtAmpBSlash(text);
    }

    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("(\\w*)_(\\W)");

    public static String escapeUnderlines(String text) {
        if (StringUtils.isNullOrEmpty(text)) {
            return text;
        } else {
            Matcher m = UNDERLINE_PATTERN.matcher(text);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                m.appendReplacement(sb, "$1" + Matcher.quoteReplacement("\\_") + "$2");
            }
            m.appendTail(sb);
            return sb.toString();
        }
    }

    /**
     * Returns a String with escaped special JavaScript characters.
     *
     * @param s String that needs to be escaped
     * @return a valid escaped JavaScript string
     */
    private static String escapeJavaScriptChars(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    sb.append("\\\'");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if (ch < 32 || ch >= 127) {
                        sb.append(String.format("\\u%04X", (int) ch));
                    } else {
                        sb.append(ch);
                    }
                    break;
            }
        }
        return sb.toString();
    }


    public static String escapeHTML(String str) {
        return str.codePoints().mapToObj(c ->
                        (c > 127 || "\"'<>&".indexOf(c) != -1)
                                ? "&#" + c + ";"
                                : new String(Character.toChars(c)))
                .collect(Collectors.joining());
    }

//    private static DocumentBuilder builder = null;
//
//    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
//        if (builder == null) {
//            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        }
//        return builder;
//    }

    public static org.jsoup.nodes.Element parseAnchorTag(String anchorHtmlTag) {
        try {
            return (Element) Jsoup.parseBodyFragment(anchorHtmlTag).body().childNode(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public static Element parseAnchorTag(String anchorHtmlTag) {
//        try {
//            //Document document = getDocumentBuilder().parse(new ByteArrayInputStream(anchorHtmlTag.getBytes(StandardCharsets.UTF_8)));
//            return (Element) document.getFirstChild();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static final Pattern unescapePattern = Pattern.compile("&#(d?);");

    /**
     * Преобразует Unicode-последовательности в текст.
     *
     * @param htmlText html-текст с unicode-последовательностями.
     * @return Строка без unicode-последовательностей.
     */
    public String unescapeHTML(String htmlText) {
        Matcher matcher = unescapePattern.matcher(htmlText);
        HashMap<String, String> map = new HashMap<>();
        while (matcher.find()) {
            String tag = matcher.group();
            String content = matcher.group(2);
            String escapedContent = String.valueOf((char) Integer.parseInt(content));
            String tagReplace = tag.replace(content, escapedContent);
            map.put(tag, tagReplace);
        }
        String escapedHtmlText = htmlText;
        for (Map.Entry<String, String> kv : map.entrySet()) {
            escapedHtmlText = escapedHtmlText.replace(kv.getKey(), kv.getValue());
        }
        return escapedHtmlText;
    }


    private static final Pattern A_TAG_PATTERN = Pattern.compile("<a .+?>(.+?)</a>");
    private static final Pattern A_TAG_PATTERN_QUOTED = Pattern.compile("(.*)<a .+?>(.+?)</a>(.*)");

    public static String removeATags(String text) {
        Matcher m = A_TAG_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "$1");
        }
        m.appendTail(sb);
        return sb.toString();
        // java11
        // return A_TAG_PATTERN.matcher(text).replaceAll((m) -> m.group(1));
    }

    private static final Pattern CODE_TAG_PATTERN = Pattern.compile("<code>([\\w\\W\\s\\n]+?)</code>");
    //private static final Pattern CODE_TAG_PATTERN = Pattern.compile("&lt;code&gt;(.+?)&lt;/code&gt;");
    public static String removeCodeTags(String text) {
        Matcher m = CODE_TAG_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "$1");
        }
        m.appendTail(sb);
        return sb.toString();
        // java11
        // return CODE_TAG_PATTERN.matcher(text).replaceAll((m) -> m.group(1));
    }

    public static String extractAText(String text) {
        return A_TAG_PATTERN.matcher(text).group(1);
    }

    public static String extractATextOrElse(String text, Supplier<String> getter) {
        Matcher matcher = A_TAG_PATTERN_QUOTED.matcher(text);
        boolean matches = matcher.matches();
        return matches
                ? matcher.group(1) + matcher.group(2) + matcher.group(3)
                : getter.get();
    }


    /**
     * <a href="http://google.ru"/>
     * <a href="http://google.ru"></a>
     * <a href="http://google.ru">Google</a>
     */
    private static final Pattern PATTERN_A_HREF = Pattern.compile("href\\s*=\\s*\\\"(.*?)\\\"");
    private static final Pattern PATTERN_A_TEXT = Pattern.compile("<\\s*a\\s+.*?>(.*?)</\\s*a\\s*>");

    /**
     * @param a "<a href="url">Text</a>" tag.
     * @return Instance of {@link Link}
     */
    public static InlineElement hrefToLink(String a) {
        Matcher hrefMatcher = PATTERN_A_HREF.matcher(a);
        Matcher textMatcher = PATTERN_A_TEXT.matcher(a);

        String href = hrefMatcher.find() ? hrefMatcher.group(1) : a;
        String text = textMatcher.find() ? textMatcher.group(1) : null;

        return hrefToLink(href, text);
    }

    public static InlineElement hrefToLink(HtmlTag htmlTag) {
        String href = htmlTag.getAttributes().get("href");
        String text = htmlTag.getText();
        return hrefToLink(href, text);
    }

    public static InlineElement hrefToLink(String href, String text) {
        String relativeLinkLowerCase = href.toLowerCase(Locale.US);
        if (relativeLinkLowerCase.startsWith("mailto:") ||
                relativeLinkLowerCase.startsWith("http:") ||
                relativeLinkLowerCase.startsWith("https:") ||
                relativeLinkLowerCase.startsWith("file:") ||
                relativeLinkLowerCase.contains(".html")) {
            return Rst4Sphinx.elements().anonymousLink(href, StringUtils.findFirstNotNullOrEmpty(text, href));
        } else if (relativeLinkLowerCase.startsWith("#")) {
            return Rst4Sphinx.elements().link(href.substring(1), StringUtils.findFirstNotNullOrEmpty(text, href.substring(1)));
        } else {
            return new Role("ref", href, StringUtils.findFirstNotNullOrEmpty(text, href));
        }
    }

}
