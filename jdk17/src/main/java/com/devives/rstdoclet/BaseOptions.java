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
package com.devives.rstdoclet;

import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BaseOptions extends jdk.javadoc.internal.doclets.toolkit.BaseOptions {

    protected BaseOptions(BaseConfiguration config) {
        super(config);
    }

    //
//    /**
//     * Argument for command-line option {@code -docfilessubdirs}.
//     * Destination directory name, in which doclet will copy the doc-files to.
//     */
//    String docFileDestDirName() {
//        try {
//            Method method = jdk.javadoc.internal.doclets.toolkit.BaseOptions.class.getDeclaredMethod("docFileDestDirName");
//            method.invoke(this, )
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
    String destDirName() {
        try {
            Field field = jdk.javadoc.internal.doclets.toolkit.BaseOptions.class.getDeclaredField("destDirName");
            field.setAccessible(true);
            return (String) field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Argument for hidden command-line option {@code --dump-on-error}.
     */
    boolean dumpOnError() {
        try {
            Field field = jdk.javadoc.internal.doclets.toolkit.BaseOptions.class.getDeclaredField("dumpOnError");
            field.setAccessible(true);
            return (boolean) field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
