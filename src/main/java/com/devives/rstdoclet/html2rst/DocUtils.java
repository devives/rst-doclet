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
package com.devives.rstdoclet.html2rst;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.tools.javadoc.DocEnv;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DocUtils {

    public static String formatTypeSimpleName(com.sun.javadoc.Type type) {
        return doFormatTypeName(type, type::simpleTypeName);
    }

    public static String formatTypeName(com.sun.javadoc.Type type) {
        return doFormatTypeName(type, type::typeName);
    }

    private static String doFormatTypeName(com.sun.javadoc.Type type, Supplier<String> nameGetter) {
        String result = type.toString();
        if (!type.isPrimitive()) {
            Map<String, ClassDoc> refClasses = new HashMap<>();
            refClasses.put(type.qualifiedTypeName(), type.asClassDoc());
            refClasses.putAll(new ImportsCollector().collect(type.asClassDoc()).getImportsMap());
            for (Map.Entry<String, ClassDoc> entry : refClasses.entrySet()) {
                result = result.replace(entry.getKey(), entry.getValue().typeName());
            }
        }
        return result;
    }

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("(\\w+\\.)+(\\w+)");

    public static String formatReturnTypeName(com.sun.javadoc.Type type) {
        String result = type.typeName();
        if (type.isPrimitive()) {
            // do nothing
        } else if ("[]".equals(type.dimension())) {
            result += type.dimension();
        } else if (type.asParameterizedType() != null) {
            String typeArgs = Arrays.stream(type.asParameterizedType().typeArguments())
                    .map(DocUtils::formatTypeName)
                    .collect(Collectors.joining(", "));
            if (!typeArgs.isEmpty()) {
                result += "<" + typeArgs + ">";
            }
        }
        return result;
    }


    public static String formatTypeVariable(TypeVariable typeVariable) {
        String result = typeVariable.toString();
        if (!typeVariable.isPrimitive()) {
            Map<String, ClassDoc> refClasses = new HashMap<>();
            for (Type bound : typeVariable.bounds()) {
                refClasses.put(bound.qualifiedTypeName(), bound.asClassDoc());
            }
            for (Map.Entry<String, ClassDoc> entry : refClasses.entrySet()) {
                result = result.replaceAll(entry.getKey(), entry.getValue().typeName());
            }
        }
        return result;
    }

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

    public static DocEnv getDocEnv(Type javadocType) {
        try {
            Field envField = javadocType.getClass().getDeclaredField("env");
            return (DocEnv) envField.get(javadocType);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    public static com.sun.tools.javac.code.Type getJavacType(Type javadocType) {
        try {
            Field envField = javadocType.getClass().getField("type");
            return (com.sun.tools.javac.code.Type) envField.get(javadocType);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
}