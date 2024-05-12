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
package com.devives.rstdoclet.rst.builder;

import com.devives.rst.builder.RstElementBuilderAbst;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.document.inline.Role;
import com.devives.sphinx.rst.Rst4Sphinx;

public abstract class JavaRoleBuilderAbst<
        PARENT extends RstNodeBuilder<?, ?, ?, ?>,
        SELF extends JavaRoleBuilderAbst<PARENT, SELF>>
        extends RstElementBuilderAbst<PARENT, Role, SELF> {

    protected String text_;

    @Override
    protected Role createRstElement() {
        return Rst4Sphinx.elements().role(formatName(), formatTarget(), formatText());
    }

    protected abstract String formatName();

    protected abstract String formatTarget();

    protected String formatText() {
        return text_;
    }

    public SELF setText(String label) {
        text_ = label;
        return (SELF) this;
    }
}
