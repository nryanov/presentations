package processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.AbstractTypeVisitor14;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceMethodsVisitor extends AbstractTypeVisitor14<Void, Void> {
    private final Messager messager;
    private final Map<DeclaredType, List<ExecutableElement>> executables;

    public InterfaceMethodsVisitor(Messager messager) {
        this.messager = messager;
        executables = new HashMap<>();
    }

    public Map<DeclaredType, List<ExecutableElement>> getExecutables() {
        return Collections.unmodifiableMap(executables);
    }

    @Override
    public Void visitIntersection(IntersectionType t, Void unused) {
        return null;
    }

    @Override
    public Void visitUnion(UnionType t, Void unused) {
        return null;
    }

    @Override
    public Void visitPrimitive(PrimitiveType t, Void unused) {
        return null;
    }

    @Override
    public Void visitNull(NullType t, Void unused) {
        return null;
    }

    @Override
    public Void visitArray(ArrayType t, Void unused) {
        return null;
    }

    @Override
    public Void visitDeclared(DeclaredType t, Void unused) {
        executables.putIfAbsent(t, new ArrayList<>());

        var executablesList = executables.get(t);

        t.asElement().getEnclosedElements().forEach(el -> {
            var executable = (ExecutableElement) el;
            executablesList.add(executable);

        });

        return null;
    }

    @Override
    public Void visitError(ErrorType t, Void unused) {
        return null;
    }

    @Override
    public Void visitTypeVariable(TypeVariable t, Void unused) {
        return null;
    }

    @Override
    public Void visitWildcard(WildcardType t, Void unused) {
        return null;
    }

    @Override
    public Void visitExecutable(ExecutableType t, Void unused) {
        return null;
    }

    @Override
    public Void visitNoType(NoType t, Void unused) {
        return null;
    }
}
