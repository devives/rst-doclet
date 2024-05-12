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

import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;

import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Set;

/**
 * Retrieve and format messages stored in multiple a resource files.
 */
public class CompositeMessageRetriever extends MessageRetriever {

    private final MessageRetriever messageRetriever_;
    private final Set<String> filteredMessages_;

    /**
     * Initialize the ResourceBundle with the given resource.
     *
     * @param messageRetriever parent
     * @param configuration    the configuration
     * @param resourceLocation resources.
     */
    public CompositeMessageRetriever(MessageRetriever messageRetriever, Configuration configuration, String resourceLocation) {
        super(configuration, resourceLocation);
        messageRetriever_ = Objects.requireNonNull(messageRetriever);
        filteredMessages_ = new HashSet<String>() {{
            add("doclet.Toolkit_Usage_Violation");
        }};
    }

    @Override
    public String getText(String key, Object... args) throws MissingResourceException {
        try {
            return messageRetriever_.getText(key, args);
        } catch (MissingResourceException ex) {
            return super.getText(key, args);
        }
    }

    @Override
    public void error(SourcePosition pos, String key, Object... args) {
        if (filteredMessages_.contains(key)) {
            return;
        }
        super.error(pos, key, args);
    }

    @Override
    public void error(String key, Object... args) {
        if (filteredMessages_.contains(key)) {
            return;
        }
        super.error(key, args);
    }

    @Override
    public void warning(SourcePosition pos, String key, Object... args) {
        if (filteredMessages_.contains(key)) {
            return;
        }
        super.warning(pos, key, args);
    }

    @Override
    public void warning(String key, Object... args) {
        if (filteredMessages_.contains(key)) {
            return;
        }
        super.warning(key, args);
    }

    @Override
    public void notice(SourcePosition pos, String key, Object... args) {
        if (filteredMessages_.contains(key)) {
            return;
        }
        super.notice(pos, key, args);
    }

    @Override
    public void notice(String key, Object... args) {
        if (filteredMessages_.contains(key)) {
            return;
        }
        super.notice(key, args);
    }
}
