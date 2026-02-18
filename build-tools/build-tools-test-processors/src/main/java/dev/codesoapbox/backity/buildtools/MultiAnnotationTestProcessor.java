package dev.codesoapbox.backity.buildtools;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * Annotation processor that generates concrete subclasses for test classes
 * annotated (directly or via meta-annotation) with {@link MultiAnnotationTest}.
 *
 * <p>For each concrete annotation declared in {@code @MultiAnnotationTest},
 * a distinct subclass is generated and annotated accordingly.
 *
 * <p>This enables the same test class to be executed multiple times with
 * different class-level annotations (e.g. different database configurations),
 * eliminating the need to manually create per-configuration subclasses.
 *
 * @see <a href="https://github.com/junit-team/junit-framework/issues/4506">
 * JUnit issue discussing test class reusability limitations
 * </a>
 */
@SupportedAnnotationTypes("*")
public class MultiAnnotationTestProcessor extends AbstractProcessor {

    private static final String PROCESSOR_ANNOTATION = MultiAnnotationTest.class.getName();

    private Messager messager;
    private Elements elements;
    private Types types;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        messager = env.getMessager();
        elements = env.getElementUtils();
        types = env.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        for (Element root : roundEnv.getRootElements()) {
            if (root.getKind() != ElementKind.CLASS) {
                continue;
            }

            TypeElement classElement = (TypeElement) root;

            RecursiveAnnotationFinder.find(classElement, PROCESSOR_ANNOTATION)
                    .ifPresent(annotation -> generateForParentClass(classElement, annotation));
        }

        return false;
    }

    private void generateForParentClass(TypeElement parentClassElement, AnnotationMirror processorAnnotation) {
        AnnotationConfig annotationConfig = AnnotationConfig.create(elements, processorAnnotation);
        ParentClassInfo parentClassInfo = ParentClassInfo.build(elements, parentClassElement);

        for (AnnotationValue concreteAnnotationValue : annotationConfig.concreteAnnotations()) {
            TypeElement concreteAnnotationElement =
                    (TypeElement) types.asElement((TypeMirror) concreteAnnotationValue.getValue());

            GeneratedClassInfo generatedClassInfo = GeneratedClassInfo.create(
                    parentClassElement, concreteAnnotationElement, parentClassInfo, annotationConfig);

            generateClass(generatedClassInfo);
        }
    }

    private void generateClass(GeneratedClassInfo generatedClassInfo) {
        String fullClassName = generatedClassInfo.getFullClassName();

        try {
            JavaFileObject file = createSourceFile(fullClassName, generatedClassInfo);
            try (Writer writer = file.openWriter()) {
                writer.write(generatedClassInfo.getContent());
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "Generated " + fullClassName);
        } catch (FilerException _) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    "Could not generate " + fullClassName + ". The class might already exist.",
                    generatedClassInfo.origin());
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Failed to generate " + generatedClassInfo.parentClassName()
                            + ": " + e.getMessage(), generatedClassInfo.origin());
        }
    }

    protected JavaFileObject createSourceFile(String fullClassName,
                                              GeneratedClassInfo generatedClassInfo)
            throws IOException {
        return processingEnv.getFiler()
                .createSourceFile(fullClassName, generatedClassInfo.origin());
    }
}