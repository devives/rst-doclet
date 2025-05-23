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
package com.devives.rstdoclet.tools.javac.tree;

import com.sun.tools.javac.tree.DocPretty;

import java.io.IOException;
import java.io.Writer;

public class DocPrettyNonEscaped extends DocPretty {
    private final Writer out;

    public DocPrettyNonEscaped(Writer out) {
        super(out);
        this.out = out;
    }

    @Override
    protected void print(Object s) throws IOException {
        out.write(s.toString());
    }
}
