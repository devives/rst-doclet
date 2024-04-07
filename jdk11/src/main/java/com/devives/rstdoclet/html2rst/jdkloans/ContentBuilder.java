/*
 * Copyright (c) 2003, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.devives.rstdoclet.html2rst.jdkloans;

import jdk.javadoc.internal.doclets.toolkit.Content;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A sequence of Content nodes.
 */
public class ContentBuilder extends Content {
    protected List<Content> contents = Collections.emptyList();

    public ContentBuilder() { }

    public ContentBuilder(Content... contents) {
        for (Content c : contents) {
            addContent(c);
        }
    }

    @Override
    public void addContent(Content content) {
        nullCheck(content);
        ensureMutableContents();
        if (content instanceof ContentBuilder) {
            contents.addAll(((ContentBuilder) content).contents);
        } else
            contents.add(content);
    }

    @Override
    public void addContent(CharSequence text) {
        if (text.length() == 0)
            return;
        ensureMutableContents();
        Content c = contents.isEmpty() ? null : contents.get(contents.size() - 1);
        StringContent sc;
        if (c != null && c instanceof StringContent) {
            sc = (StringContent) c;
        } else {
            contents.add(sc = new StringContent());
        }
        sc.addContent(text);
    }

    @Override
    public boolean write(Writer writer, boolean atNewline) throws IOException {
        for (Content content: contents) {
            atNewline = content.write(writer, atNewline);
        }
        return atNewline;
    }

    @Override
    public boolean isEmpty() {
        for (Content content: contents) {
            if (!content.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public int charCount() {
        int n = 0;
        for (Content c : contents)
            n += c.charCount();
        return n;
    }

    private void ensureMutableContents() {
        if (contents.isEmpty())
            contents = new ArrayList<>();
    }
}