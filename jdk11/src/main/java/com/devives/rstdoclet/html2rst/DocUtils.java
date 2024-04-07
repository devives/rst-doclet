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

import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DocUtils {

    public static String formatTypeName(TypeMirror type, Utils utils) {
        return doFormatTypeName(type, () -> utils.getQualifiedTypeName(type), utils);
    }

    private static String doFormatTypeName(TypeMirror type, Supplier<String> nameGetter, Utils utils) {
        String result = type.toString();
        if (!type.getKind().isPrimitive()) {
            Map<String, TypeElement> refClasses = new HashMap<>();
            refClasses.put(utils.getQualifiedTypeName(type), utils.asTypeElement(type));
            refClasses.putAll(new ImportsCollector(utils).collect(utils.asTypeElement(type)).getImportsMap());
            for (Map.Entry<String, TypeElement> entry : refClasses.entrySet()) {
                result = result.replace(entry.getKey(), entry.getValue().getSimpleName().toString());
            }
        }
        return result;
    }
//
//    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("(\\w+\\.)+(\\w+)");
//
//    public static String formatReturnTypeName(TypeMirror type) {
//        String result = type.typeName();
//        if (type.isPrimitive()) {
//            // do nothing
//        } else if ("[]".equals(type.dimension())) {
//            result += type.dimension();
//        } else if (type.asParameterizedType() != null) {
//            String typeArgs = Arrays.stream(type.asParameterizedType().typeArguments())
//                    .map(DocUtils::formatTypeName)
//                    .collect(Collectors.joining(", "));
//            if (!typeArgs.isEmpty()) {
//                result += "<" + typeArgs + ">";
//            }
//        }
//        return result;
//    }
//
//
//    public static String formatTypeVariable(TypeVariable typeVariable) {
//        String result = typeVariable.toString();
//        if (!typeVariable.isPrimitive()) {
//            Map<String, TypeElement> refClasses = new HashMap<>();
//            for (Type bound : typeVariable.bounds()) {
//                refClasses.put(bound.qualifiedTypeName(), bound.asClassDoc());
//            }
//            for (Map.Entry<String, TypeElement> entry : refClasses.entrySet()) {
//                result = result.replaceAll(entry.getKey(), entry.getValue().typeName());
//            }
//        }
//        return result;
//    }

    public static String formatAnnotations(TypeElement classDoc) {
        String result = "";
        if (classDoc.getAnnotationMirrors().size() > 0) {
            result = classDoc.getAnnotationMirrors().stream()
                    .filter(an -> !(((TypeElement)an.getAnnotationType().asElement()).getQualifiedName().toString().startsWith("java.lang.annotation.")))
                    .map(a -> "@" + a.getAnnotationType().asElement().getSimpleName().toString())
                    .collect(Collectors.joining(" "));
        }
        return result;
    }
//
//    public static DocEnv getDocEnv(Type javadocType) {
//        try {
//            Field envField = javadocType.getClass().getDeclaredField("env");
//            return (DocEnv) envField.get(javadocType);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//
//        }
//    }
//
//    public static com.sun.tools.javac.code.Type getJavacType(Type javadocType) {
//        try {
//            Field envField = javadocType.getClass().getField("type");
//            return (com.sun.tools.javac.code.Type) envField.get(javadocType);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//
//        }
//    }
}