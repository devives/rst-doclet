/**
 * RstDoclet for JavaDoc Tool, generating reStructuredText for Sphinx.
 * Copyright (C) 2023-2024 Vladimir Ivanov <ivvlev@devives.com>.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.devives.rstdoclet.html2rst;

import com.devives.html2rst.HtmlTagFactory;
import com.devives.html2rst.HtmlUtils;
import com.devives.rst.document.inline.InlineElement;
import com.devives.rst.util.StringUtils;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.devives.sphinx.rst.Rst4Sphinx;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;

public class HrefConverterImpl implements HrefConverter {

    private final RstGeneratorContext docContext_;
    private final String packageIndexFileName_;
    private final String packageSummaryFileName_ = "package-summary.html";

    public HrefConverterImpl(RstGeneratorContext docContext) {
        this.docContext_ = docContext;
        this.packageIndexFileName_ = docContext_.getRstConfiguration().packageIndexFileName + ".html";
    }

    private static String endWith(String text, String ending) {
        if (text != null && !text.endsWith(ending)) {
            text += ending;
        }
        return text;
    }


    @Override
    public InlineElement resolve(String href, Map<String, String> attributes, String text) {
        if (StringUtils.isNullOrEmpty(href)) {
            Rst4Sphinx.elements().text(text);
        }

        URI hrefUri = URI.create(href);
        if (hrefUri.getScheme() != null) {
            // Any external link
        } else if (href.startsWith("#")) {
            // Link to the class member or internal document anchor or header.
        } else if (href.equals(packageSummaryFileName_)) {
            // Link to the package index.
            return Rst4Sphinx.elements().anonymousLink(packageIndexFileName_, text);
        } else if (href.endsWith(packageSummaryFileName_)) {
            String path = Paths.get(docContext_.getHtmlDocletWriter().path.getPath()).getParent().toString().replace("\\", "/");
            String pathToRoot = endWith(docContext_.getHtmlDocletWriter().pathToRoot.getPath().replace("\\", "/"), "/");
            URI pathUri = URI.create(pathToRoot).resolve(path);
            if (href.startsWith(pathUri.toString())) {
                return Rst4Sphinx.elements().anonymousLink(packageIndexFileName_, text);
            }
        } else if (href.equals(packageIndexFileName_)) {
            // Link to the package index.
            return Rst4Sphinx.elements().anonymousLink(packageIndexFileName_, text);
        } else if (href.endsWith(packageIndexFileName_)) {
            String path = Paths.get(docContext_.getHtmlDocletWriter().path.getPath()).getParent().toString().replace("\\", "/");
            String pathToRoot = endWith(docContext_.getHtmlDocletWriter().pathToRoot.getPath().replace("\\", "/"), "/");
            URI pathUri = URI.create(pathToRoot).resolve(path);
            if (href.startsWith(pathUri.toString())) {
                return Rst4Sphinx.elements().anonymousLink(packageIndexFileName_, text);
            }
        }
        return HtmlUtils.hrefToLink(HtmlTagFactory.create("a", attributes, text));
    }

}
