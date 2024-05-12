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
package com.devives.rstdoclet.html2rst;

import com.devives.rstdoclet.util.ImportsCollectorImpl;
import com.sun.javadoc.ClassDoc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DocUtils {

    public static String formatAnnotations(ClassDoc classDoc) {
        String result = "";
        if (classDoc.annotations().length > 0) {
            result = Arrays.stream(classDoc.annotations())
                    .filter(an -> !(an.annotationType().qualifiedTypeName().startsWith("java.lang.annotation.")))
                    .map(a -> "@" + a.annotationType().typeName())
                    .collect(Collectors.joining(" "));
        }
        return result;
    }

    public static String formatTypeName(com.sun.javadoc.Type type) {
        String result = type.toString();
        if (!type.isPrimitive()) {
            Map<String, ClassDoc> refClasses = new HashMap<>();
            refClasses.put(type.qualifiedTypeName(), type.asClassDoc());
            refClasses.putAll(new ImportsCollectorImpl().collect(type.asClassDoc()).getImportsMap());
            for (Map.Entry<String, ClassDoc> entry : refClasses.entrySet()) {
                result = result.replace(entry.getKey(), entry.getValue().typeName());
            }
        }
        return result;
    }

}
