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
import com.sun.javadoc.*;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class ClassRstGenerator implements Supplier<String> {

    private final ClassDoc classDoc_;
    private final RstConfigurationImpl configuration_;
    private final RstGeneratorContext docContext_;
    private final Map<String, ClassDoc> imports_ = new HashMap<>();
    private final HtmlDocletWriter classHtmlWriter_;

    public ClassRstGenerator(RstConfigurationImpl configuration, ClassTree classTree, ClassDoc classDoc) throws IOException {
        this.classDoc_ = classDoc;
        this.configuration_ = configuration;
        com.sun.tools.doclets.formats.html.HtmlDocletWriter jdkHtmlDocletWriter = new ClassHtmlWriterImpl(
                configuration.getHtmlConfiguration(),
                classDoc,
                null,
                null,
                classTree);
        this.classHtmlWriter_ = new HtmlDocletWriter(jdkHtmlDocletWriter);
        this.docContext_ = new RstGeneratorContextImpl(configuration, classHtmlWriter_);
    }

    @Override
    public String get() {
        RstDocumentBuilder<?> classContentBuilder = Rst.builders().document().title(classDoc_.name());
        classContentBuilder
                .addChild(new JavaPackageBuilder<>(classDoc_.containingPackage(), docContext_)
                        .setNoIndex(true)
                        .build())
                .ifTrue(classDoc_.annotations().length > 0, builder -> {
                    String annotations = DocUtils.formatAnnotations(classDoc_);
                    builder.ifTrue(!annotations.isEmpty(), () -> {
                        builder.paragraph(annotations);
                    });
                })
                .addChild(new JavaTypeBuilder<>(classDoc_, docContext_).fillImports(imports_)
                        .build())
                .ifTrue(classDoc_.enumConstants().length > 0, (textBuilder) -> {
                    textBuilder.subTitle("Enum Constants");
                    FieldDoc[] sortedFieldDocs = Arrays.stream(classDoc_.enumConstants()).sorted(Comparator.comparing(Doc::name)).toArray(FieldDoc[]::new);
                    for (FieldDoc fieldDoc : sortedFieldDocs) {
                        textBuilder.title(fieldDoc.name(), 3)
                                .addChild(new JavaFieldBuilder<>(fieldDoc, docContext_).fillImports(imports_).build());
                    }
                }).ifTrue(classDoc_.fields().length > 0, (documentBuilder) -> {
                    documentBuilder.subTitle("Fields");
                    FieldDoc[] sortedFieldDocs = Arrays.stream(classDoc_.fields()).sorted(Comparator.comparing(Doc::name)).toArray(FieldDoc[]::new);
                    for (FieldDoc fieldDoc : sortedFieldDocs) {
                        documentBuilder.title(fieldDoc.name(), 3)
                                .addChild(new JavaFieldBuilder<>(fieldDoc, docContext_).fillImports(imports_).build());
                    }
                }).ifTrue(classDoc_.constructors().length > 0, (documentBuilder) -> {
                    documentBuilder.subTitle("Constructors");
                    for (ConstructorDoc constructorDoc : classDoc_.constructors()) {
                        documentBuilder.title(constructorDoc.name(), 3)
                                .addChild(new JavaConstructorBuilder<>(constructorDoc, docContext_).fillImports(imports_).build());
                    }
                }).ifTrue(classDoc_.methods().length > 0, (documentBuilder) -> {
                    documentBuilder.subTitle("Methods");
                    MethodDoc[] sortedMethodDocs = Arrays.stream(classDoc_.methods()).sorted(Comparator.comparing(Doc::name)).toArray(MethodDoc[]::new);
                    for (MethodDoc methodDoc : sortedMethodDocs) {
                        documentBuilder.title(methodDoc.name(), 3)
                                .addChild(new JavaMethodBuilder<>(methodDoc, docContext_).fillImports(imports_).build());
                    }
                });
        Map<String, ClassDoc> filteredImports = imports_.entrySet().stream()
                .filter((entry) -> (!(entry.getValue().containingPackage().name()).equals(classDoc_.containingPackage().name())
                        && !entry.getKey().startsWith(Object.class.getPackage().getName())
                        && entry.getValue().containingPackage() != null
                        && !entry.getValue().containingPackage().equals(classDoc_.containingPackage())
                        && !entry.getValue().containingPackage().name().equals("")
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Directive> imports = filteredImports.values().stream()
                .sorted(Comparator.comparing(Type::qualifiedTypeName))
                .map(doc -> new JavaImportBuilder<>(doc).build())
                .collect(Collectors.toList());

        RstDocument document = classContentBuilder.build();
        document.getChildren().addAll(0, imports);
        return document.getSerialized("Something gone wrong.");
    }

}
