package githubsearch;
import com.github.javaparser.ParseException;
import githubsearch.config.Config;
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
    public static void main(String[] args) throws IOException, ParseException {
        Config conf = new Config("config.properties");
        JavaFileStorage storage = new JavaFileStorage(conf.javaFileRoot, conf.javaIndexFile);
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
