package githubsearch;

import java.util.ArrayList;
import java.util.Optional;

public class SymbolPackage {
    // The package name for the compilation unit.
    private final String packageName;
    // All methods in the compilation unit.
    private final ArrayList<MethodDecl> methods;
    // All imports to the compilation unit.
    private final ArrayList<String> imports;
    // All function calls in the compilation unit.
    private final ArrayList<CallExpr> callExprs;
    // All classes or interfaces that are declared in the compilation unit.
    private final ArrayList<ClassDecl> classDecls;
    SymbolPackage(String pkg, ArrayList<MethodDecl> methods, ArrayList<String> imports, ArrayList<CallExpr> callExprs, ArrayList<ClassDecl> classDecls) {
        this.packageName = pkg;
        this.methods = methods;
        this.imports = imports;
        this.callExprs = callExprs;
        this.classDecls = classDecls;
    }
    public Optional<String> getPackageName() {
        if(packageName == null) {
            return Optional.empty();
        }
        return Optional.of(packageName);
    }
    ArrayList<MethodDecl> getMethods() {
        return methods;
    }
    ArrayList<String> getImports() {
        return imports;
    }
    ArrayList<CallExpr> getCallExprs() {
        return callExprs;
    }
    ArrayList<ClassDecl> getClassDecls() {
        return classDecls;
    }

    @Override
    public String toString() {
        return String.format("package: %s\nimports: %s\nmethodDecls: %s\ncallexprs: %s\n", getPackageName(), imports, methods, callExprs);
    }
}
