package com.devives.html2rst;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.Map;
import java.util.stream.Collectors;

public class HtmlTagFactory {

    public static HtmlTag create(Element element) {
        return create(
                element.normalName(),
                element.attributes().asList().stream().collect(Collectors.toMap(Attribute::getKey, Attribute::getValue)),
                element.text(),
                element.html());
    }

    public static HtmlTag create(String name, Map<String, String> attributes, String text) {
        return new HtmlTagImpl(
                name,
                attributes,
                text,
                text);
    }

    public static HtmlTag create(String name, Map<String, String> attributes, String text, String html) {
        return new HtmlTagImpl(
                name,
                attributes,
                text,
                html);
    }

}
