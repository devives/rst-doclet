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
package com.devives.rstdoclet.util;

import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Retrieve and format messages stored in multiple a resource files.
 */
public class CompositeMessageRetriever extends MessageRetriever {

    private final MessageRetriever[] messageRetrievers_;
    private final String[] resourceLocations_;
    private int callCounter_ = 0;
    /**
     * The lazily fetched resource..
     */
    private ResourceBundle messageRB;

    /**
     * Initialize the ResourceBundle with the given resource.
     *
     * @param configuration     the configuration
     * @param resourceLocations resources.
     */
    public CompositeMessageRetriever(Configuration configuration, String... resourceLocations) {
        super(configuration, resourceLocations[0]);
        resourceLocations_ = resourceLocations;
        messageRetrievers_ = new MessageRetriever[resourceLocations.length];
        messageRetrievers_[0] = this;
        for (int i = 1; i < resourceLocations.length; i++) {
            messageRetrievers_[i] = new MessageRetriever(configuration, resourceLocations[i]);
        }
    }

    @Override
    public String getText(String key, Object... args) throws MissingResourceException {
        if (callCounter_ == 0) {
            callCounter_++;
            try {
                MissingResourceException missingResourceException = null;
                for (MessageRetriever messageRetriever : messageRetrievers_) {
                    try {
                        return messageRetriever.getText(key, args);
                    } catch (MissingResourceException ex) {
                        missingResourceException = ex;
                    }
                }
                throw missingResourceException;
            } finally {
                callCounter_--;
            }
        } else {
            if (messageRB == null) {
                try {
                    messageRB = ResourceBundle.getBundle(resourceLocations_[0]);
                } catch (MissingResourceException e) {
                    throw new Error("Fatal: Resource (" + resourceLocations_[0] +
                            ") for javadoc doclets is missing.", e);
                }
            }
            String message = messageRB.getString(key);
            return MessageFormat.format(message, args);
        }
    }
}
