package githubsearch;

import java.util.ArrayList;
import java.util.Optional;

public class SymbolPackage {
    public static class MethodDeclaration {
        final String name;
        final String type;
        final ArrayList<String> parents;
        public MethodDeclaration(String name, String type, ArrayList<String> parents) {
            this.name = name;
            this.type = type;
            this.parents = parents;
        }
        @Override
        public String toString() {
            return String.format("%s:%s", name, type);
        }
    }
    public static class CallExpr {
        final private String name;
        final private String type;
        CallExpr(String name, String type) {
            this.name = name;
            this.type = type;
        }
        String getName() {
            return name;
        }
        Optional<String> getType() {
            return Optional.of(type);
        }
    }
    // The package name for the compilation unit.
    private final String packageName;
    // All methods in the compilation unit.
    private final ArrayList<MethodDeclaration> methods;
    // All imports to the compilation unit.
    private final ArrayList<String> imports;
    // All function calls in the compilation unit.
    private final ArrayList<CallExpr> callExprs;
    SymbolPackage(String pkg, ArrayList<MethodDeclaration> methods, ArrayList<String> imports, ArrayList<CallExpr> callExprs) {
        this.packageName = pkg;
        this.methods = methods;
        this.imports = imports;
        this.callExprs = callExprs;
    }
    Optional<String> getPackageName() {
        return Optional.of(packageName);
    }
    ArrayList<MethodDeclaration> getMethods() {
        return methods;
    }
    ArrayList<String> getImports() {
        return imports;
    }
    ArrayList<CallExpr> getCallExprs() {
        return callExprs;
    }

    @Override
    public String toString() {
        return String.format("package: %s\nimports: %s\nmethodDecls: %s\ncallexprs: %s\n", getPackageName(), imports, methods, callExprs);
    }
}
