package githubsearch;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import javassist.compiler.ast.CallExpr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class SymbolExtractor {
    // Used to keep track of if we've set the configuration for the static JavaParser.
    private final static SymbolExtractor extractor = new SymbolExtractor();
    private SymbolExtractor() {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
    }
    // getParents returns all parent classes or interface recursively.
    private static ArrayList<String> getParents(Optional<Node> decl) {
        if(!decl.isPresent()) {
            return new ArrayList<>();
        }
        Node n = decl.get();
        if(n instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration)n;
            ArrayList<String> ret = new ArrayList<>(Collections.singleton(cid.getNameAsString()));
            ret.addAll(getParents(cid.getParentNode()));
            return ret;
        }
        return new ArrayList<>();
    }
    // parse Parses the sourceCode and returns relevant symbols.
    static SymbolPackage parse(String sourceCode) {
        if(extractor == null) {
            throw new RuntimeException("Extractor has not been initialized.");
        }
        CompilationUnit cu = StaticJavaParser.parse(sourceCode);
        String packageName = null;
        if(cu.getPackageDeclaration().isPresent()) {
            packageName = cu.getPackageDeclaration().get().getNameAsString();
        }
        ArrayList<SymbolPackage.MethodDeclaration> decls = new ArrayList<>();
        cu.findAll(MethodDeclaration.class).forEach(md -> {
            ArrayList<String> parents = getParents(md.getParentNode());
            decls.add(new SymbolPackage.MethodDeclaration(md.getNameAsString(), md.getTypeAsString(), parents));
        });
        ArrayList<String> imports = new ArrayList<>();
        cu.findAll(ImportDeclaration.class).forEach(id -> {
            imports.add(id.getNameAsString());
        });
        ArrayList<SymbolPackage.CallExpr> callers = new ArrayList<>();
        cu.findAll(MethodCallExpr.class).forEach(ce -> {
            callers.add(new SymbolPackage.CallExpr(ce.getNameAsString(), null));
        });
        return new SymbolPackage(packageName, decls, imports, callers);
    }
}