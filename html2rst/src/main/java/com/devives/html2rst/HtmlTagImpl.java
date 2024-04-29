package com.devives.html2rst;

import com.devives.rst.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HtmlTagImpl implements HtmlTag {

    private final String name;
    private final Map<String, String> attributes;
    private final String text;
    private final String html;
    private final String formatted;

    public HtmlTagImpl(String name, Map<String, String> attributes, String text, String html) {
        this.name = StringUtils.requireNotNullOrEmpty(name, "name");
        this.attributes = Collections.unmodifiableMap(attributes != null ? attributes : Collections.emptyMap());
        this.text = StringUtils.findFirstNotNullOrEmpty(text, "");
        this.html = StringUtils.findFirstNotNullOrEmpty(html, "");
        this.formatted = format();
    }

    private String format() {
        String sAttributes = this.attributes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> String.format("%s=\"%s\"", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));
        return String.format("<%s%s>%s</%s>", name, StringUtils.isNullOrEmpty(sAttributes) ? "" : " " + sAttributes, text, name);
    }

    @Override
    public String getName() {
        return name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String getText() {
        return text;
    }

    public String getHtml() {
        return html;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(formatted, ((HtmlTagImpl) o).formatted);
    }

    @Override
    public int hashCode() {
        return formatted.hashCode();
    }

    @Override
    public String toString() {
        return formatted;
    }
}
