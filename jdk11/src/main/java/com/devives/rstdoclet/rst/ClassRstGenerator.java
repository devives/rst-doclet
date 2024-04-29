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
package com.devives.rstdoclet.rst;

import com.devives.rst.Rst;
import com.devives.rst.builder.RstDocumentBuilder;
import com.devives.rst.document.RstDocument;
import com.devives.rst.document.directive.Directive;
import com.devives.rstdoclet.RstConfigurationImpl;
import com.devives.rstdoclet.html.ClassHtmlWriterImpl;
import com.devives.rstdoclet.html.HtmlDocletWriter;
import com.devives.rstdoclet.html2rst.DocUtils;
import com.devives.rstdoclet.rst.builder.*;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ClassRstGenerator implements Supplier<String> {

    private final TypeElement classDoc_;
    private final RstConfigurationImpl configuration_;
    private final Map<String, TypeElement> imports_ = new HashMap<>();
    private final ClassHtmlWriterImpl htmlClassWriter_;
    private final HtmlDocletWriter htmlDocletWriter_;
    private final RstGeneratorContext docContext_;

    public ClassRstGenerator(RstConfigurationImpl configuration, TypeElement typeElement, ClassTree classTree) {
        this.classDoc_ = Objects.requireNonNull(typeElement);
        this.configuration_ = configuration;
        this.htmlClassWriter_ = new ClassHtmlWriterImpl(configuration.getHtmlConfiguration(), typeElement, classTree);
        this.htmlDocletWriter_ = new HtmlDocletWriter(htmlClassWriter_);
        this.docContext_ = new RstGeneratorContextImpl(configuration, htmlDocletWriter_);
    }

    @Override
    public String get() {
        RstDocumentBuilder<?> classContentBuilder = Rst.builders().document().title(configuration_.utils.getSimpleName(classDoc_));
        List<Element> enumConstants = configuration_.utils.getEnumConstants(classDoc_);
        List<VariableElement> fields = configuration_.utils.getFields(classDoc_);
        List<ExecutableElement> constructors = configuration_.utils.getConstructors(classDoc_);
        List<ExecutableElement> methods = configuration_.utils.getMethods(classDoc_);

        classContentBuilder
                .addChild(new JavaPackageBuilder<>(ElementUtils.getPackageOfType(classDoc_), configuration_)
                        .setNoIndex(true)
                        .build())
                .ifTrue(classDoc_.getAnnotationMirrors().size() > 0, builder -> {
                    String annotations = DocUtils.formatAnnotations(classDoc_);
                    builder.ifTrue(!annotations.isEmpty(), () -> {
                        builder.paragraph(annotations);
                    });
                })
                .addChild(new JavaTypeBuilder<>(classDoc_, docContext_).fillImports(imports_).build())
                .ifTrue(enumConstants.size() > 0, (textBuilder) -> {
                    textBuilder.subTitle("Enum Constants");
                    VariableElement[] sortedFieldDocs = enumConstants.stream()
                            .sorted(Comparator.comparing(e -> configuration_.utils.getSimpleName(e)))
                            .map(it -> (VariableElement) it)
                            .toArray(VariableElement[]::new);
                    for (VariableElement fieldDoc : sortedFieldDocs) {
                        textBuilder.title(configuration_.utils.getSimpleName(fieldDoc), 3)
                                .addChild(new JavaFieldBuilder<>(fieldDoc, docContext_).fillImports(imports_).build());
                    }
                }).ifTrue(fields.size() > 0, (documentBuilder) -> {
                    documentBuilder.subTitle("Fields");
                    VariableElement[] sortedFieldDocs = fields.stream().sorted(Comparator.comparing(e -> configuration_.utils.getSimpleName(e))).toArray(VariableElement[]::new);
                    for (VariableElement fieldDoc : sortedFieldDocs) {
                        documentBuilder.title(configuration_.utils.getSimpleName(fieldDoc), 3)
                                .addChild(new JavaFieldBuilder<>(fieldDoc, docContext_).fillImports(imports_).build());
                    }
                }).ifTrue(constructors.size() > 0, (documentBuilder) -> {
                    documentBuilder.subTitle("Constructors");
                    for (ExecutableElement constructorDoc : constructors) {
                        documentBuilder.title(configuration_.utils.getSimpleName(constructorDoc), 3)
                                .addChild(new JavaConstructorBuilder<>(constructorDoc, docContext_).fillImports(imports_).build());
                    }
                }).ifTrue(methods.size() > 0, (documentBuilder) -> {
                    documentBuilder.subTitle("Methods");
                    ExecutableElement[] sortedMethodDocs = methods.stream()
                            .sorted(Comparator.comparing(e -> configuration_.utils.getSimpleName(e)))
                            .toArray(ExecutableElement[]::new);
                    for (ExecutableElement methodDoc : sortedMethodDocs) {
                        documentBuilder.title(configuration_.utils.getSimpleName(methodDoc), 3)
                                .addChild(new JavaMethodBuilder<>(methodDoc, docContext_).fillImports(imports_).build());
                    }
                });
        Map<String, TypeElement> filteredImports = imports_.entrySet().stream()
                .filter((entry) -> (!(entry.getKey()).startsWith((ElementUtils.getPackageOfType(classDoc_)).getQualifiedName().toString())
                        && !entry.getKey().startsWith(Object.class.getPackage().getName())
                        && entry.getValue().getEnclosingElement() != null
                        && !entry.getValue().getEnclosingElement().equals(classDoc_.getEnclosingElement())
                        && (entry.getValue().getEnclosingElement().getSimpleName().length() != 0)
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Directive> imports = new ArrayList<>();
        for (TypeElement doc : filteredImports.values()) {
            imports.add(0, new JavaImportBuilder<>(doc, configuration_.utils).build());
        }
        RstDocument document = classContentBuilder.build();
        document.getChildren().addAll(0, imports);
        return document.getSerialized("Something gone wrong.");
    }
}
