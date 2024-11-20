package processor;

import com.google.common.base.Joiner;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.List;

public class AnnotationProcessorTest {
    public static final String NEW_LINE = System.getProperty("line.separator");

    @Test
    public void generateOutput() {
        final JavaFileObject inputInterface = JavaFileObjects.forSourceString(
                "test.MyInterface",
                Joiner.on(NEW_LINE).join(
                        "package test;",
                        "public interface MyInterface {",
                        "void foo();",
                        "}"
                )
        );

        final JavaFileObject input = JavaFileObjects.forSourceString(
                "test.Test",
                Joiner.on(NEW_LINE).join(
                        "package test;",
                        "@annotations.ExecutionTimer",
                        "public class Test implements MyInterface {",
                        "public void foo() {",
                        "System.out.println(\"some logic\");",
                        "}",
                        "}"
                )
        );

        final JavaFileObject output = JavaFileObjects.forSourceString(
                "test.metered.MeteredTest",
                Joiner.on(NEW_LINE).join(
                        "package metered;",

                        "import annotations.Generated;",
                        "import java.lang.Long;",
                        "import java.lang.Override;",
                        "import java.lang.System;",
                        "import test.MyInterface;",
                        "import test.Test;",

                        "@Generated(\n",
                        "    description = \"Generated code\"\n",
                        ")",

                        "public final class MeteredTest implements MyInterface {",

                        "private final Test delegate;",

                        "public MeteredServiceClass(Test delegate) {",
                        "        this.delegate = delegate;",
                        "    }",

                        "@Override",
                        "public void foo() {",
                        "Long start = System.currentTimeMillis();",
                        "delegate.foo();",
                        "Long end = System.currentTimeMillis();",
                        "System.out.println(\"Code execution time (ms): \" + end);",
                        "}",


                        "}"
                )
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(inputInterface, input))
                .processedWith(new AnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(output);
    }
}
