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

/**
 * Sample enumeration.
 */
public class SampleRootClass {
    /**
     * Private inner class.
     */
    private static class InnerClass1 {

    }

    /**
     * Protected inner class.
     */
    protected static class InnerClass2 {

    }

    /**
     * Package protected inner class.
     */
    static class InnerClass3 {

    }

    /**
     * Public inner class.
     */
    public static class InnerClass4 {

    }

    /**
     * Public inner class.
     */
    public static class InnerClass5 {

        /**
         * Public inner inner class.
         */
        public static class InnerInnerClass5 {

        }
    }

    /**
     * Public inner interface.
     */
    public interface InnerInterface {
        /**
         * Public inner inner class.
         */
        public class InnerInnerClass5 {

        }
    }
}
