package githubsearch;

import java.io.IOException;
import java.util.*;

public class HashXRefIndexStorage implements XRefIndexStorage {
    protected HashMap<String, SymbolPackage> packages = new HashMap<>(); // Package -> package imports
    protected HashMap<String, List<MethodDecl>> methodDecls = new HashMap<>(); // name -> method decls
    protected HashMap<String, List<CallExpr>> callExprs = new HashMap<>(); // name -> call exprs
    protected HashMap<MethodDecl, Set<CallExpr>> resolvedCalls = new HashMap<>(); // Method declaration to call sites.


    @Override
    public void flush() throws IOException {
        // No need to do any cleanup.
    }
    @Override
    public void storeMethodDecl(MethodDecl md) {
        String name = md.resolvedName();
        if(!methodDecls.containsKey(name)) {
            methodDecls.put(name, new ArrayList<>());
        }
        methodDecls.get(name).add(md);
    }

    @Override
    public void storeCallExpr(CallExpr ce) {
        String name = ce.getName();
        if(!callExprs.containsKey(name)) {
            callExprs.put(name, new ArrayList<>());
        }
        callExprs.get(name).add(ce);
    }

    @Override
    public void storePackage(SymbolPackage pack) {
        if(!pack.getPackageName().isPresent()) {
            return;
        }
        packages.put(pack.getPackageName().get(), pack);
    }

    @Override
    public boolean packageImports(String source, String pack) {
        if(!packages.containsKey(source)) {
            return false;
        }
        return packages.get(source).getImports().contains(pack);
    }

    @Override
    public boolean containsCallerWithName(String methodName) {
        return callExprs.containsKey(methodName);
    }

    @Override
    public List<CallExpr> getCallersByName(String methodName) {
        List<CallExpr> callers = callExprs.get(methodName);
        if(callers == null) {
            throw new RuntimeException("Callers does not contain caller to method: " + methodName);
        }
        return new ArrayList<>(callers); // Return a copy as we might delete things from here.
    }

    @Override
    public void resolveCallExprToMethodDecl(MethodDecl src, CallExpr callee) {
        if(!resolvedCalls.containsKey(src)) {
            resolvedCalls.put(src, new HashSet<>());
        }
        resolvedCalls.get(src).add(callee);
        callExprs.get(callee.getName()).remove(callee);
    }

    @Override
    public List<MethodDecl> getMethodDeclsByName(String methodName) {
        List<MethodDecl> mds = methodDecls.get(methodName);
        if(mds == null) {
            throw new RuntimeException("Method declarations does not contain method declaration to name: " + methodName);
        }
        return new ArrayList<>(mds);
    }

    @Override
    public boolean packageDeclaresMethodByName(String pkg, String methodName) {
        if(!packages.containsKey(pkg)) {
            return false;
        }
        for(MethodDecl md : packages.get(pkg).getMethods()) {
            if(md.name.equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MethodDecl getDeclaredMethodByName(String pkg, String methodName) {
        if(!packages.containsKey(pkg)) {
            throw new RuntimeException("Package " + pkg + " does not exist in the package map.");
        }
        for(MethodDecl md : packages.get(pkg).getMethods()) {
            if(md.name.equals(methodName)) {
                return md;
            }
        }
        throw new RuntimeException("Package " + pkg + " does not declare method by name: " + methodName);
    }

    @Override
    public Set<CallExpr> getCallExprsResolvingToName(String methodName) {
        List<MethodDecl> mds = methodDecls.get(methodName);
        Set<CallExpr> ces = new HashSet<>();
        for(MethodDecl md : mds) {
            Set<CallExpr> cess = resolvedCalls.get(md);
            if(cess != null) {
                ces.addAll(cess);
            }
        }
        return ces;
    }
}
