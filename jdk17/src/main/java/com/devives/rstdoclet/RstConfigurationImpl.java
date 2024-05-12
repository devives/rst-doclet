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
package com.devives.rstdoclet;

import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

public class RstConfigurationImpl implements RstConfiguration {

    private final HtmlConfiguration htmlConfiguration_;
    private final RstOptions rstOptions_;
    public Utils utils;

    public RstConfigurationImpl(HtmlConfiguration htmlConfiguration) {
        htmlConfiguration_ = htmlConfiguration;
        rstOptions_ = new RstOptions(htmlConfiguration.getOptions(), htmlConfiguration);
    }

    public HtmlConfiguration getHtmlConfiguration() {
        return htmlConfiguration_;
    }

    RstOptions getOptions() {
        return rstOptions_;
    }

    @Override
    public String getPackageIndexFileName() {
        return getOptions().getPackageIndexFileName();
    }

    @Override
    public Utils utils() {
        return utils;
    }
}
