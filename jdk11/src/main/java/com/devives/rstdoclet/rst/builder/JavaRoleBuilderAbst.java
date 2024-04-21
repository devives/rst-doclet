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
package com.devives.rstdoclet.rst.builder;

import com.devives.rst.Rst;
import com.devives.rst.builder.RstElementBuilderAbst;
import com.devives.rst.builder.RstNodeBuilder;
import com.devives.rst.document.inline.Role;

public abstract class JavaRoleBuilderAbst<
        PARENT extends RstNodeBuilder<?, ?, ?, ?>,
        SELF extends JavaRoleBuilderAbst<PARENT, SELF>>
        extends RstElementBuilderAbst<PARENT, Role, SELF> {

    protected String text_;

    @Override
    protected Role createRstElement() {
        return Rst.elements().role(formatName(), formatTarget(), formatText());
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
