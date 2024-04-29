package com.devives.html2rst;

import java.util.Map;

public interface HtmlTag {

    String getName();

    Map<String, String> getAttributes();

    String getText();

    String getHtml();
}
