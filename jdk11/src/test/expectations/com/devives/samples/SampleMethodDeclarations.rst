.. java:import:: java.util HashMap

.. java:import:: java.util Collection

.. java:import:: java.util Map

.. java:import:: java.util List

SampleMethodDeclarations
========================

.. java:package:: com.devives.samples
   :noindex:

.. java:type:: public interface SampleMethodDeclarations<T>

   Sample interface illustrate different method declarations.

Methods
-------

arrayProcedure
^^^^^^^^^^^^^^

.. java:method:: void arrayProcedure(T[] args)
   :outertype: SampleMethodDeclarations

   Procedure with array arguments.

   :param args: Array of ``<T>``\ .

copy
^^^^

.. java:method:: static <T> void copy(List<? extends T> src, List<? super T> dest)
   :outertype: SampleMethodDeclarations

   Method copy source list content into destination list.

   :param src: Source
   :param dest: Destination
   :param <T>: Item type

defaultFunction
^^^^^^^^^^^^^^^

.. java:method:: default String defaultFunction(String argument)
   :outertype: SampleMethodDeclarations

   Default function.

   :param argument: Argument
   :return: Return ``null``\ .

functionReturnsObject
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: Object functionReturnsObject()
   :outertype: SampleMethodDeclarations

   Simple function with Object result.

   :return: ``true`` or ``false``\ .

functionReturnsPrimitive
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: boolean functionReturnsPrimitive()
   :outertype: SampleMethodDeclarations

   Simple function with boolean result.

   :return: ``true`` or ``false``\ .

methodWithClassTypeParameter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: default <R> R methodWithClassTypeParameter(Map<T, R> map)
   :outertype: SampleMethodDeclarations

   Method with class type parameter.

   :param map: map
   :param <R>: return type
   :return: Some object

parametrisedFunction
^^^^^^^^^^^^^^^^^^^^

.. java:method:: <R> R parametrisedFunction(R argument)
   :outertype: SampleMethodDeclarations

   Parametrized function.

   :param argument: First argument
   :param <R>: type of result
   :return: instance of ``<R>``\ .

parametrisedFunction2
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: <R extends List> R parametrisedFunction2(R argument)
   :outertype: SampleMethodDeclarations

   Parametrized function 2.

   :param argument: First argument
   :param <R>: type of result
   :return: instance of ``<R>``\ .

parametrisedFunction3
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: <R extends SampleInterface> List<Collection<SampleInterface2<HashMap<String, R>>>> parametrisedFunction3()
   :outertype: SampleMethodDeclarations

   Parametrized function 3.

   :param <R>: type of result
   :return: instance of ``<R>``\ .

parametrisedFunction3
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: <R extends List<String>> R parametrisedFunction3(R argument1, R argument2)
   :outertype: SampleMethodDeclarations

   Parametrized function.

   :param argument1: First argument
   :param argument2: Second argument
   :param <R>: type of result
   :return: instance of ``<R>``\ .

procedure
^^^^^^^^^

.. java:method:: void procedure()
   :outertype: SampleMethodDeclarations

   Simple procedure.

procedureWithoutJavaDoc
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: void procedureWithoutJavaDoc(String arg1, List<?> arg2)
   :outertype: SampleMethodDeclarations

varArgProcedure
^^^^^^^^^^^^^^^

.. java:method:: void varArgProcedure(Object... args)
   :outertype: SampleMethodDeclarations

   Procedure with variable arguments.

   :param args: Array of any object.

varArgProcedure2
^^^^^^^^^^^^^^^^

.. java:method:: void varArgProcedure2(T... args)
   :outertype: SampleMethodDeclarations

   Procedure with variable arguments.

   :param args: Array of ``<T>``\ .
