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

.. contents:: Overview
   :depth: 2

Limitations
-----------

**Current version works only with Java 8, because require %JAVA_HOME%\lib\tools.jar for extracting javadoc comments.**

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
#. Add library to dependencies:

   .. code:: gradle

      dependencies {
          rstDoclet('com.devives:devive-rst-doclet-jdk8-all:0.2.1')
      }
#. Register gradle task ``javadoc2rst``:

   .. code:: gradle

      tasks.register('javadoc2rst', Javadoc) {
          description = 'Generate rst files based on javadoc comments in code.'
          group = 'documentation'
          source = sourceSets.main.allJava
          classpath = configurations.compileClasspath
          destinationDir = file("$docsDir/javadoc2rst")
          failOnError = true
          options.docletpath = configurations.rstDoclet.files as List
          options.doclet = "com.devives.rstdoclet.RstDoclet"
          options.encoding = "UTF-8"
          options.showFromPackage()
          (options as CoreJavadocOptions).addStringOption("packageindexfilename", "package-index")
      }
#. Reload All Gradle Projects.
#. Execute gradle task ``documentation \ javadoc2rst``.
#. Find generated files at ``$project.build/docs/javadoc2rst/``.

License
-------

The code of project distributed under the GNU General Public License version 2 only. 
The source code is available on `GitHub <https://github.com/devives/rst-doclet>`_.

Why ``GNU General Public License version 2 only``?

   The RstDoclet project has several code borrowings from JDK, which redistribute under GNU General Public License version 2 only.
   May be later, borrowings will be removed and license can be changed. All depends on future and demand of the project.

Links
-----

* `Javadoc Tool <https://www.oracle.com/java/technologies/javase/javadoc-tool.html>`_
* `ReStructuredText Document & Builder for Sphinx <https://github.com/devives/rst-document-for-sphinx>`_
* `ReStructuredText Document & Builder <https://github.com/devives/rst-document>`_

.. footer::

   This document generated using `this code <https://github.com/devives/rst-doclet/blob/main/jdk8/src/test/java/com/devives/rstdoclet/ReadMeGenerator.java>`_.
