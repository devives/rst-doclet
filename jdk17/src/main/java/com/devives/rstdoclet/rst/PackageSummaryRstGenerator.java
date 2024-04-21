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

import com.devives.rst.builder.BlockQuoteBuilder;
import com.devives.rst.builder.BlockQuoteBuilderImpl;
import com.devives.rstdoclet.RstConfiguration;
import com.devives.rstdoclet.html2rst.CommentBuilder;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.html2rst.jdkloans.HtmlDocletWriter;
import com.devives.rstdoclet.rst.builder.JavaPackageBuilder;
import com.devives.sphinx.rst.Rst4Sphinx;
import com.devives.sphinx.rst.document.IncludeDocument;
import com.sun.source.doctree.DocTree;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Supplier;

public class PackageSummaryRstGenerator implements Supplier<String> {

    private final PackageElement packageDoc_;
    private final RstConfiguration configuration_;
    private final HtmlDocletWriter docContext_;

    public PackageSummaryRstGenerator(PackageElement packageDoc, RstConfiguration configuration) {
        this.packageDoc_ = packageDoc;
        this.configuration_ = configuration;
        this.docContext_ = new HtmlDocletWriter(packageDoc_, configuration_);
    }

    @Override
    public String get() {
        List<? extends DocTree> tags = configuration_.utils.getBlockTags(packageDoc_);
        List<? extends DocTree> body = configuration_.utils.getBody(packageDoc_);
        SortedSet<? extends TypeElement> allClasses = configuration_.utils.getAllClasses(packageDoc_);
        return Rst4Sphinx.builders().document()
                .title(configuration_.utils.getPackageName(packageDoc_))
                .addChild(new JavaPackageBuilder<>(packageDoc_, configuration_).build())
                .ifTrue(tags.size() > 0, (builder) -> {
                    BlockQuoteBuilder<?> bodyBuilder = new BlockQuoteBuilderImpl<>();
                    new TagUtils(docContext_).appendTags(bodyBuilder, packageDoc_, Arrays.asList(TagUtils.TagName.Since, TagUtils.TagName.Version, TagUtils.TagName.Deprecated));
                    bodyBuilder.build().getChildren().forEach(builder::addChild);
                })
                .ifTrue(body.size() > 0, (builder) -> {
                    IncludeDocument includeDocument = new IncludeDocument();
                    includeDocument.getChildren().add(new CommentBuilder(
                            packageDoc_,
                            docContext_).build());
                    builder.addChild(includeDocument);
                })
                .tocTree(tocTreeBuilder -> tocTreeBuilder
                        .setMaxDepth(1)
                        .ifTrue(allClasses.size() > 0, () -> {
                            TypeElement[] classDocs = allClasses.stream()
                                    .sorted(Comparator.comparing(t -> t.getQualifiedName().toString()))
                                    .toArray(TypeElement[]::new);
                            tocTreeBuilder.paragraph(pb -> {
                                for (TypeElement classDoc : classDocs) {
                                    pb.text(configuration_.utils.getSimpleName(classDoc).replace(".", "-") + ".rst")
                                            .lineBreak();
                                }
                            });
                        }))
                .build().serialize();
    }
//
//    private static String formatSimpleName(TypeElement typeElement) {
//        List<String> list = new LinkedList<>();
//        Element parentElement = typeElement.getEnclosingElement();
//        while (parentElement instanceof TypeElement) {
//            list.add(0, parentElement.getSimpleName().toString());
//            parentElement = parentElement.getEnclosingElement();
//        }
//        list.add(typeElement.getSimpleName().toString());
//        return String.join(".", list);
//    }
}
