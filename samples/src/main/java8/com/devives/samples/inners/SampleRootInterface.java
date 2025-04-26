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
package com.devives.samples.inners;

import com.devives.samples.SampleInterface;

/**
 * Sample interface.
 */
public interface SampleRootInterface<R> extends SampleInterface {

    /**
     * Some inner class.
     *
     * @since 0.1.0 Since description.
     * @version 0.1.0 Version description.
     * @author Author name adaress.
     * @deprecated Deprecated description.
     */
    class InnerClass {

    }

    /**
     * Inner interface.
     *
     * @since 0.1.0 Since description.
     * @version 0.1.0 Version description.
     * @author Author name adaress.
     * @deprecated Deprecated description.
     */
    interface InnerInterface {

    }
}
