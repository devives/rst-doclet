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
import com.devives.rstdoclet.RstConfigurationImpl;
import com.devives.rstdoclet.html.HtmlDocletWriter;
import com.devives.rstdoclet.html.PackageHtmlWriterImpl;
import com.devives.rstdoclet.html2rst.CommentBuilder;
import com.devives.rstdoclet.html2rst.TagUtils;
import com.devives.rstdoclet.rst.builder.JavaPackageBuilder;
import com.devives.sphinx.rst.Rst4Sphinx;
import com.devives.sphinx.rst.document.IncludeDocument;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Type;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

public class PackageSummaryRstGenerator implements Supplier<String> {

    private final PackageDoc packageDoc_;
    private final RstConfigurationImpl configuration_;
    private final PackageHtmlWriterImpl htmlPackageWriter_;
    private final HtmlDocletWriter htmlDocletWriter_;
    private final RstGeneratorContext docContext_;

    public PackageSummaryRstGenerator(PackageDoc packageDoc, RstConfigurationImpl configuration) throws IOException {
        this.packageDoc_ = packageDoc;
        this.configuration_ = configuration;
        this.htmlPackageWriter_ = new PackageHtmlWriterImpl(configuration.getHtmlConfiguration(), packageDoc_, null, null);
        this.htmlDocletWriter_ = new HtmlDocletWriter(htmlPackageWriter_);
        this.docContext_ = new RstGeneratorContextImpl(configuration, htmlDocletWriter_);
    }

    @Override
    public String get() {
        return Rst4Sphinx.builders().document()
                .title(packageDoc_.name())
                .addChild(new JavaPackageBuilder<>(packageDoc_, docContext_).build())
                .ifTrue(packageDoc_.tags().length > 0, (builder) -> {
                    BlockQuoteBuilder<?> bodyBuilder = new BlockQuoteBuilderImpl<>();
                    new TagUtils(docContext_).appendTags(bodyBuilder, packageDoc_, Arrays.asList(TagUtils.TagName.Since, TagUtils.TagName.Version, TagUtils.TagName.Deprecated));
                    bodyBuilder.build().getChildren().forEach(builder::addChild);
                })
                .ifTrue(packageDoc_.inlineTags().length > 0, (builder) -> {
                    IncludeDocument includeDocument = new IncludeDocument();
                    includeDocument.getChildren().add(new CommentBuilder(
                            docContext_,
                            packageDoc_).build());
                    builder.addChild(includeDocument);
                })
                .tocTree(tocTreeBuilder -> tocTreeBuilder
                        .setMaxDepth(1)
                        .ifTrue(packageDoc_.allClasses().length > 0, () -> {
                            ClassDoc[] classDocs = Arrays.stream(packageDoc_.allClasses())
                                    .sorted(Comparator.comparing(Type::typeName))
                                    .toArray(ClassDoc[]::new);
                            tocTreeBuilder.paragraph(pb -> {
                                for (ClassDoc classDoc : classDocs) {
                                    pb.text(classDoc.name().replace(".", "-") + ".rst")
                                            .lineBreak();
                                }
                            });
                        }))
                .build().serialize();
    }
}
