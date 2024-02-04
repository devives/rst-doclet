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
 * Usage: javadoc [options] [packagenames] [sourcefiles] [@files]
 *   -overview <file>                 Read overview documentation from HTML file
 *   -public                          Show only public classes and members
 *   -protected                       Show protected/public classes and members (default)
 *   -package                         Show package/protected/public classes and members
 *   -private                         Show all classes and members
 *   -help                            Display command line options and exit
 *   -doclet <class>                  Generate output via alternate doclet
 *   -docletpath <path>               Specify where to find doclet class files
 *   -sourcepath <pathlist>           Specify where to find source files
 *   -classpath <pathlist>            Specify where to find user class files
 *   -cp <pathlist>                   Specify where to find user class files
 *   -exclude <pkglist>               Specify a list of packages to exclude
 *   -subpackages <subpkglist>        Specify subpackages to recursively load
 *   -breakiterator                   Compute first sentence with BreakIterator
 *   -bootclasspath <pathlist>        Override location of class files loaded
 *                                    by the bootstrap class loader
 *   -source <release>                Provide source compatibility with specified release
 *   -extdirs <dirlist>               Override location of installed extensions
 *   -verbose                         Output messages about what Javadoc is doing
 *   -locale <name>                   Locale to be used, e.g. en_US or en_US_WIN
 *   -encoding <name>                 Source file encoding name
 *   -quiet                           Do not display status messages
 *   -J<flag>                         Pass <flag> directly to the runtime system
 *   -X                               Print a synopsis of nonstandard options and exit
 */
public class RstDocletTest extends AbstractTest {

    private static final Path projectRootPath = Paths.get("").toAbsolutePath();
    private static final Path docletPath = projectRootPath.resolve("build/classes/java/main/");
    private static final Path outputPath = projectRootPath.resolve("build/test-results/javadoc2rst");

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
                , "-doclet", RstDoclet.class.getCanonicalName()
                , "-docletpath", docletPath.toString()
                , "-sourcepath", sourcePath.toString()
                , "-subpackages", subPackages
        };
        System.out.println("sourcePath = " + sourcePath);
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
                , "-docletpath", docletPath.toString()
                , "-sourcepath", sourcePath + "/" + ";"
                , "-subpackages", subpackages
                //, sourceFiles
        };
        System.out.println("sourcePath = " + sourcePath);
        Assertions.assertEquals(0, Main.execute(args));
    }


}
