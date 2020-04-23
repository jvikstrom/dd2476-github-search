package githubsearch;
import java.lang.String;

public class Main {
    public static void main(String[] args) {
    SymbolPackage pack = SymbolExtractor.parse("urlURLurl","package main.com.pack;\nimport something.com.dom;\nclass X { int x() { return 1 + 1.0 - 5; } }");
    // Parse some code
    System.out.println("Package: " + pack.toString());
}

}
