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
package com.devives.rstdoclet.html2rst;

import java.util.HashMap;
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
                .replaceAll("&bslash;", "\\\\")
                ;
        //.replaceAll("\\u00a0", " ") // &NBSP
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

    private static final Pattern unescapePattern = Pattern.compile("&#(d?);");

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

//    private static final Pattern A_TAG_PATTERN = Pattern.compile("<a .+?>(.+?)</a>");
//
//    public static String removeATags(String text) {
//        return A_TAG_PATTERN.matcher(text).replaceAll((m) -> m.group(1));
//    }
//
//    public static String extractAText(String text) {
//        return A_TAG_PATTERN.matcher(text).group(1);
//    }
//
//    public static String extractATextOrElse(String text, Supplier<String> getter) {
//        Matcher matcher = A_TAG_PATTERN.matcher(text);
//        boolean matches = matcher.matches();
//        int groupCount = matcher.groupCount();
//        return matches
//                ? ((groupCount < 2) ? matcher.group(1) : matcher.group(1) + matcher.group(2))
//                : getter.get();
//    }

    private static final Pattern A_TAG_PATTERN = Pattern.compile("<a .+?>(.+?)</a>");
    private static final Pattern A_TAG_PATTERN_QUOTED = Pattern.compile("(.*)<a .+?>(.+?)</a>(.*)");

    public static String removeATags(String text) {
        return A_TAG_PATTERN.matcher(text).replaceAll((m) -> m.group(1));
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

}
