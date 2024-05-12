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

import com.devives.rst.builder.BlockQuoteBuilder;
import com.devives.rst.builder.BlockQuoteBuilderImpl;
import com.devives.rstdoclet.RstConfiguration;
import com.devives.rstdoclet.html.HtmlAdaptersFactory;
import com.devives.rstdoclet.html.HtmlDocletWriterAdapter;
import com.devives.rstdoclet.html2rst.CommentBuilder;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.rst.builder.JavaPackageBuilder;
import com.devives.sphinx.rst.Rst4Sphinx;
import com.devives.sphinx.rst.document.IncludeDocument;
import com.sun.source.doctree.DocTree;
import jdk.javadoc.internal.doclets.formats.html.HtmlDocletWriter;

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
    private final HtmlDocletWriter htmlPackageWriter_;
    private final HtmlDocletWriterAdapter htmlDocletWriter_;
    private final RstGeneratorContext docContext_;

    public PackageSummaryRstGenerator(PackageElement packageDoc, RstConfiguration configuration) {
        this.packageDoc_ = packageDoc;
        this.configuration_ = configuration;
        this.htmlPackageWriter_ = HtmlAdaptersFactory.getInstance().newHtmlPackageWriter(configuration.getHtmlConfiguration(), packageDoc_);
        this.htmlDocletWriter_ =
                HtmlAdaptersFactory.getInstance().newHtmlDocletWriterAdapter(
                        HtmlAdaptersFactory.getInstance().newHtmlDocletWriter(
                                htmlPackageWriter_));
        this.docContext_ = new RstGeneratorContextImpl(configuration, htmlDocletWriter_);
    }

    @Override
    public String get() {
        List<? extends DocTree> tags = configuration_.utils().getBlockTags(packageDoc_);
        List<? extends DocTree> body = configuration_.utils().getBody(packageDoc_);
        SortedSet<? extends TypeElement> allClasses = configuration_.utils().getAllClasses(packageDoc_);
        return Rst4Sphinx.builders().document()
                .title(configuration_.utils().getPackageName(packageDoc_))
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
                                    pb.text(configuration_.utils().getSimpleName(classDoc).replace(".", "-") + ".rst")
                                            .lineBreak();
                                }
                            });
                        }))
                .build().serialize();
    }

}
