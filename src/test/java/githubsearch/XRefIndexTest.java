package githubsearch;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class XRefIndexTest {
    SymbolPackage p1() {
        String pkg = "java.util";
        return new SymbolPackage(
                pkg,
                new ArrayList<MethodDecl>(Arrays.asList(
                        new MethodDecl("foo", "int", new SourceLocation("java.util", "", 10, 20), new ArrayList<>()),
                        new MethodDecl("goo", "void", new SourceLocation("java.util", "", 10, 20), new ArrayList<>())
                )),
                new ArrayList<>(),
                new ArrayList<CallExpr>(Arrays.asList(
                        new CallExpr("foo", null, new SourceLocation("java.util", "", 10, 15))
                ))
        );
    }

    void callExprMatchPackages(String[] expectedPackages, Set<CallExpr> actual) {
        List<String> actualPackages = new ArrayList<>();
        for(CallExpr ce : actual) {
            if(!ce.getSourceLocation().getPackage().isPresent()) {
                // FIXME: This should never happen.
                throw new RuntimeException("A CallExpr did not have a package");
            }
            actualPackages.add(ce.getSourceLocation().getPackage().get());
        }
        for(String pkg : expectedPackages) {
            boolean found = actualPackages.contains(pkg);
            if(!found) {
                System.out.println("Expected packages: " + Arrays.toString(expectedPackages) + ", actual packages: " + actualPackages);
            }
            Assert.assertTrue(found);
        }
    }

    @org.junit.jupiter.api.Test
    void resolveSymbolsSimpleTest() {
        XRefIndex index = new XRefIndex();
        index.resolveSymbols(p1());
        callExprMatchPackages(new String[]{"java.util"}, index.getCallSites("foo"));
    }
}