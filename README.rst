==========================
RstDoclet for JavaDoc Tool
==========================

This project is the extension of `Javadoc Tool <https://www.oracle.com/java/technologies/javase/javadoc-tool.html>`_. 

It's Implement `Doclet <https://docs.oracle.com/javase/8/docs/technotes/guides/javadoc/doclet/overview.html>`_ 
witch generates `reStructuredText <https://www.sphinx-doc.org/en/master/usage/restructuredtext/index.html>`_
files based on `javadoc <https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html>`_
comments in code.

Generated ``*.rst`` files can be published using 
`Sphinx <https://www.sphinx-doc.org/en/master/>`_. 

.. note::

   The `javasphinx <https://bronto-javasphinx.readthedocs.io/en/latest/>`_ extension must be installed in Sphinx.

Quick Start
-----------

1. Add ``mavencentral()`` repository to your root ``build.gradle``:

   .. code:: gradle

      repositories {
          mavenCentral()
      }
#. Add ``rstDoclet``configuration to configurations:

   .. code:: gradle

      configurations {
          rstDoclet
      }
#. Add the library of required java version to the dependencies:

   Java 8

   .. code:: gradle

      dependencies {
          rstDoclet('com.devives:devive-rst-doclet-jdk8-all:0.4.3')
      }

   Java 11

   .. code:: gradle

      dependencies {
          rstDoclet('com.devives:devive-rst-doclet-jdk11-all:0.4.3')
      }

   Java 17

   .. code:: gradle

      dependencies {
          rstDoclet('com.devives:devive-rst-doclet-jdk17-all:0.4.3')
      }

   Java 21

   .. code:: gradle

      dependencies {
          rstDoclet('com.devives:devive-rst-doclet-jdk21-all:0.4.3')
      }
#. Register gradle task ``javadoc4sphinx``depends java version:

   Java 8

   .. code:: gradle

      tasks.register('javadoc4sphinx', Javadoc) {
          description = 'Generate rst files based on javadoc comments in code.'
          group = 'documentation'
          source = sourceSets.main.allJava
          classpath = configurations.compileClasspath
          destinationDir = file("$docsDir/javadoc4sphinx")
          failOnError = true
          options.docletpath = configurations.rstDoclet.files as List
          options.doclet = "com.devives.rstdoclet.RstDoclet"
          options.encoding = "UTF-8"
          options.showFromPackage()
          (options as CoreJavadocOptions).addStringOption("packageindexfilename", "package-index")
      }

   Java 11

   .. code:: gradle

      List<String> exportsList = [
              '--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit.taglets=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit.util=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.formats.html=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.formats.html.markup=ALL-UNNAMED',
      ]

      tasks.register('javadoc4sphinx', Javadoc) {
          description = 'Generate rst files based on javadoc comments in code.'
          group = 'documentation'
          source = sourceSets.main.allJava
          classpath = configurations.compileClasspath
          destinationDir = file("$docsDir/javadoc4sphinx")
          failOnError = true
          options.docletpath = configurations.rstDoclet.files as List
          options.doclet = "com.devives.rstdoclet.RstDoclet"
          options.encoding = "UTF-8"
          options.showFromPackage()
          (options as CoreJavadocOptions).addStringOption("packageindexfilename", "package-index")
          (options as CoreJavadocOptions).setJFlags(exportsList)
      }

   Java 17

   .. code:: gradle

      List<String> exportsList = [
              '--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit.taglets=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit.util=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.formats.html=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.formats.html.markup=ALL-UNNAMED',
      ]

      tasks.register('javadoc4sphinx', Javadoc) {
          description = 'Generate rst files based on javadoc comments in code.'
          group = 'documentation'
          source = sourceSets.main.allJava
          classpath = configurations.compileClasspath
          destinationDir = file("$docsDir/javadoc4sphinx")
          options.docletpath = configurations.rstDoclet.files.asType(List)
          options.doclet = "com.devives.rstdoclet.RstDoclet"
          options.encoding = "UTF-8"
          options.windowTitle(null)
          options.showFromPackage()
          failOnError = false
          (options as CoreJavadocOptions).addStringOption("packageindexfilename", "package-index")
          (options as CoreJavadocOptions).setJFlags(exportsList)
      }

   Java 21

   .. code:: gradle

      List<String> exportsList = [
              '--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED',
              '--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit.taglets=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit.util=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.formats.html=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.formats.html.markup=ALL-UNNAMED',
              '--add-exports=jdk.javadoc/jdk.javadoc.internal.doclets.toolkit.builders=ALL-UNNAMED',
      ]

      List<String> opensList = [
              '--add-opens=jdk.javadoc/jdk.javadoc.internal.doclets.formats.html=ALL-UNNAMED',
      ]

      tasks.register('javadoc4sphinx', Javadoc) {
          description = 'Generate rst files based on javadoc comments in code.'
          group = 'documentation'
          source = sourceSets.main.allJava
          classpath = configurations.compileClasspath
          destinationDir = file("$docsDir/javadoc4sphinx")
          options.docletpath = configurations.rstDoclet.files.asType(List)
          options.doclet = "com.devives.rstdoclet.RstDoclet"
          options.encoding = "UTF-8"
          options.windowTitle(null)
          options.showFromPackage()
          failOnError = false
          (options as CoreJavadocOptions).addStringOption("packageindexfilename", "package-index")
          (options as CoreJavadocOptions).setJFlags(exportsList + opensList)
      }
#. Reload All Gradle Projects.
#. Execute gradle task ``documentation \ javadoc4sphinx``.
#. Find generated files at ``$project.build/docs/javadoc4sphinx/``.

Complete example projects
-------------------------

Are placed at `GitHub <https://github.com/devives/rst-doclet/tree/main/usage/gradle>`_.

License
-------

The code of project distributed under the GNU General Public License version 3 or any later version.
The source code is available on `GitHub <https://github.com/devives/rst-doclet>`_.

Links
-----

* `Javadoc Tool <https://www.oracle.com/java/technologies/javase/javadoc-tool.html>`_
* `ReStructuredText Document & Builder for Sphinx <https://github.com/devives/rst-document-for-sphinx>`_
* `ReStructuredText Document & Builder <https://github.com/devives/rst-document>`_

.. footer::

   This document generated using `this code <https://github.com/devives/rst-doclet/blob/main/jdk8/src/test/java/com/devives/rstdoclet/ReadMeGenerator.java>`_.
