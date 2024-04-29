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

import com.devives.html2rst.HtmlTagFactory;
import com.devives.html2rst.HtmlUtils;
import com.devives.rst.document.inline.InlineElement;
import com.devives.rst.util.StringUtils;
import com.devives.rstdoclet.rst.RstGeneratorContext;
import com.devives.sphinx.rst.Rst4Sphinx;
import com.sun.javadoc.Doc;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;

public class HrefConverterImpl implements HrefConverter {

    private final RstGeneratorContext docContext_;
    private final String packageIndexFileName_;
    private final String packageSummaryFileName_ = "package-summary.html";
    private final Doc doc_;

    public HrefConverterImpl(RstGeneratorContext docContext, Doc doc) {
        this.docContext_ = docContext;
        this.doc_ = doc;
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
