package com.devives.samples;

@Deprecated
@SampleAnnotation
public class SampleAnnotatedClass {

    /**
     * Annotated Field.
     */
    @Deprecated
    @SampleAnnotation
    public String annotatedField = "";

    /**
     * Annotated procedure.
     */
    @Deprecated
    @SampleAnnotation
    void annotatedProcedure() {

    }

    /**
     * Procedure with annotated arguments.
     *
     * @param arg1 First argument
     * @param arg2 Second argument
     */
    void annotatedArgsProcedure(@Deprecated Object arg1, @Deprecated @SampleAnnotation Object arg2) {

    }

}
