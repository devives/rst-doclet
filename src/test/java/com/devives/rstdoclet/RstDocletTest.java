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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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
    }

    private void deleteDirectoryRecursive(Path path) throws Exception {
        if (Files.exists(path)) {
            try (Stream<Path> pathStream = Files.walk(path)) {
                pathStream.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }

    @Test
    public void generate_forSamples_noExceptions() throws Exception {
        deleteDirectoryRecursive(outputPath.resolve("com"));
        Path sourcePath = projectRootPath.resolve("../samples/src/main/java/");
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
        validateResults(
                projectRootPath.resolve("src/test/expectations/com/devives"),
                outputPath.resolve("com/devives"));
    }


    @Test
    public void generate_forJavaUtils_noExceptions() throws Exception {
        deleteDirectoryRecursive(outputPath.resolve("java"));
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

    @Test
    @Disabled
    public void validateResultsTest() throws Exception {
        validateResults(
                projectRootPath.resolve("src/test/expectations/com/devives"),
                outputPath.resolve("com/devives"));
    }

    private void validateResults(Path expectationsPath, Path resultsPath) throws Exception {
        if (!Files.exists(expectationsPath)) {
            throw new IOException("Directory '" + expectationsPath + "' not exists.");
        }
        if (!Files.exists(resultsPath)) {
            throw new IOException("Directory '" + resultsPath + "' not exists.");
        }

        try (Stream<Path> pathStream = Files.walk(expectationsPath)) {
            pathStream.forEach(expectedPath -> {
                try {
                    Path relativeActualPath = expectationsPath.relativize(expectedPath);
                    Path actualPath = resultsPath.resolve(relativeActualPath);
                    if (!Files.exists(actualPath)) {
                        throw new IOException("Directory or file '" + actualPath + "' not exists.");
                    }
                    if (actualPath.toFile().isFile()) {
                        List<String> expectedLines = Files.readAllLines(expectedPath);
                        List<String> actualLines = Files.readAllLines(actualPath);
                        for (int i = 0; i < expectedLines.size(); i++) {
                            try {
                                Assertions.assertEquals(expectedLines.get(i), actualLines.get(i));
                            } catch (AssertionFailedError e) {
                                e.addSuppressed(new Exception(String.format("Files '%s' and '%s' are not equals.", expectedPath, actualPath)));
                                throw e;
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    // E:\GitHub\devives\RstProjectGroup\rst-doclet\build\test-results\javadoc2rst\com\devives
}
