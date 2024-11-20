package processor;

import annotations.ExecutionTimer;
import annotations.Generated;
import com.google.auto.service.AutoService;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("annotations.ExecutionTimer")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "Start custom annotation-processor logic");

        try {
            for (Element element : roundEnv.getElementsAnnotatedWith(ExecutionTimer.class)) {
                if (element.getKind() == ElementKind.CLASS) {
                    var typeElement = (TypeElement) element;
                    generate(typeElement);
                }
            }
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Error happened while generating code: %s", e.getMessage()));
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "Finish custom annotation-processor logic");

        //  If true is returned, the annotation interfaces are claimed and subsequent processors
        //  will not be asked to process them; if false is returned, the annotation interfaces are unclaimed and
        //  subsequent processors may be asked to process them
        return false;
    }

    private void generate(TypeElement element) throws IOException {
        var interfaces = element.getInterfaces();
        var interfaceSuperTypes = interfaces.stream().map(TypeName::get).toList();

        var methodVisitor = new InterfaceMethodsVisitor(messager);
        interfaces.forEach(it -> it.accept(methodVisitor, null));
        var executables = methodVisitor.getExecutables();

        var className = "Metered" + element.getSimpleName().toString();

        var meteredMethods = new ArrayList<MethodSpec>();
        executables.forEach((ownerInterface, methods) -> methods.forEach(method -> {
            var name = method.getSimpleName().toString();

            var methodBuilder = MethodSpec
                    .methodBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);

            var returnType = TypeName.get(method.getReturnType());
            methodBuilder.returns(returnType);

            var inputTypes = method.getParameters();
            inputTypes.forEach(it -> {
                var parameterType = TypeName.get(it.asType());
                methodBuilder.addParameter(parameterType, it.getSimpleName().toString());
            });

            if (method.getReturnType().getKind() == TypeKind.VOID) {
                methodBuilder
                        .addStatement("$T start = $T.currentTimeMillis()", Long.class, System.class)
                        .addStatement("$N.$L()", "delegate", method.getSimpleName().toString())
                        .addStatement("$T end = $T.currentTimeMillis()", Long.class, System.class)
                        .addStatement("$T.out.println(\"Code execution time (ms): \" + $N)", System.class, "end");
            } else {
                var argsPlaceholders = inputTypes
                        .stream()
                        .map(it -> "$N").collect(Collectors.joining(", "));

                var superMethodInputArgs =  new Object[3 + inputTypes.size()];
                superMethodInputArgs[0] = returnType;
                superMethodInputArgs[1] = "delegate";
                superMethodInputArgs[2] = method.getSimpleName().toString();
                int idx = 3;
                for (var it : inputTypes) {
                    superMethodInputArgs[idx++] = it.getSimpleName().toString();
                }

                methodBuilder
                        .addStatement("$T start = $T.currentTimeMillis()", Long.class, System.class)
                        .addStatement(String.format("$T result = $N.$L(%s)", argsPlaceholders), superMethodInputArgs)
                        .addStatement("$T end = $T.currentTimeMillis()", Long.class, System.class)
                        .addStatement("$T.out.println(\"Code execution time (ms): \" + $N)", System.class, "end")
                        .addStatement("return $N", "result");
            }

            meteredMethods.add(methodBuilder.build());
        }));

        var generatedAnnotationMarkerCodeBlock = CodeBlock.of("\"$L\"", "Generated code");
        var delegateField = FieldSpec
                .builder(TypeName.get(element.asType()), "delegate")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        var constructor = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(element.asType()), "delegate")
                .addStatement("this.$N = $N", "delegate", "delegate")
                .build();

        var meteredClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(Generated.class).addMember("description", generatedAnnotationMarkerCodeBlock).build())
                .addSuperinterfaces(interfaceSuperTypes)
                .addField(delegateField)
                .addMethod(constructor)
                .addMethods(meteredMethods)
                .build();

        var javaFile = JavaFile.builder("metered", meteredClass)
                .build();

        javaFile.writeTo(filer);
    }
}
