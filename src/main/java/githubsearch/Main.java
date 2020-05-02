package githubsearch;
import com.github.javaparser.ParseException;
import githubsearch.crawler.FileData;
import githubsearch.crawler.JavaFileStorage;
import githubsearch.crawler.LocalFolderCrawler;
import githubsearch.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

public class Main {
    final static String javaFileRootPath = "/home/jovi/school/search/java-files"; // An empty folder where all java files will be saved.
    final static String javaFileIndexPath = "/home/jovi/school/search/java-index"; // Will create the file at this path containing the file metadatas (the folders must exist)
    public static void main(String[] args) throws IOException, ParseException {
        JavaFileStorage storage = new JavaFileStorage(javaFileRootPath, javaFileIndexPath);
        Iterator<FileData> files = storage.files();
        Log.i("Indexer", "Starting indexing");
        while(files.hasNext()) {
            FileData file = files.next();
            SymbolPackage symbols = SymbolExtractor.parse(file.metadata.url, file.source);
            Log.d("Indexer", symbols.toString());
        }

//        SymbolPackage pack = SymbolExtractor.parse("urlURLurl","package main.com.pack;\nimport something.com.dom;\nclass X { int x() { return 1 + 1.0 - 5; } }");
    // Parse some code
//    System.out.println("Package: " + pack.toString());
    }

}
