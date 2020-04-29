package githubsearch;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * A thin wrapper around the storage unit for the XRefIndex, to allow easy generalization to persistent storage.
 */
public interface XRefIndexStorage {
    // flushes is called before a storage is dereferenced.
    void flush() throws IOException;
    // storeMethodDecl stores a method declaration.
    void storeMethodDecl(MethodDecl md);
    void storeCallExpr(CallExpr ce);
    // storePackageImports should save the imports for a specific package.
    void storePackage(SymbolPackage pack);
    // packageImports should return true if the source package imports pack.
    boolean packageImports(String source, String pack);

    boolean containsCallerWithName(String methodName);
    @NotNull  List<CallExpr> getCallersByName(String methodName);

    // Resolve callee to come from the src Method declaration.
    void resolveCallExprToMethodDecl(MethodDecl src, CallExpr callee);

    // Returns method decls with a specific methodName, should never return null.
    @NotNull  List<MethodDecl> getMethodDeclsByName(String methodName);

    // Returns true if pkg declares a method with a certain name.
    boolean packageDeclaresMethodByName(String pkg, String methodName);

    // Should throw a RuntimeException if the package does not declare a method with this name.
    @NotNull MethodDecl getDeclaredMethodByName(String pkg, String methodName);

    Set<CallExpr> getCallExprsResolvingToName(String methodName);

}
