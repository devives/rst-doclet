package com.devives.html2rst;

import com.devives.rst.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RstUtils {

    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("(\\w*)_(\\W|$)");

    public static String escapeEndingUnderlines(String text) {
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

    public static String escapeRstEmphasis(String text) {
        return text.replaceAll("\\\\", "\\\\\\\\");
    }

}
