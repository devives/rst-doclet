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
package com.devives.rstdoclet.rst;

import com.devives.rstdoclet.RstConfigurationImpl;
import com.devives.rstdoclet.html.HtmlDocletWriter;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;

public class RstGeneratorContextImpl implements RstGeneratorContext {

    private final RstConfigurationImpl rstConfiguration_;
    private final HtmlDocletWriter htmlDocletWriter_;

    public RstGeneratorContextImpl(RstConfigurationImpl rstConfiguration, HtmlDocletWriter htmlDocletWriter) {
        rstConfiguration_ = rstConfiguration;
        htmlDocletWriter_ = htmlDocletWriter;
    }

    @Override
    public RstConfigurationImpl getRstConfiguration() {
        return rstConfiguration_;
    }

    @Override
    public HtmlDocletWriter getHtmlDocletWriter() {
        return htmlDocletWriter_;
    }

    @Override
    public ConfigurationImpl getHtmlConfiguration() {
        return htmlDocletWriter_.configuration;
    }
}
