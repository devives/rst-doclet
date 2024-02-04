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

import com.devives.AbstractTest;
import com.sun.tools.javadoc.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * Provided by Standard doclet:
 * -d <directory>                   Destination directory for output files
 * -use                             Create class and package usage pages
 * -version                         Include @version paragraphs
 * -author                          Include @author paragraphs
 * -docfilessubdirs                 Recursively copy doc-file subdirectories
 * -splitindex                      Split index into one file per letter
 * -windowtitle <text>              Browser window title for the documentation
 * -doctitle <html-code>            Include title for the overview page
 * -header <html-code>              Include header text for each page
 * -footer <html-code>              Include footer text for each page
 * -top    <html-code>              Include top text for each page
 * -bottom <html-code>              Include bottom text for each page
 * -link <url>                      Create links to javadoc output at <url>
 * -linkoffline <url> <url2>        Link to docs at <url> using package list at <url2>
 * -excludedocfilessubdir <name1>:.. Exclude any doc-files subdirectories with given name.
 * -group <name> <p1>:<p2>..        Group specified packages together in overview page
 * -nocomment                       Suppress description and tags, generate only declarations.
 * -nodeprecated                    Do not include @deprecated information
 * -noqualifier <name1>:<name2>:... Exclude the list of qualifiers from the output.
 * -nosince                         Do not include @since information
 * -notimestamp                     Do not include hidden time stamp
 * -nodeprecatedlist                Do not generate deprecated list
 * -notree                          Do not generate class hierarchy
 * -noindex                         Do not generate index
 * -nohelp                          Do not generate help link
 * -nonavbar                        Do not generate navigation bar
 * -serialwarn                      Generate warning about @serial tag
 * -tag <name>:<locations>:<header> Specify single argument custom tags
 * -taglet                          The fully qualified name of Taglet to register
 * -tagletpath                      The path to Taglets
 * -charset <charset>               Charset for cross-platform viewing of generated documentation.
 * -helpfile <file>                 Include file that help link links to
 * -linksource                      Generate source in HTML
 * -sourcetab <tab length>          Specify the number of spaces each tab takes up in the source
 * -keywords                        Include HTML meta tags with package, class and member info
 * -stylesheetfile <path>           File to change style of the generated documentation
 * -docencoding <name>              Specify the character encoding for the output
 */
public class HtmlDocletTest extends AbstractTest {

    private static final Path projectRootPath = Paths.get("").toAbsolutePath();
    private static final Path outputPath = projectRootPath.resolve("build/test-results/javadoc");

    @BeforeAll
    public static void beforeAll() throws Exception {
        System.out.println("project.root = " + projectRootPath);
        System.out.println("outputPath = " + outputPath);
        if (Files.exists(outputPath)) {
            Files.walk(outputPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void generate_forSamples_noExceptions() throws Exception {
        Path sourcePath = projectRootPath.resolve("src/test/java/");
        String subPackages = "com.devives.samples";
        String[] args = new String[]{
                "-d", outputPath.toString()
                , "-package"
                , "-encoding", "UTF-8"
                , "-sourcepath", sourcePath.toString()
                , "-subpackages", subPackages
        };
        Assertions.assertEquals(0, Main.execute(args));
    }


    @Test
    public void generate_forJavaUtils_noExceptions() throws Exception {
        Path sourcePath = Paths.get(System.getenv("JAVA_HOME")).resolve("src").toAbsolutePath();
        String subpackages = "java.util";
        String[] args = new String[]{
                "-d", outputPath.toString()
                , "-encoding", "UTF-8"
                , "-doclet", RstDoclet.class.getCanonicalName()
                , "-sourcepath", sourcePath + "/" + ";"
                , "-subpackages", subpackages
        };
        System.out.println("sourcePath = " + sourcePath);
        Assertions.assertEquals(0, Main.execute(args));
    }


//
//    private static final Path projectRootPath = Paths.get("").toAbsolutePath();
//
//    @BeforeAll
//    public static void beforeAll() {
//        System.out.println("user.dir = " + System.getProperty("user.dir"));
//
//    }
//
//    @Test
//    public void javadoc_for_samples() throws Exception {
//        String sourcePath = projectRootPath.resolve("src/test/java/").toString();
//        String outPath = projectRootPath.resolve("build/docs/javadoc/").toString();
//        String subPackages = "com.devives.rstdoclet.samples";
//        String[] args = new String[]{
//                "-d", outPath
//                , "-package"
//                , "-encoding", "UTF-8"
//                , "-sourcepath", sourcePath
//                , "-subpackages", subPackages
//        };
//        Assertions.assertEquals(0, Main.execute(args));
//    }
//
//    @Test
//    public void javadoc_for_java_util() throws Exception {
//        Path jkdSourcePath = Paths.get(System.getenv("JAVA_HOME")).resolve("src").toAbsolutePath();
//        System.out.println("jdk.source = " + jkdSourcePath);
//        String docletPath = projectRootPath.resolve("build/classes/java/main/") + "/";
//        String sourcePath = jkdSourcePath + "/;";
//        String subpackages = "java.util";
//        //String sourceFiles = sourceRoot.resolve("java\\util\\AbstractQueue.java").toString();
//        String outPath = projectRootPath.resolve("sphinx/sphinx-project/source/java-doc/").toString();
//        String[] args = new String[]{
//                "-d", outPath
//                , "-encoding", "UTF-8"
//                , "-doclet", RstDoclet.class.getCanonicalName()
//                , "-docletpath", docletPath
//                , "-sourcepath", sourcePath
//                , "-subpackages", subpackages
//                //, sourceFiles
//        };
//        Assertions.assertEquals(0, Main.execute(args));
//    }


}
