package githubsearch;

import com.sun.istack.internal.NotNull;

import java.util.*;

/**
 * This is an index used for finding call sites for functions.
 *
 * Important thing to note about resolving calls/declaration is that we will never have multiple packages defining the same method in a single package as it would not compile in that case.
 */
public class XRefIndex {
    private final HashMap<String, SymbolPackage> packages = new HashMap<>(); // Package name to symbols
    private final HashMap<String, List<MethodDecl>> methodDecls = new HashMap<>(); // Method declaration name to packages
    private final HashMap<String, Set<CallExpr>> callers = new HashMap<>(); // Method caller name to packages.
    private final HashMap<MethodDecl, Set<CallExpr>> resolvedCalls = new HashMap<>(); // Method declaration to call sites.

    private XRefIndexStorage storage;
    public XRefIndex(@NotNull XRefIndexStorage storage) {
        this.storage = storage;
    }
    // Returns all call sites for functions named methodName.
    public Set<CallExpr> getCallSites(String methodName) {
        return storage.getCallExprsResolvingToName(methodName);
        /*List<MethodDecl> mds = methodDecls.get(methodName);
        Set<CallExpr> ces = new HashSet<>();
        for(MethodDecl md : mds) {
            Set<CallExpr> cess = resolvedCalls.get(md);
            if(cess != null) {
                ces.addAll(cess);
            }
        }
        return ces;
*/    }
    // Resolves all symbols in the SymbolPackage and adds them to the index.
    // The SymbolPackage must have a package name, else nothing is resolved. FIXME: Add this as a limitation in the report.
    public void resolveSymbols(SymbolPackage pack) {
        if(!pack.getPackageName().isPresent()) {
            return;
        }
        storage.storePackageImports(pack);
//        packages.put(pack.getPackageName().get(), pack);
/*        for(MethodDecl md : pack.getMethods()) {
            if(!methodDecls.containsKey(md.name)) {
                methodDecls.put(md.name, new ArrayList<>());
            }
            methodDecls.get(md.name).add(md);
        }
 */
        for(MethodDecl md : pack.getMethods()) {
            storage.storeMethodDecl(md);
        }
        for(CallExpr ce : pack.getCallExprs()) {
            storage.storeCallExpr(ce);
        }
        /*
        for(CallExpr ce : pack.getCallExprs()) {
            if(!callers.containsKey(ce.getName())) {
                callers.put(ce.getName(), new HashSet<>());
            }
            callers.get(ce.getName()).add(ce);
        }
*/
        for(MethodDecl md : pack.getMethods()) {
            // Need to resolve any potential calls that haven't been resolved.
            resolveMethodDecl(pack.getPackageName().get(), md);
        }
        for(CallExpr ce : pack.getCallExprs()) {
            // Need to resolve to the correct method decl.
            resolveCallExpr(pack, ce);
        }
    }

    private void resolveCallExpr(SymbolPackage pack, CallExpr ce) {
        //List<MethodDecl> methodDecls = storage.getMethodDeclsByName(ce.getName());
        ArrayList<String> relevantPackages = pack.getImports();
        if(pack.getPackageName().isPresent()) {
            // This call expr could also be defined in this "local" package.
            relevantPackages.add(pack.getPackageName().get());
        }
        // Are any of these packages part of the package import?
        for(String imp : pack.getImports()) {
            // Check if this import is one of the packages declaring this method.
            // FIXME: Make this search faster than linear (O(N) might be OK here though...
            if(storage.packageDeclaresMethodByName(imp, ce.getName())) {
                storage.resolveCallExprToMethodDecl(storage.getDeclaredMethodByName(imp, ce.getName()), ce);
                return;
            }
        }

    }

/*    private void resolveCallExpr(SymbolPackage pack, CallExpr ce) {
        List<MethodDecl> methodDecls = this.methodDecls.get(ce.getName());
        if(methodDecls == null) {
            // Can't resolve this CallExpr as there isn't a method declared that we have found.
            return;
        }
        ArrayList<String> relevantPackages = pack.getImports();
        if(pack.getPackageName().isPresent()) {
            // This call expr could also be defined in this "local" package.
            relevantPackages.add(pack.getPackageName().get());
        }
        // Are any of these packages part of the package import?
        for(String imp : pack.getImports()) {
            // Check if this import is one of the packages declaring this method.
            // FIXME: Make this search faster than linear (O(N) might be OK here though...
            for(MethodDecl md : methodDecls) {
                if(!md.loc.getPackage().isPresent()) {
                    // FIXME: This should never happen due to the check above. Maybe log/throw an error?
                    continue;
                }
                if(md.loc.getPackage().get().equals(imp)) {
                    // This is the decl we are looking for!
                    // FIXME: So resolve this decl!
                    if(!resolvedCalls.containsKey(md)) {
                        resolvedCalls.put(md, new HashSet<>());
                    }
                    resolvedCalls.get(md).add(ce);
                    callers.get(md.resolvedName()).remove(ce);
                    return;
                }
            }
        }
    }
 */
    private void resolveMethodDecl(String pkg, MethodDecl md) {
        if(!storage.containsCallerWithName(md.name)) {
            // No one has referenced this method declaration.
            return;
        }
        ArrayList<CallExpr> toDelete = new ArrayList<>();
        for(CallExpr call : storage.getCallersByName(md.resolvedName())) {
            if(!call.getSourceLocation().getPackage().isPresent()) {
                // FIXME: This should never happen, maybe throw/log an error?
                continue;
            }
            String callPkg = call.getSourceLocation().getPackage().get();
            if(callPkg.equals(pkg) || storage.packageImports(callPkg, pkg)) {
                storage.resolveCallExprToMethodDecl(md, call);
            }
        }
    }

/*    private void resolveMethodDecl(String pkg, MethodDecl md) {
        // We have found md.
        if(!callers.containsKey(md.name)) {
            // No one has referenced this method declaration.
            return;
        }
        ArrayList<CallExpr> toDelete = new ArrayList<>();
        // There are packages that call a method by this name. Check if it is from the current package.
        for(CallExpr call : callers.get(md.resolvedName())) {
            if(!call.getSourceLocation().getPackage().isPresent() || !call.getSourceLocation().getPackage().isPresent()) {
                // FIXME: This should never happen due to the check above. Maybe log/throw an error?
                continue;
            }

            String callPkg = call.getSourceLocation().getPackage().get();
            if(callPkg.equals(pkg) || packages.get(callPkg).getImports().contains(pkg)) { // storage.packageImports(callPkg, pkg)) {
                // This is either pkg or a package importing pkg. But this symbol is visible in packageName.
                if(!resolvedCalls.containsKey(md)) {
                    resolvedCalls.put(md, new HashSet<>());
                }
                resolvedCalls.get(md).add(call);
                toDelete.add(call);
            }
        }
        for(CallExpr call : toDelete) {
            // Remove the resolved callers.
            callers.get(md.resolvedName()).remove(call);
        }
    }*/
}
